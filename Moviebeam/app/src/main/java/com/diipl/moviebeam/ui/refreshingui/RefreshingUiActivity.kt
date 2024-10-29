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
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityRefreshingUiBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.PreferenceHandler
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guest.message.GuestMessageActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.ClearCredentialsHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInteger
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import com.diipl.moviebeam.worker.UpdateDataWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

private const val TAG = "RefreshingUiActivity"

@AndroidEntryPoint
class RefreshingUiActivity : BaseActivity() {

    private var kapingResponse: KapingResponse? = null

    //Variables from datastore
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var accountId = ""
    private var epgCdnUrl = ""
    private var ua = ""
    private var appList = ArrayList<String>()

    private lateinit var binding: ActivityRefreshingUiBinding
    private val refreshingUiViewModel: RefreshingUiViewModel by viewModels()
    private val workManager: WorkManager by lazy { WorkManager.getInstance(applicationContext) }

    private var isEPGServerApiCalled = false

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var hardwareAPI: HardwareAPI
    private val clearCredentialsHandler : ClearCredentialsHandler by lazy { ClearCredentialsHandler(applicationContext, accountSetupDataStore) }

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

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
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
        }, 1000*5)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun redirectToScreen() {
        lifecycleScope.launch {
            val list = accountSetupDataStore.data.first().buttonsList.filter { it.buttonName == Constants.MAIN_GUEST_MSG_ID }
            if (list.isNotEmpty()){
                val intent = Intent(applicationContext,GuestMessageActivity::class.java)
                intent.putExtra("btnId",Constants.MAIN_GUEST_MSG_ID)
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
                    if(BuildConfig.BUILD_TYPE==Constants.BUILD_TYPE_STB) {
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
                    refreshingUiViewModel.setAccountSetupResponseData(it)
                    preferenceHandler.updateAccountData(it)

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
                refreshingUiViewModel.epgLiveData.value?.data?.let {
                    val simpleDateFormatter =
                        SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
                    if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                        processEPGData(it)
                        EndlessService.kapingCmdExecutionResponse =
                            KapingConstants.EXECUTED_SUCCESSFULLY
                        logD("In Get EPG data callback Success ")
                        redirectToMainMenuScreen()
                    } else {
                        if (!isEPGServerApiCalled) {
                            refreshingUiViewModel.fetchEPGDataFromServer(ua)
                            isEPGServerApiCalled = true
                        } else {
                            isEPGServerApiCalled = false
                            redirectToMainMenuScreen()
                        }
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

    private fun processEPGData(epgResponse: EPGResponse) {
        //Removing all Epg Channels From RoomDB.
        lifecycleScope.launch(Dispatchers.IO) {
            roomRepository.removeAllChannels()

            val simpleDateFormatter = SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
            epgResponse.let {
                if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                    preferenceHandler.updateDatastoreVariables(epgStartTime = it.ST, epgEndTime = it.ET)
                    val channelList =
                        refreshingUiViewModel.channelListLiveData.value?.data?.channelLcnList
                    val currentKey = fetchCurrentProgramKey()
                    removeEarlierData(it.epgListMap?.entries?.iterator(), currentKey)
                    for (entries in it.epgListMap?.entries!!) {
                        val iterator = entries.value.iterator()
                        val key = entries.key
                        val ciMap = HashMap<Int, Boolean>()
                        while (iterator.hasNext()) {
                            val channel = iterator.next()
                            channel.key = key
                            if (channel.CI != null) {
                                var isFound = false
                                for (channelApi in channelList!!) {
                                    if (channelApi.CI == channel.CI) {
                                        isFound = true
                                        //Mapping EpgMap with Channel List Api
                                        channel.AR = channelApi.AR
                                        channel.CN = channelApi.CN
                                        channel.CNO = channelApi.CNO
                                        channel.CBT = channelApi.CBT
                                        channel.CL = channelApi.CL
                                        channel.CLCloud = channelApi.CLCloud
                                        if (channelApi.httpStreaming == true)
                                            channel.VP = channelApi.httpStreamingUrl
                                        else
                                            channel.VP = channelApi.VP
                                        channel.param1 = channelApi.param1
                                        channel.param2 = channelApi.param2
                                        channel.httpStreamingUrl = channelApi.httpStreamingUrl
                                        channel.httpStreaming = channelApi.httpStreaming
                                        channel.recordable = channelApi.recordable

                                        channel.channelNameNo =
                                            "${channelApi.CNO}   ${channelApi.CN}"
                                        channel.lastProg = channel.C
                                        channel.prog1Time =
                                            "${channel.P1_ST} - ${channel.P1_ET}"

                                        //Mapping EpgMap with Program Map Api
                                        if (channel.P1_ID != null) {
                                            val program1 =
                                                it.programsListMap?.get(channel.P1_ID)
                                            if (program1 != null) {
                                                channel.P1_PT = program1.PT
                                                channel.P1_SY = program1.SY
                                                channel.progInfo = program1.PT
                                                channel.progSynopsis = program1.SY
                                                channel.liveProg1 = program1.PT
                                                channel.progInfo1 =
                                                    "${channel.CNO} - ${program1.PT}"
                                            } else {
                                                channel.P1_PT =
                                                    Constants.NO_INFORMATION_AVAILABLE
                                                channel.P1_SY =
                                                    Constants.NO_INFORMATION_AVAILABLE
                                                channel.progInfo =
                                                    Constants.NO_INFORMATION_AVAILABLE
                                                channel.progSynopsis =
                                                    Constants.NO_INFORMATION_AVAILABLE
                                                channel.liveProg1 =
                                                    Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        // for live tv and full screen (Next)
                                        if (channel.C?.toInteger()!! > 1) {
                                            if (channel.P2_ID != null) {
                                                val program2 =
                                                    it.programsListMap?.get(channel.P2_ID)
                                                channel.P2_PT = program2?.PT
                                                channel.P2_SY = program2?.SY
                                                channel.liveProg2 = program2?.PT
                                                channel.progInfo2 =
                                                    "${channel.CNO} - ${program2?.PT}"
                                                channel.prog2Time =
                                                    "${channel.P2_ST} - ${channel.P2_ET}"
                                            }
                                            if (channel.P3_ID != null) {
                                                val program3 =
                                                    it.programsListMap?.get(channel.P3_ID)
                                                channel.P3_PT = program3?.PT
                                                channel.P3_SY = program3?.SY
                                            }
                                            if (channel.P4_ID != null) {
                                                val program4 =
                                                    it.programsListMap?.get(channel.P4_ID)
                                                channel.P4_PT = program4?.PT
                                                channel.P4_SY = program4?.SY
                                            }
                                            if (channel.P5_ID != null) {
                                                val program5 =
                                                    it.programsListMap?.get(channel.P5_ID)
                                                channel.P5_PT = program5?.PT
                                                channel.P5_SY = program5?.SY
                                            }
                                            if (channel.P6_ID != null) {
                                                val program6 =
                                                    it.programsListMap?.get(channel.P6_ID)
                                                channel.P6_PT = program6?.PT
                                                channel.P6_SY = program6?.SY
                                            }
                                            if (channel.P7_ID != null) {
                                                val program7 =
                                                    it.programsListMap?.get(channel.P7_ID)
                                                channel.P7_PT = program7?.PT
                                                channel.P7_SY = program7?.SY
                                            }
                                            if (channel.P8_ID != null) {
                                                val program8 =
                                                    it.programsListMap?.get(channel.P8_ID)
                                                channel.P8_PT = program8?.PT
                                                channel.P8_SY = program8?.SY
                                            }
                                        } else {
                                            //Calculating next Program Time from program1 end Time when Only One Program is Available
                                            val nextProgramTime = Calendar.getInstance()
                                            nextProgramTime.time =
                                                simpleDateFormatter.parse(channel.P1_DET)
                                            val nextProgramKey =
                                                fetchCurrentProgramKey(nextProgramTime)
                                            val nextProgram: ChannelEpgDTO? =
                                                it.epgListMap[nextProgramKey]?.first {
                                                    it.CI == channelApi.CI
                                                }
                                            when (nextProgramTime.get(Calendar.MINUTE)) {
                                                0, 30 -> {
                                                    channel.prog2Time =
                                                        "${nextProgram?.P1_ST} - ${channel.P1_ET}"
                                                    channel.liveProg2 =
                                                        it.programsListMap?.get(nextProgram?.P1_ID)?.PT
                                                }

                                                else -> {
                                                    channel.prog2Time =
                                                        "${channel.P2_ST} - ${channel.P2_ET}"
                                                    channel.liveProg2 =
                                                        it.programsListMap?.get(nextProgram?.P2_ID)?.PT
                                                }
                                            }
                                        }
                                        break
                                    }
                                }
                                if (!isFound)
                                    iterator.remove()
                                else {
                                    //Removing Duplicate Channels
                                    if (ciMap[channel.CI] != null)
                                        iterator.remove()
                                    else
                                        ciMap[channel.CI] = true
                                }
                            }
                        }
                        //Sorting Channels by Channel No
                        entries.value.sortBy { it.CNO?.toInteger() }
                        //Adding Channels to RoomDB.
                        roomRepository.insertChannels(entries.value)
                    }
                } else {
                    //TODO EPG DATA INVALID
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

    private fun redirectToMainMenuScreen() {
        val i = Intent(this, MainMenuActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            accountId = getAccountId()
            epgCdnUrl = getEpgCdUrl()
            ua = getUa()
            appList = ArrayList(getAppList())
        }
    }

    private suspend fun getAccountId(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
            ""
        )
    }

    private suspend fun getEpgCdUrl(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.EPG_CDN_URL_KEY,
            ""
        )
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    private suspend fun getAppList(): Set<String> {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.APP_LIST_KEY,
            emptySet()
        )
    }

}