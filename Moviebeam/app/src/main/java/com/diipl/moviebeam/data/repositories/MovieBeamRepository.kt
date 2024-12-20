package com.diipl.moviebeam.data.repositories

import android.content.Context
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tv.settings.aidl.regular.IDeviceNameConfigureCallback
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.datastore.UpdateDataStore
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.concierge.ConciergeResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.movies.RentalReversalRequest
import com.diipl.moviebeam.data.dto.movies.VodMovieResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.sysInfo.SysInfoDTO
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.datasource.RemoteDataSource
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.handler.ClearCredentialsHandler
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.rebootDevice
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.scheduleMsgEndTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MovieBeamRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val preferenceHandler: PreferenceHandler,
    private val roomRepository: RoomRepository,
    private val updateDataStore: UpdateDataStore,
    private val accountDataStore: DataStore<AccountSetupResponse>,
    private val themeDataStore: DataStore<ThemeResponse>,
    private val channelListDataStore: DataStore<ChannelListResponse>,
    private val hardwareAPI: HardwareAPI,
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper
) {

    private val TAG = "MovieBeamRepository"

    private val appContext = context
    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    private val clearCredentialsHandler by lazy {
        ClearCredentialsHandler(
            context,
            accountDataStore
        )
    }

    private val epgHandler by lazy { EPGHandler(appContext, roomRepository) }

    private var _epgStatus = MutableLiveData(EPGHandler.STATUS_INIT)
    val epgStatus: LiveData<Int> get() = _epgStatus

    private val _isLastApiCallFinished = MutableLiveData<Boolean>()
    val isLastApiCallFinished: LiveData<Boolean> = _isLastApiCallFinished

    companion object {
        const val ACCOUNT_SETUP = "AccountSetup"
        const val THEME = "Theme"
        const val HS = "Hotel Services"
        const val LA = "Local Attractions"
        const val MOVIES = "Movies"
        const val SHOW_TIME = "Showtime"
        const val CHANNEL_LIST = "Channel List"
        const val GUEST_MESSAGE = "Guest Message"
        const val EPG_DATA = "EPG data"
        const val TICKER_MESSAGE = "Ticker Message"
        const val WEATHER = "Weather"
    }

    private fun loge(msg: String) = this@MovieBeamRepository.logE(msg)
    private fun logd(msg: String) = this@MovieBeamRepository.logD(msg)


    fun fetchAllApi() = viewModelScope.launch {
        val isCompleted = fetchAccountData()

        delay(500)

        val accountSetupResponse = accountDataStore.data.first()
        val isAccountAvailable = accountSetupResponse.accountId == preferenceHandler.accountID || preferenceHandler.accountID.isEmpty()

        if (isCompleted && isAccountAvailable) {
            updateGuestSession(false, CmdDataDto())

            val initCall = async {
                fetchThemeDetails()
                fetchWeatherData()
                fetchTickerMessages()
                if (BuildConfig.BUILD_TYPE_ID == 6){
                    if (accountSetupResponse.isPlayBoxTv)
                        fetchChannelList() else return@async
                } else fetchChannelList()
            }

            val btnModelList = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter { model ->
                accountSetupResponse.buttonsList.any { it.buttonName == model.btnId }
            }

            // Launch all API calls via btnID with 2 sec interval and wait for their completion
            val apiCallJobs = btnModelList.map { btnModel ->
                delay(1000 * 2)
                async {
                    handleButtonAction(btnModel.btnId)
                }
            }

            logd("Waiting for API calls to complete.... ")
            initCall.await()

            // check theme version is different from existing version and retry 3 times if api call fails then reboot the device
            var apiCount = 0
            val recall = async {
                while (isActive) {
                    fetchThemeDetails()
                    val themeResponse = themeDataStore.data.first()
                    logd("retry apiCount: $apiCount, themeVersion: ${preferenceHandler.themeVersion}, datastore theme version: ${themeResponse.version}")
                    if (preferenceHandler.themeVersion == themeResponse.version)
                        break
                    if (apiCount == 3){
                        BaseActivity.currentActivity?.let {
                            preferenceHandler.updateDatastoreVariables(isAllDataFetched = false)
                            it.rebootDevice()
                        }
                        break
                    }
                    apiCount++
                    delay(500)
                }
            }

            recall.await()
            // Wait for all API calls to complete
            apiCallJobs.awaitAll()
            // Mark the completion after all calls are done
            logd("All API calls completed.")
            _isLastApiCallFinished.postValue(true)
        } else {
            loge("fetchAllApi: $ACCOUNT_SETUP API failed!")
            _isLastApiCallFinished.postValue(false)
        }
    }

    fun fetchDataNotPresent() = viewModelScope.launch {
        val accountSetupResponse = accountDataStore.data.first()

        val btnModelList = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter { model ->
            accountSetupResponse.buttonsList.any { it.buttonName == model.btnId }
        }

        // Launch all API calls via btnID with 2 sec interval and wait for their completion
        val apiCallJobs = btnModelList.map { btnModel ->
            delay(1000 * 2)
            async {
                handleButtonAction(btnModel.btnId)
            }
        }

        logd("Waiting for API calls to complete.... ")
        apiCallJobs.awaitAll()
        // Mark the completion after all calls are done
        logd("All API calls completed.")

        _isLastApiCallFinished.postValue(true)
    }

    private suspend fun handleButtonAction(btnId: String) {
        when (btnId) {
            Constants.VOD_ID -> if (preferenceHandler.isMoviesEmpty || !preferenceHandler.isAllDataFetched) fetchMoviesMore()
            Constants.PRG_GUIDE_ID -> {
                if (BuildConfig.BUILD_TYPE_ID != 6)
                    if (preferenceHandler.isEPGEmpty || !preferenceHandler.isAllDataFetched)
                        fetchEpgData()
            }
            Constants.SHOWTIMES_ID -> if (preferenceHandler.isShowtimeEmpty || !preferenceHandler.isAllDataFetched) fetchShowtime()
            Constants.HOTEL_SERVICES_ID -> if (preferenceHandler.isHSEmpty || !preferenceHandler.isAllDataFetched) fetchHotelService()
            Constants.LOCAL_ATTRACTION_ID -> if (preferenceHandler.isLAEmpty || !preferenceHandler.isAllDataFetched) fetchLocalAttractions()
        }
    }

    private fun scheduleTickerMsg(response: TickerResponse) {
        val sdf =
            SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            response.tvTickerList?.removeIf { msg ->
                (msg.all == 0 && msg.assignedRooms?.contains(preferenceHandler.roomNo) != true) || sdf.parse(
                    msg.etStr
                ).before(Date())
            }
        } else {
            val iterator = response.tvTickerList?.iterator()
            while (iterator!!.hasNext()) {
                val msg: TvTickerDTO = iterator.next()
                if ((msg.all == 0 && msg.assignedRooms?.contains(preferenceHandler.roomNo) != true) || sdf.parse(
                        msg.etStr
                    ).before(Date())
                ) {
                    iterator.remove()
                }
            }
        }
        response.tvTickerList?.forEach { msg ->
            appContext.scheduleMsgEndTask(msg)
        }
    }

    // Generalized fetch data methods
    // isKapingCmd is used only to check if is it a Kaping call or not
    suspend fun fetchAccountData(isKapingCmd: Boolean = false): Boolean = fetchData(
        {
            remoteDataSource.getAccountSetupDetails(
                Constants.ACTIVATE,
                preferenceHandler.UA,
                Constants.MODE
            )
        },
        { response ->
            if (preferenceHandler.accountID.isNotEmptyOrNull() && isKapingCmd){
                if (preferenceHandler.accountID != response.accountId){
                    BaseActivity.currentActivity?.let {
                        preferenceHandler.updateDatastoreVariables(isStbAllocated = false, isAllDataFetched = false)
                        it.rebootDevice()
                        return@fetchData
                    }
                }
            }
            if(BuildConfig.BUILD_TYPE==Constants.BUILD_TYPE_STB) {
                hardwareAPI.myService?.setDeviceName("MBAP_${response.accountId}_${response.roomNo}",
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
            appContext.scheduleClearCredentialsTask(response.checkOutTime)
            updateDataStore.updateAccountData(response)
        }, ACCOUNT_SETUP

    )

    suspend fun fetchHotelService(): Boolean = fetchData(
        { remoteDataSource.getHotelServiceInfo(preferenceHandler.UA) },
        { updateDataStore.updateHSData(it) }, HS
    )

    suspend fun fetchLocalAttractions(): Boolean = fetchData(
        { remoteDataSource.getLocalAttractionInfo(preferenceHandler.UA) },
        { updateDataStore.updateLAData(it) }, LA
    )

    suspend fun fetchWeatherData(): Boolean = fetchData(
        { remoteDataSource.getWeatherData(preferenceHandler.UA) },
        { updateDataStore.updateWeatherData(it) }, WEATHER
    )

    suspend fun fetchThemeDetails(): Boolean = fetchData(
        { remoteDataSource.getThemeDetails(preferenceHandler.UA) },
        { updateDataStore.updateThemeData(it) }, THEME
    )

    suspend fun fetchMoviesMore(): Boolean = fetchData(
        { remoteDataSource.getMoviesInfo(preferenceHandler.UA) },
        { updateDataStore.updateMoviesData(it) }, MOVIES
    )

    suspend fun fetchShowtime(): Boolean = fetchData(
        { remoteDataSource.getShowtimeInfo(preferenceHandler.UA) },
        { updateDataStore.updateShowTimeData(it) }, SHOW_TIME
    )

    suspend fun fetchTickerMessages(): Boolean = fetchData(
        { remoteDataSource.getTvTickerMessages(preferenceHandler.UA) },
        {
            scheduleTickerMsg(it)
            updateDataStore.updateTickerData(it)
        }, TICKER_MESSAGE
    )

    suspend fun fetchChannelList(): Boolean = fetchData(
        { remoteDataSource.getChannelList(preferenceHandler.UA) },
        { updateDataStore.updateChannelListData(it) }, CHANNEL_LIST
    )

    suspend fun fetchGuestMessage(): Boolean = fetchData(
        { remoteDataSource.getGuestMessages(preferenceHandler.UA, preferenceHandler.sessionId) },
        { updateDataStore.updateGuestMessageData(it) }, GUEST_MESSAGE
    )

    suspend fun fetchEpgData() {
        val response = remoteDataSource.getEPGFromCloud(preferenceHandler.epgCDNUrl)
        handleEPGStatus()
        response?.let {
            logD("In $EPG_DATA from Cloud Callback Success ")
            epgHandler.parseEPG(it, channelListDataStore.data.first().channelLcnList)
        } ?: fetchEPGDataFromServer()
    }

    private fun fetchEPGDataFromServer() = viewModelScope.launch(Dispatchers.IO) {
        val response = remoteDataSource.getEPGDataFromServer(preferenceHandler.UA)
        handleEPGStatus(false)
        response?.let {
            logD("In $EPG_DATA from Server Callback Success ")
            epgHandler.parseEPG(it, channelListDataStore.data.first().channelLcnList)
        }
    }

    private fun handleEPGStatus(isCDN: Boolean = true) {
        viewModelScope.launch(Dispatchers.Main) {
            epgHandler.epgStatus.observeForever { status ->
                _epgStatus.postValue(status)
                when (status) {
                    EPGHandler.STATUS_FAIL -> {
                        if (isCDN) {
                            logE("Invalid $EPG_DATA found from Cloud.")
                            logD("Fetching $EPG_DATA from Server.")
                            fetchEPGDataFromServer()
                        } else {
                            logE("Invalid $EPG_DATA found from Server.")
                        }
                    }

                    EPGHandler.STATUS_OK -> {
                        if (isCDN){
                            logD("Valid $EPG_DATA found from Cloud.")
                        } else logD("Valid $EPG_DATA found from server.")
                        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                        this.cancel()
                    }
                }
            }
        }
    }

    // Generic helper for data fetching
    private suspend fun <T> fetchData(
        fetch: suspend () -> T?,
        handleResponse: suspend (T) -> Unit,
        logTag: String,
    ): Boolean {
        val response = fetch()
        /*loge(
            "fetchData: response -->> ${
                response.toString().substring(
                    0,
                    if (response.toString().length > 400) 200 else response.toString().length
                )
            }"
        )*/
        return if (response != null) {
            handleResponse(response)
            logd("In $logTag callback success")
            true
        } else {
            loge("In $logTag callback fail")
            false
        }
    }

    fun updateGuestSession(isCheckedIn: Boolean, guestDetails: CmdDataDto?, response: MessageResponse = MessageResponse()) = CoroutineScope(Dispatchers.IO).launch {
        guestDetails?.let {
            val sessionId = it.sessionId ?: ""
            if (isCheckedIn && sessionId == preferenceHandler.sessionId){
                return@launch
            }

            roomRepository.deleteRecentMovies()
            roomRepository.deleteRecentShows()
            preferenceDataStoreHelper.putPreference(PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS, false)

            updateDataStore.updateGuestMessageData(response)
            clearCredentialsHandler.startClearCredentials(false)

            preferenceHandler.updateDatastoreVariables(
                isCheckedIn = isCheckedIn,
                sessionId = sessionId
            )
            updateDataStore.updateGuestData(it)
        }
    }

    suspend fun getMovieAccess(request: RentalMovieRequest) =
        remoteDataSource.getMoviesAccess(request)

    suspend fun getFlightStatus(callType: String, apCode: String) =
        remoteDataSource.getFlightStatus(Constants.FLIGHT_STATUS_CMD, preferenceHandler.UA, callType, apCode, Constants.MODE)

    suspend fun getInRoomDiningInfo() = remoteDataSource.getInRoomDiningInfo(preferenceHandler.UA)

    suspend fun getNewsHeader(languageId: Int) = remoteDataSource.getNewsHeader(preferenceHandler.UA, languageId)

    suspend fun getNewsDetails(newsId: Int) = remoteDataSource.getNewsDetails(preferenceHandler.UA, newsId)

    suspend fun processStbMaster(UA: String, serialNo: String) = remoteDataSource.processStbMaster(UA, serialNo, Constants.MAC_ADDRESS,
        Constants.WIFI_MAC_ADDRESS,
        Constants.STB_TYPE)

    suspend fun getLaundryDetails(serviceId: String) = remoteDataSource.laundryResponse(preferenceHandler.UA, serviceId)

    suspend fun sendGuestFeedback(feedback: String) = remoteDataSource.sendGuestFeedback(preferenceHandler.UA, feedback)

    suspend fun setRentalReversal(request: RentalReversalRequest) = remoteDataSource.setRentalReversal(request)

    suspend fun buyPassRequest(request: AdultDayPassRequest) = remoteDataSource.buyPassRequest(request)

    suspend fun sendSysInfo(body: SysInfoDTO) = remoteDataSource.sendSysInfo(preferenceHandler.UA, body)

    suspend fun sendConciergeInfo(serviceId: String, body: ConciergeResponse) =
        remoteDataSource.processConciergeMaster(preferenceHandler.UA, serviceId, body)

    suspend fun getSoftwareUpdateDetails() = remoteDataSource.getSoftwareUpdateDetails(BuildConfig.BUILD_TYPE_ID, preferenceHandler.UA)

    suspend fun getNewMovieAccess(
        q: String,
        UA: String,
        RID: Int,
        PID: Int,
        price: Double,
        timeStamp: Long,
        seek: Long,
        sessionID: String,
        a: Int,
        ra: Int,
        cType: String,
        seekType: Long,
        rentalID: String,
        contentTypeID: Int,
        productType: Int,
        vodMID: Long,
        AID: Long,
        mode: String
    ): RentalMovieResponse? {
        Log.d("TAG", "getVodData: ${remoteDataSource.getNewMoviesAccess(
            q, UA, RID, PID, price, timeStamp, seek, sessionID, a, ra, cType, seekType,
            rentalID, contentTypeID, productType, vodMID, AID, mode
        )}")
        return remoteDataSource.getNewMoviesAccess(
            q, UA, RID, PID, price, timeStamp, seek, sessionID, a, ra, cType, seekType,
            rentalID, contentTypeID, productType, vodMID, AID, mode
        )
    }

    suspend fun getVodData(
//        vodMgrIp: String,
        baseUrl: String,
        vodMid: Int ,
        transId: Long,
        streamingType: String,
        mode: String ,
        ua: String,
        rentalId: Int,
        productId: Int,
        priority: Int ,
        fileName: String,
        contentType: String,
        seek: Long,
        json: Boolean
    ): VodMovieResponse? {
        Log.d("TAG", "getVodData: ${remoteDataSource.getVodData(
            baseUrl,vodMid,transId,streamingType,mode,ua,rentalId,productId,priority,fileName,contentType,seek,json
        )}")
        return remoteDataSource.getVodData(
            baseUrl,vodMid,transId,streamingType,mode,ua,rentalId,productId,priority,fileName,contentType,seek,json
        )
    }
}