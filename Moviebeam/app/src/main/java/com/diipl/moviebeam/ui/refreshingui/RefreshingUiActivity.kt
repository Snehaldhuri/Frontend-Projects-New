package com.diipl.moviebeam.ui.refreshingui

import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.android.tv.settings.aidl.regular.IDeviceNameConfigureCallback
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityRefreshingUiBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.handler.ClearCredentialsHandler
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guest.message.GuestMessageActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import com.diipl.moviebeam.worker.UpdateDataWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RefreshingUiActivity"

@AndroidEntryPoint
class RefreshingUiActivity : BaseActivity() {

    private var kapingResponse: KapingResponse? = null

    //Variables from datastore
    private var accountId = ""
    private var epgCdnUrl = ""
    private var ua = ""
    private var appList = ArrayList<String>()

    private lateinit var binding: ActivityRefreshingUiBinding
    private val refreshingUiViewModel: RefreshingUiViewModel by viewModels()
    private val workManager: WorkManager by lazy { WorkManager.getInstance(applicationContext) }

    private var isEPGServerApiCalled = false

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var hardwareAPI: HardwareAPI
    private val clearCredentialsHandler: ClearCredentialsHandler by lazy {
        ClearCredentialsHandler(
            applicationContext,
            accountSetupDataStore
        )
    }
    private val epgHandler by lazy { EPGHandler(this, roomRepository) }

    override fun observeViewModel() {
        observe(refreshingUiViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(refreshingUiViewModel.themeLiveData, ::handleThemeResponse)
        observe(refreshingUiViewModel.hotelServiceLiveData, ::handleHotelServicesResponse)
        observe(refreshingUiViewModel.localAttractionLiveData, ::handleLocalAttractionResponse)
        observe(refreshingUiViewModel.moviesLiveData, ::handleMoviesResponse)
        observe(refreshingUiViewModel.showtimeLiveData, ::handleShowtimeResponse)
        observe(refreshingUiViewModel.channelListLiveData, ::handleChannelListResponse)
        observe(refreshingUiViewModel.epgLiveData, ::handleEPGDataResponse)
        observe(refreshingUiViewModel.guestMessageLiveData, ::handleGuestMessageResponse)
    }

    override fun initViewBinding() {
        binding = ActivityRefreshingUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.initializeDatastoreParams()

        val data = intent.getStringExtra("response")
        data?.let {
            kapingResponse = it.fromJson<KapingResponse>()
        }

        binding.btnOk.handleFocusChange()

        binding.btnOk.setOnClickListener {
            redirectToScreen()
        }
        binding.root.postDelayed({
            this.handleKaping(kapingResponse)
        }, 1000 * 5)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun redirectToScreen() {
        lifecycleScope.launch {
            val list =
                accountSetupDataStore.data.first().buttonsList.filter { it.buttonName == Constants.MAIN_GUEST_MSG_ID }
            if (list.isNotEmpty()) {
                val intent = Intent(applicationContext, GuestMessageActivity::class.java)
                intent.putExtra("btnId", Constants.MAIN_GUEST_MSG_ID)
                startActivity(intent)
            } else {
                val intent = Intent(applicationContext, GuestServiceActivity::class.java)
                intent.putExtra("btnId", Constants.MESSAGE_ID)
                startActivity(intent)
            }
        }
    }

    private fun handleKaping(kapingResponse: KapingResponse?) {
        Log.e(TAG, "handleKaping: ${kapingResponse.toJson()}")
        when (kapingResponse?.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> {
                handleAccountActivateCmd()
            }

            KapingConstants.KAP_CMD_CHECK_IN -> {
                handleCheckInCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> {
                handleCheckOutCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                handleThemeChangeCmd()
            }

            KapingConstants.KAP_CMD_HS_CHANGE -> {
                handleHsChangeCmd()
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                handleLAChangeCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                handleFetchSyncListCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                handleFetchShowtimeCmd()
            }

            KapingConstants.KAP_CMD_GET_CHANNEL_LIST -> {
                handleFetchChannelListCmd()
            }

            KapingConstants.KAP_CMD_GET_EPG_DATA -> {
                handleGetEPGDataCmd()
            }

            KapingConstants.KAP_CMD_GET_GUEST_MESSAGES -> {
                handleGuestMessageCmd()
            }
        }

    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        logD("Signal for check out command")
        refreshingUiViewModel.updateGuestMessage(
            MessageResponse()
        )
        refreshingUiViewModel.updateGuestSession(
            false,
            kapingResponse.cmdData?.cmdData
        )
        GuestDetails.IS_GUEST_CHECKED_IN = false
//        clearCredentials(appList)
        clearCredentialsHandler.startClearCredentials(false)
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        redirectToMainMenuScreen()
    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        logD("Signal for check in command")
        refreshingUiViewModel.updateGuestSession(
            true,
            kapingResponse.cmdData?.cmdData
        )
        GuestDetails.IS_GUEST_CHECKED_IN = true
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        redirectToMainMenuScreen()
    }

    private fun handleAccountActivateCmd() {
        logD("Signalling to get account set up")
        refreshingUiViewModel.fetchAccountSetupDetails(
            Constants.ACTIVATE,
            ua,
            Constants.MODE
        )
    }

    private fun handleThemeChangeCmd() {
        refreshingUiViewModel.fetchThemeDetails(ua)
    }

    private fun handleHsChangeCmd() {
        refreshingUiViewModel.fetchHotelServiceInfo(accountId)
    }

    private fun handleLAChangeCmd() {
        refreshingUiViewModel.fetchLocalAttractionInfo(ua)
    }

    private fun handleFetchSyncListCmd() {
        refreshingUiViewModel.fetchSyncList(ua)
    }

    private fun handleFetchShowtimeCmd() {
        refreshingUiViewModel.fetchShowtimeData(ua)
    }

    private fun handleFetchChannelListCmd() {
        refreshingUiViewModel.handleFetchChannelListCmd(ua)
    }

    private fun handleGuestMessageCmd() {
        if (GuestDetails.IS_GUEST_CHECKED_IN) {
            refreshingUiViewModel.fetchGuestMessage(ua, GuestDetails.SESSION_ID)
        }
    }

    private fun handleGetEPGDataCmd() {
        logD("Signal for get EPG Data Cmd")
        refreshingUiViewModel.getChannelList(channelListDataStore)
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    refreshingUiViewModel.setAccountSetupResponseData(it)

                    if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_STB) {
                        hardwareAPI.myService?.setDeviceName(
                            "MBAP_${it.accountId}_${it.roomNo}",
                            object : IDeviceNameConfigureCallback {
                                @Throws(RemoteException::class)
                                override fun onDeviceNameConfigureCallback(s: String) {
                                    Log.e("TAG", "onDeviceNameConfigureCallback: setDeviceName $s")
                                }

                                override fun asBinder(): IBinder? {
                                    return null
                                }
                            }
                        )
                    }

                    scheduleClearCredentialsTask(it.checkOutTime)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    logD("AccountSetup callback Success ")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In AccountSetup callback fail")
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.themeLiveData.value?.data?.let {
                    refreshingUiViewModel.setThemeResponseData(it)
                    preferenceHandler.updateDatastoreVariables(
                        gradientStartColor = it.gradientColor,
                        gradientEndColor = it.spotLightColor
                    )
                    ThemeDetails.GRADIENT_COLOR_START = it.gradientColor
                    ThemeDetails.GRADIENT_COLOR_END = it.spotLightColor
                    ThemeDetails.GRADIENT = null
                    ThemeDetails.GRADIENT = getGradientColor()
                    ThemeDetails.BG_IMAGE = it.themeBackgroundFileName
                    ThemeDetails.LOGO_IMAGE = it.themeLogoFileName
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    startUpdateDataWorker(UpdateDataWorker.ACTION_THEME)
                    logD("In Theme callback Success")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Theme Callback fail")
            }
        }
    }

    private fun handleHotelServicesResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.hotelServiceLiveData.value?.data?.let {
                    refreshingUiViewModel.setHotelServicesResponseData(it)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    startUpdateDataWorker(UpdateDataWorker.ACTION_HS)
                    logD("In Hotel Services callback Success")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Hotel Services callback fail")
            }
        }
    }

    private fun handleLocalAttractionResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.localAttractionLiveData.value?.data?.let {
                    refreshingUiViewModel.setLocalAttractionResponseData(
                        it
                    )
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    startUpdateDataWorker(UpdateDataWorker.ACTION_LA)
                    logD("In Local Attractions callback Success ")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Local Attractions callback fail ")
            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    refreshingUiViewModel.updateSyncList(it)
                    preferenceHandler.updateDatastoreVariables(
                        moviesCount = it.freeContentList.size.plus(it.premiumContentList.size),
                        cListVersion = it.version
                    )
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    logD("In Movies callback Success ")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Movies callback fail ")
            }
        }
    }

    private fun handleShowtimeResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.showtimeLiveData.value?.data?.let {
                    preferenceHandler.updateDatastoreVariables(showsCount = it.shoContentList.size)
                    refreshingUiViewModel.updateShowtimeData(it)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    logD("In Showtime callback Success ")
                    redirectToMainMenuScreen()
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Showtime callback fail ")
            }
        }
    }

    private fun handleChannelListResponse(status: Resource<ChannelListResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.channelListLiveData.value?.data?.let {
                    if (EndlessService.kapingCMD == KapingConstants.KAP_CMD_GET_CHANNEL_LIST) {
                        refreshingUiViewModel.updateChannelList(it)
                        EndlessService.kapingCmdExecutionResponse =
                            KapingConstants.EXECUTED_SUCCESSFULLY
                        preferenceHandler.updateDatastoreVariables(channelCount = it.channelLcnList.size)
                        logD("In Channel List callback Success ")
                        redirectToMainMenuScreen()
                    } else {
                        refreshingUiViewModel.fetchEPGData(epgCdnUrl)
                    }
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                if (EndlessService.kapingCMD == KapingConstants.KAP_CMD_GET_CHANNEL_LIST)
                    logE("In Channel List callback fail ")
            }
        }
    }

    private fun handleEPGDataResponse(status: Resource<EPGResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    refreshingUiViewModel.channelListLiveData.value?.data?.channelLcnList?.let { list ->
                        epgHandler.parseEPG(it, list)
                        processEPGData()
                    }
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Get EPG data callback fail ")
            }
        }
    }

    private fun handleGuestMessageResponse(status: Resource<MessageResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    refreshingUiViewModel.updateGuestMessage(it)
                    binding.tvInfo.text = getString(R.string.you_have_a_new_message)
                    binding.tvMsg.text = it.messagesList?.find {
                        it.isRead == 0
                    }?.messageSubject
                    binding.tvTxt.toGone()
                    binding.btnOk.toVisible()
                    binding.btnOk.requestFocus()
                    logD("In Get Guest Message callback success ")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                logE("In Get Guest Message callback fail ")
            }
        }
    }

    private fun processEPGData() {
        epgHandler.epgStatus.observe(this) {
            when (it) {
                EPGHandler.STATUS_FAIL -> {
                    if (!isEPGServerApiCalled) {
                        refreshingUiViewModel.fetchEPGDataFromServer(ua)
                        isEPGServerApiCalled = true
                    } else {
                        isEPGServerApiCalled = false
                        redirectToMainMenuScreen()
                    }
                }

                EPGHandler.STATUS_OK -> {
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    logD("In Get EPG data callback Success ")
                    redirectToMainMenuScreen()
                }

            }
        }
    }

    private fun startUpdateDataWorker(action: String) {
        val inputData = Data.Builder()
            .putString(UpdateDataWorker.ACTION, action)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdateDataWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
    }

    private fun redirectToMainMenuScreen() = lifecycleScope.launch {
        delay(1000 * 5)
        val i = Intent(applicationContext, MainMenuActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    private fun initializeDatastoreParams() =
        lifecycleScope.launch {
            accountId = preferenceHandler.accountID
            epgCdnUrl = preferenceHandler.epgCDNUrl
            ua = preferenceHandler.UA
            appList = preferenceHandler.appList

        }

}