package com.diipl.moviebeam.ui.stbdetail

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.isWorkDone
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.launchNewActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.scheduleEpgApiCall
import com.diipl.moviebeam.utils.scheduleMsgEndTask
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInteger
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val stbDetailViewModel: STBDetailViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding
    private var serialNumber: String = ""
    private var UA = ""
    private var isEPGServerApiCalled = false

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    @Inject
    lateinit var tickerDataStore: DataStore<TickerResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var showTimeDataStore: DataStore<ShowTimeResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var preferences: SharedPreference

    //Variables from datastore
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            applicationContext
        )
    }
    private var stbRoomNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferences.irFrequencyModel == null)
            preferences.irFrequencyModel = IRUtils.SELECTED_BRAND

        this.initializeDatastoreParams()
        stbDetailViewModel.getNetworkStatus(preferenceDataStoreHelper)

        if (!IS_API_CALLED) {
            stbDetailViewModel.getDataFromDataStore(preferenceDataStoreHelper)
            IS_API_CALLED = true
        } else {
            finish()
        }

    }

    //observe class
    override fun observeViewModel() {
        observe(stbDetailViewModel.networkStatus, ::handleNetworkResponse)
        observe(stbDetailViewModel.serialNoLiveData, ::handleSerialNumberResponse)
        observe(stbDetailViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(stbDetailViewModel.themeLiveData, ::handleThemeResponse)
        observe(stbDetailViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(stbDetailViewModel.localAttractionLiveData, ::handleLAServiceResponse)
        observe(stbDetailViewModel.channelListLiveData, ::handleChannelListResponse)
        observe(stbDetailViewModel.moviesLiveData, ::handleMoviesResponse)
        observe(stbDetailViewModel.tickerLiveData, ::handleTickerResponse)
        observe(stbDetailViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
        observe(stbDetailViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(stbDetailViewModel.epgLiveData, ::handleEpgResponse)

        observeSnackBarMessages(stbDetailViewModel.showSnackBar)
        observeToast(stbDetailViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityStbdetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun handleNetworkResponse(isConnected: Boolean) {
//        isNetworkConnected = isConnected
        /*if (!isConnected) {
            launchMain()
        }*/
    }

    private fun launchMain() {
        launchNewActivity(MainMenuActivity::class.java, true)
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.weatherLiveData.value?.data?.let {
                    stbDetailViewModel.setWeatherResponseData(
                        it.copy(tempCondition = replaceDegreeSymbol(it.tempCondition))
                    )
                }
                logD("Weather Api call success")
            }

            else -> {
                logE("Weather Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.themeLiveData.value?.data?.let {
                    stbDetailViewModel.setThemeResponseData(it)
                    updateDatastoreVariables(
                        gradientStartColor = it.gradientColor,
                        gradientEndColor = it.spotLightColor
                    )
                    ThemeDetails.GRADIENT_COLOR_START = it.gradientColor
                    ThemeDetails.GRADIENT_COLOR_END = it.spotLightColor
                    ThemeDetails.GRADIENT = null
                    ThemeDetails.GRADIENT = getGradientColor()
                    ThemeDetails.BG_IMAGE = it.themeBackgroundFileName
                    ThemeDetails.LOGO_IMAGE = it.themeLogoFileName
                }
                logD("Theme Api call success")
            }

            else -> {
                logE("Theme Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.setAccountSetupResponseData(accountSetupDataStore, it)
                    CoroutineScope(Dispatchers.Main).launch {
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
                            it.accountId
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
                            it.roomNo
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.EPG_CDN_URL_KEY,
                            it.epgCdnUrl + it.accountId + Constants.EPG_CLOUD_URL_SUFFIX
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.CASTING_URL_KEY,
                            it.stbCastingPageUrl
                        )
                        if (it.contentDetailFlag)
                            preferenceDataStoreHelper.putPreference(
                                PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY,
                                it.httpStreamingHotelvideoUrl + it.hotelChannelList[0].fileName
                            )
                    }
                    scheduleClearCredentialsTask(it.checkOutTime)
                    stbDetailViewModel.fetchHotelService()
                }
                logD("Account Setup Api call success")
            }

            else -> {
                logE("Account Setup Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                status.data?.let {
                    stbDetailViewModel.setHotelServicesResponseData(it)
                }
                logD("Hotel Services Api call success")
            }

            else -> {
                logE("Hotel Services Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.localAttractionLiveData.value?.data?.let {
                    stbDetailViewModel.setLocalAttractionResponseData(it)
                }
                logD("Local Attractions Api call success")
            }

            else -> {
                logE("Local Attractions Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleChannelListResponse(status: Resource<ChannelListResponse>) {
        when (status) {
            is Resource.Success -> {
                stbDetailViewModel.channelListLiveData.value?.data?.let {
                    stbDetailViewModel.setChannelListResponseData(channelListDataStore, it)
                    updateDatastoreVariables(channelCount = it.channelLcnList.size)
                }
                logD("Channel List Api call success")
            }

            else -> {
                logE("Channel List Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleEpgResponse(status: Resource<EPGResponse>) {
        when (status) {
            is Resource.Success -> {
                //Removing all Epg Channels From RoomDB.
                lifecycleScope.launch(Dispatchers.IO) {
                    roomRepository.removeAllChannels()
                }

                val simpleDateFormatter =
                    SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
                status.data?.let {
                    if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                        logD("Valid EPG data found EPG Start time: ${it.ST} & EPG End time: ${it.ET}")
                        updateDatastoreVariables(epgStartTime = it.ST, epgEndTime = it.ET)
                        val channelList =
                            stbDetailViewModel.channelListLiveData.value?.data?.channelLcnList
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
                            lifecycleScope.launch(Dispatchers.IO) {
                                roomRepository.insertChannels(entries.value)
                            }
                        }
                        redirectToMainMenuPage()
                    } else {
                        logE("Invalid EPG data found")
                        fetchEPGFromServer()
                    }
                }

            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun fetchEPGFromServer() {
        if (!isEPGServerApiCalled) {
            logD("Fetching EPG Data from server")
            stbDetailViewModel.fetchEPGDataFromServer(UA)
            isEPGServerApiCalled = true
        } else {
            redirectToMainMenuPage()
        }
    }

    private fun redirectToMainMenuPage() {
        scheduleEpgApiCall()
        /* binding.root.post { binding.root.performClick() }
         var uuid: UUID? = null
         binding.root.setSafeOnClickListener {
             val inputData = Data.Builder()
                 .putString(UpdateDataWorker.ACTION, UpdateDataWorker.ACTION_ALL)
                 .build()

             val request = OneTimeWorkRequest.Builder(UpdateDataWorker::class.java)
                 .setInputData(inputData)
                 .build()
             uuid = request.id
             Log.e(TAG, "uuid: $uuid")
             workManager.beginUniqueWork(uuid.toString(), ExistingWorkPolicy.REPLACE, request).enqueue()
 //            workManager.enqueue(request)
         }*/


//            workManager.getWorkInfoByIdLiveData(uuid!!).observe(this@STBDetailsActivity) { data ->
//                val isDone = data.state == WorkInfo.State.SUCCEEDED
//                if (isDone) {

//                }
        lifecycleScope.launch {
            while (true) {
                if (isWorkDone == 3) {
                    launchMain()
                    isWorkDone = 0
                }
                delay(1000 * 10)
            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.moviesLiveData.value?.data?.let {
                    updateDatastoreVariables(
                        moviesCount = it.freeContentList.size.plus(it.premiumContentList.size),
                        cListVersion = it.version
                    )
                    stbDetailViewModel.setMoviesResponseData(moviesDataStore, it)
                }
                logD("Movies Api call success")
            }

            else -> {
                logE("Movies Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleTickerResponse(status: Resource<TickerResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    val sdf =
                        SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.tvTickerList?.removeIf { msg ->
                            (msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true) || sdf.parse(
                                msg.etStr
                            ).before(Date())
                        }
                    } else {
                        val iterator = it.tvTickerList?.iterator()
                        while (iterator!!.hasNext()) {
                            val msg: TvTickerDTO = iterator.next()
                            if ((msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true) || sdf.parse(
                                    msg.etStr
                                ).before(Date())
                            ) {
                                iterator.remove()
                            }
                        }
                    }
                    stbDetailViewModel.setTickerResponseData(tickerDataStore, it)
                    it.tvTickerList?.forEach { msg ->
                        applicationContext.scheduleMsgEndTask(msg)
                    }
                }
            }

            else -> {

                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                status.data?.let {
                    updateDatastoreVariables(showsCount = it.shoContentList.size)
                    stbDetailViewModel.setShowTimeResponseData(showTimeDataStore, it)
                }
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.fetchEpgData(it.epgCdnUrl + it.accountId + Constants.EPG_CLOUD_URL_SUFFIX)
                }
                logD("Showtime Api call success")
            }

            else -> {
                logE("Showtime Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        logE("handleSerialNumberResponse Started")
        /*   if (!isNetworkConnected) {
               launchMain()
               return
           }*/
        isWorkDone = 0

        serialNumber = serialNo
        UA = "21$serialNumber"
        stbDetailViewModel.fetchApis()

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_SHORT)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_SHORT)
    }

    private fun replaceDegreeSymbol(temp: String?): String {
        var temperature = ""
        temp?.let {
            temperature = if (it.contains("&deg C")) {
                it.replace("&deg C", Constants.SYMBOL_DEGREE_CELSIUS)
            } else {
                it.replace("&deg F", Constants.SYMBOL_DEGREE_FAHRENHEIT)
            }
        }
        return temperature
    }

    override fun onBackPressed() {}

    private fun updateDatastoreVariables(
        moviesCount: Int? = null,
        showsCount: Int? = null,
        cListVersion: String? = null,
        gradientStartColor: String? = null,
        gradientEndColor: String? = null,
        channelCount: Int? = null,
        epgStartTime: String? = null,
        epgEndTime: String? = null
    ) {
        lifecycleScope.launch {
            moviesCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.MOVIES_COUNT_KEY,
                    it
                )
            }
            showsCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SHOWS_COUNT_KEY,
                    it
                )
            }
            cListVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.C_LIST_VERSION_KEY,
                    it
                )
            }
            gradientStartColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
                    it
                )
            }
            gradientEndColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
                    it
                )
            }
            channelCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.CHANNEL_COUNT_KEY,
                    it
                )
            }
            epgStartTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_START_TIME_KEY,
                    it
                )
            }
            epgEndTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_END_TIME_KEY,
                    it
                )
            }
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            stbRoomNo = getStbRoomNo()
        }
    }

    private suspend fun getStbRoomNo(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
            ""
        )
    }

    companion object {
        private var IS_API_CALLED = false
    }

}