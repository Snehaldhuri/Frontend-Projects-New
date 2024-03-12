package com.diipl.moviebeam.ui.refreshingui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityRefreshingUiBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.kappingservice.EndlessService
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "RefreshingUiActivity"

@AndroidEntryPoint
class RefreshingUiActivity : BaseActivity() {

    private val TAG = this::class.java.simpleName

    private var kapingResponse: KapingResponse? = null

    private lateinit var binding: ActivityRefreshingUiBinding
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private val refreshingUiViewModel: RefreshingUiViewModel by viewModels()

    private var isEPGServerApiCalled = false

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var showTimeDataStore: DataStore<ShowTimeResponse>

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    override fun observeViewModel() {
        observe(refreshingUiViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(refreshingUiViewModel.themeLiveData, ::handleThemeResponse)
        observe(refreshingUiViewModel.hotelServiceLiveData, ::handleHotelServicesResponse)
        observe(refreshingUiViewModel.localAttractionLiveData, ::handleLocalAttractionResponse)
        observe(refreshingUiViewModel.moviesLiveData, ::handleMoviesResponse)
        observe(refreshingUiViewModel.showtimeLiveData, ::handleShowtimeResponse)
        observe(refreshingUiViewModel.channelListLiveData, ::handleChannelListResponse)
        observe(refreshingUiViewModel.epgLiveData, ::handleEPGDataResponse)
    }

    override fun initViewBinding() {
        binding = ActivityRefreshingUiBinding.inflate(layoutInflater)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        setContentView(binding.root)

        kapingResponse = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("response", KapingResponse::class.java)
        } else {
            intent.getParcelableExtra("response")
        }
        binding.root.postDelayed({
            this.handleKaping(kapingResponse)
        }, 5000)
    }

    private fun handleKaping(kapingResponse: KapingResponse?) {
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

            KapingConstants.KAP_CMD_REBOOT -> {
//                reboot()
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
        }

    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        LoggingService.sendMessageToWebSocket(
            "Signal for check out command",
            getCurrentPanelNumber()
        )
        refreshingUiViewModel.updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            false,
            kapingResponse.cmdData?.cmdData
        )
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        redirectToMainMenuScreen()

    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        LoggingService.sendMessageToWebSocket(
            "Signal for check in command",
            getCurrentPanelNumber()
        )
        refreshingUiViewModel.updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            true,
            kapingResponse.cmdData?.cmdData
        )
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        redirectToMainMenuScreen()

    }

    private fun handleAccountActivateCmd() {
        LoggingService.sendMessageToWebSocket(
            "Signalling to get account set up",
            getCurrentPanelNumber()
        )
        refreshingUiViewModel.fetchAccountSetupDetails(
            Constants.ACTIVATE,
            Constants.UA,
            Constants.MODE
        )

    }

    private fun handleThemeChangeCmd() {
        refreshingUiViewModel.fetchThemeDetails(Constants.UA)
    }

    private fun handleHsChangeCmd() {
        refreshingUiViewModel.fetchHotelServiceInfo(Constants.ACCOUNT_ID)
    }

    private fun handleLAChangeCmd() {
        refreshingUiViewModel.fetchLocalAttractionInfo(Constants.UA)
    }

    private fun handleFetchSyncListCmd() {
        refreshingUiViewModel.fetchSyncList(Constants.UA)
    }

    private fun handleFetchShowtimeCmd() {
        refreshingUiViewModel.fetchShowtimeData(Constants.UA)
    }

    private fun handleFetchChannelListCmd() {
        refreshingUiViewModel.handleFetchChannelListCmd(Constants.UA)
    }

    private fun handleGetEPGDataCmd() {
        LoggingService.sendMessageToWebSocket(
            "Signal for check out command",
            getCurrentPanelNumber()
        )
        refreshingUiViewModel.getChannelList(channelListDataStore)

    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    refreshingUiViewModel.setAccountSetupResponseData(accountSetupDataStore, it)
                    Constants.ACCOUNT_ID = it.accountId
                    Constants.STB_ROOM_NO = it.roomNo
                    Constants.EPG_CDN_URL = it.epgCdnUrl
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    redirectToMainMenuScreen()
                    LoggingService.sendMessageToWebSocket(
                        "AccountSetup callback Success ",
                        getCurrentPanelNumber()
                    )
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In AccountSetup callback fail",
                    getCurrentPanelNumber()
                )
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.themeLiveData.value?.data?.let {
                    refreshingUiViewModel.setThemeResponseData(themeDataStore, it)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    redirectToMainMenuScreen()
                    LoggingService.sendMessageToWebSocket(
                        "In Theme callback Success",
                        getCurrentPanelNumber()
                    )

                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Theme Callback fail",
                    getCurrentPanelNumber()
                )


            }
        }
    }

    private fun handleHotelServicesResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.hotelServiceLiveData.value?.data?.let {
                    refreshingUiViewModel.setHotelServicesResponseData(hotelServicesDataStore, it)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    redirectToMainMenuScreen()
                    LoggingService.sendMessageToWebSocket(
                        "In Hotel Services callback Success",
                        getCurrentPanelNumber()
                    )
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Hotel Services callback fail",
                    getCurrentPanelNumber()
                )
            }
        }
    }

    private fun handleLocalAttractionResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.localAttractionLiveData.value?.data?.let {
                    refreshingUiViewModel.setLocalAttractionResponseData(
                        localAttractionDataStore,
                        it
                    )
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    redirectToMainMenuScreen()
                    LoggingService.sendMessageToWebSocket(
                        "In Local Attractions callback Success ",
                        getCurrentPanelNumber()
                    )

                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Local Attractions callback fail ",
                    getCurrentPanelNumber()
                )

            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    refreshingUiViewModel.updateSyncList(moviesDataStore, it)
                    Constants.C_LIST_VERSION = it.version
                    Constants.MOVIES_COUNT = it.freeContentList.size.plus(it.premiumContentList.size)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    LoggingService.sendMessageToWebSocket(
                        "In MoviesReleasesCollection callback Success ",
                        getCurrentPanelNumber()
                    )

                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Movies callback fail ",
                    getCurrentPanelNumber()
                )
            }
        }
    }

    private fun handleShowtimeResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.showtimeLiveData.value?.data?.let {
                    Constants.SHOWS_COUNT = it.shoContentList.size
                    refreshingUiViewModel.updateShowtimeData(showTimeDataStore, it)
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    redirectToMainMenuScreen()
                    LoggingService.sendMessageToWebSocket(
                        "In ShowtimeReleasesCollection callback Success ",
                        getCurrentPanelNumber()
                    )
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Showtime callback fail ",
                    getCurrentPanelNumber()
                )
            }
        }
    }

    private fun handleChannelListResponse(status: Resource<ChannelListResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.channelListLiveData.value?.data?.let {
                    if (EndlessService.kapingCMD == KapingConstants.KAP_CMD_GET_CHANNEL_LIST) {
                        refreshingUiViewModel.updateChannelList(channelListDataStore, it)
                        EndlessService.kapingCmdExecutionResponse =
                            KapingConstants.EXECUTED_SUCCESSFULLY
                        Constants.CHANNEL_COUNT = it.channelLcnList.size
                        redirectToMainMenuScreen()
                        LoggingService.sendMessageToWebSocket(
                            "In Channel List callback Success ",
                            getCurrentPanelNumber()
                        )
                    } else {
                        refreshingUiViewModel.fetchEPGData(Constants.EPG_CDN_URL + Constants.ACCOUNT_ID + Constants.EPG_CLOUD_URL_SUFFIX)
                    }
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                if (EndlessService.kapingCMD == KapingConstants.KAP_CMD_GET_CHANNEL_LIST)
                    LoggingService.sendMessageToWebSocket(
                        "In Channel List callback fail ",
                        getCurrentPanelNumber()
                    )
            }
        }
    }

    private fun handleEPGDataResponse(status: Resource<EPGResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.epgLiveData.value?.data?.let {
                    val simpleDateFormatter =
                        SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
                    val startDate = simpleDateFormatter.parse(it.ST)
                    val endDate = simpleDateFormatter.parse(it.ET)
                    if (isEpgDataValid(startDate, endDate)) {
                        processEPGData(it)
                        EndlessService.kapingCmdExecutionResponse =
                            KapingConstants.EXECUTED_SUCCESSFULLY
                        LoggingService.sendMessageToWebSocket(
                            "In Get EPG data callback Success ",
                            getCurrentPanelNumber()
                        )
                        redirectToMainMenuScreen()
                    } else {
                        if (!isEPGServerApiCalled) {
                            refreshingUiViewModel.fetchEPGDataFromServer(Constants.UA)
                            isEPGServerApiCalled = true
                        } else {
                            redirectToMainMenuScreen()
                            isEPGServerApiCalled = false
                        }
                    }
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
                LoggingService.sendMessageToWebSocket(
                    "In Get EPG data callback fail ",
                    getCurrentPanelNumber()
                )
            }
        }
    }

    private val REQUEST_PERMISSION_PHONE_STATE = 1

    private fun reboot() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_PERMISSION_PHONE_STATE
            )
        } else {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            pm.reboot("System update")
        }
    }

    private fun processEPGData(epgResponse: EPGResponse) {
        //Removing all Epg Channels From RoomDB.
        lifecycleScope.launch(Dispatchers.IO) {
            roomRepository.removeAllChannels()

            val simpleDateFormatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
            epgResponse.let {
                val startDate = simpleDateFormatter.parse(it.ST)
                val endDate = simpleDateFormatter.parse(it.ET)
                if (isEpgDataValid(startDate, endDate)) {
                    Constants.EPG_START = it.ST ?: ""
                    Constants.EPG_END = it.ET ?: ""
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
                                        if (channel.C?.toInt()!! > 1) {
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
                        entries.value.sortBy { it.CNO?.toInt() }
                        //Adding Channels to RoomDB.
                        roomRepository.insertChannels(entries.value)
                    }
                } else {
                    //TODO EPG DATA INVALID
                }
            }
        }
    }

    private fun removeEarlierData(
        iterator: MutableIterator<MutableMap.MutableEntry<String, MutableList<ChannelEpgDTO>>>?,
        currentKey: String
    ) {
        while (iterator?.hasNext() == true) {
            val entry = iterator.next()
            if (entry.key == currentKey)
                break
            iterator.remove()
        }
    }

    private fun fetchCurrentProgramKey(cal: Calendar = Calendar.getInstance()): String {
        val date = cal.get(Calendar.DATE)
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        var hour = cal.get(Calendar.HOUR)
        val minutes = cal.get(Calendar.MINUTE)
        val amPm = cal.get(Calendar.AM_PM)
        val time = StringBuilder()

        if (date < 10) time.append(appendZeros(date))
        else time.append(date)

        if (month < 10) time.append(appendZeros(month))
        else time.append(month)

        time.append(year)

        if (hour == 0) hour = 12

        if (hour < 10) time.append(appendZeros(hour))
        else time.append(hour.toString())

        if (minutes < 30) time.append("00")
        else time.append("30")

        if (amPm == 0) time.append("AM")
        else time.append("PM")

        return time.toString()
    }

    private fun appendZeros(value: Int): String {
        val str = StringBuffer(value.toString()).reverse()
        str.append("0")
        return str.reverse().toString()
    }

    // Validates Cloud EPG data.
    private fun isEpgDataValid(startDate: Date?, endDate: Date?): Boolean {
        val currentDate = Date()
        return !(currentDate.before(startDate) or currentDate.after(endDate))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_PHONE_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                pm.reboot("System update")
            }

            else -> {}
        }
    }

    private fun redirectToMainMenuScreen() {
        val i = Intent(this, MainMenuActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

}