package com.diipl.moviebeam.ui.stbdetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker.Result
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.refreshingui.UpdateDataWorker
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.scheduleMsgEndTask
import com.diipl.moviebeam.utils.setSafeOnClickListener
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInteger
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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

    private val TAG = "STBDetailsActivity"

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

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var startMs: Long = 0
    private val workManager: WorkManager by lazy { WorkManager.getInstance(applicationContext) }
    private var isNetworkConnected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        startMs = System.currentTimeMillis()

        if (preferences.irFrequencyModel == null)
            preferences.irFrequencyModel = IRUtils.SELECTED_BRAND
        clearCache()

        stbDetailViewModel.getNetworkStatus(preferenceDataStoreHelper)

        if (!Constants.IS_API_CALLED) {
            stbDetailViewModel.getDataFromDataStore(preferenceDataStoreHelper)
            Constants.IS_API_CALLED = true
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
        isNetworkConnected = isConnected
        if (!isConnected) {
            launchMain()
        }
    }

    private fun launchMain() {
        val intent = Intent(this@STBDetailsActivity, MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.weatherLiveData.value?.data?.let {
                    stbDetailViewModel.setWeatherResponseData(
                        it.copy(tempCondition = replaceDegreeSymbol(it.tempCondition))
                    )
                    Log.d("DataStoreResponse", "handleWeatherResponse: $it")
                }
            }

            else -> {
                launchMain()
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {

                stbDetailViewModel.themeLiveData.value?.data?.let {
                    stbDetailViewModel.setThemeResponseData(themeDataStore, it)
                    Log.d("DataStoreResponse", "handleThemeResponse: $it")
                }
            }

            else -> {
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
                    Constants.ACCOUNT_ID = it.accountId
                    Constants.STB_ROOM_NO = it.roomNo
                    Constants.EPG_CDN_URL = it.epgCdnUrl
                    scheduleClearCredentialsTask(it.checkOutTime)
                    stbDetailViewModel.fetchHotelService()
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

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
            }

            else -> {
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
            }

            else -> {
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
                    Constants.CHANNEL_COUNT = it.channelLcnList!!.size
                }
            }

            else -> {

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

                val simpleDateFormatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
                stbDetailViewModel.epgLiveData.value?.data?.let {
                    val startDate = simpleDateFormatter.parse(it.ST ?: "")
                    val endDate = simpleDateFormatter.parse(it.ET ?: "")
                    if (isEpgDataValid(startDate, endDate)) {
                        Constants.EPG_START = it.ST ?: ""
                        Constants.EPG_END = it.ET ?: ""
                        val channelList =
                            stbDetailViewModel.channelListLiveData.value?.data?.channelLcnList
                        val currentKey = fetchCurrentProgramKey()
                        this.removeEarlierData(it.epgListMap?.entries?.iterator(), currentKey)
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
                        if (!isEPGServerApiCalled) {
                            stbDetailViewModel.fetchEPGDataFromServer(UA)
                            isEPGServerApiCalled = true
                        } else {
                            redirectToMainMenuPage()
                        }
                    }
                }

            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun redirectToMainMenuPage() {
        binding.root.post { binding.root.performClick() }
        binding.root.setSafeOnClickListener {
            val inputData = Data.Builder()
                .putString(UpdateDataWorker.ACTION, UpdateDataWorker.ACTION_ALL)
                .build()

            val request = OneTimeWorkRequestBuilder<UpdateDataWorker>()
                .setInputData(inputData)
                .build()
            workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
        }
        
        workManager.getWorkInfosForUniqueWorkLiveData(TAG).observe(this) { data ->
            if (data.isNotEmpty()){
                if(data[0].state == WorkInfo.State.SUCCEEDED){
                    val intent = Intent(this@STBDetailsActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    //  Removes Data Before Current Time.
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

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.moviesLiveData.value?.data?.let {
                    Constants.MOVIES_COUNT =
                        it.freeContentList.size.plus(it.premiumContentList.size)
                    Constants.C_LIST_VERSION = it.version
                    stbDetailViewModel.setMoviesResponseData(moviesDataStore, it)
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleTickerResponse(status: Resource<TickerResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    val sdf = SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.tvTickerList?.removeIf { msg ->
                            (msg.all == 0 && msg.assignedRooms?.contains(Constants.STB_ROOM_NO) != true) || sdf.parse(
                                msg.etStr
                            ).before(Date())
                        }
                    } else {
                        val iterator = it.tvTickerList?.iterator()
                        while (iterator!!.hasNext()) {
                            val msg: TvTickerDTO = iterator.next()
                            if ((msg.all == 0 && msg.assignedRooms?.contains(Constants.STB_ROOM_NO) != true) || sdf.parse(
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
                    Constants.SHOWS_COUNT = it.shoContentList.size
                    stbDetailViewModel.setShowTimeResponseData(showTimeDataStore, it)
                }
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.fetchEpgData(it.epgCdnUrl + it.accountId + Constants.EPG_CLOUD_URL_SUFFIX)
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        if (!isNetworkConnected){
            launchMain()
            return
        }

        serialNumber = serialNo
        Constants.SERIAL_NO = serialNo
        UA = "21$serialNumber"
        Constants.UA = UA
        stbDetailViewModel.fetchApis(applicationContext, preferenceDataStoreHelper)

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
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

}