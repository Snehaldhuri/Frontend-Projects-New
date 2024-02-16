package com.diipl.moviebeam.ui.kappingservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.KapingConstants
import com.diipl.moviebeam.PanelConstants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.kaping.CmdDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HelpInfoFragment
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.localattraction.LocalAttractionActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.movies.MovieDetailFragment
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.programguide.PrgGuidePlayerActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiActivity
import com.diipl.moviebeam.ui.serial_info.SerialActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeDetailFragment
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.KapingResponseParsing
import com.diipl.moviebeam.utils.log
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Integer.parseInt
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class EndlessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var counter = 0
    private val myApiService = createRetrofitService()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var UA = ""
    private var isGuestCheckedIn = false
    private var versionNumber = ""
    private var themeVersion = ""
    private var laVersion = ""
    private var moviesVersion = ""
    private var hotelServicesVersion = ""
    private var kapingCMD = KapingConstants.KAP_CMD_DO_NOTHING
    private var CMDRES = ""
    private var EVENT = ""
    private var epochTime = ""
    private var transactionId = ""

    private val _themeLiveData = MutableLiveData<ThemeResponse>()
    val themeLiveData: LiveData<ThemeResponse> get() = _themeLiveData

    private val _localAttractionLiveData = MutableLiveData<LocalAttractionResponse>()
    val localAttractionLiveData: LiveData<LocalAttractionResponse> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<MoviesResponse>()
    val moviesLiveData: LiveData<MoviesResponse> get() = _moviesLiveData

    private val _hotelServicesLiveData = MutableLiveData<HotelServiceResponse>()
    val hotelServicesLiveData: LiveData<HotelServiceResponse> get() = _hotelServicesLiveData

    @Inject
    lateinit var movieBeamRepository: MovieBeamRepository

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var localAttractionsDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var showtimeDataStore: DataStore<ShowTimeResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>
    @Inject
    lateinit var roomRepository: RoomRepository


    companion object {
        var isServiceStarted = false
        var AS_FLAG = false
        var kapingCmdExecutionResponse = KapingConstants.PENDING_EXECUTION

        val gson = GsonBuilder()
            .setLenient()
            .create()

        fun provideOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
                .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
                .build()
        } else
            OkHttpClient
                .Builder()
                .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
                .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
                .build()

        fun createRetrofitService(): LgRestApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.BASE_URL_ACCOUNT_SETUP)
                .client(provideOkHttpClient())
                .build()

            return retrofit.create(LgRestApiService::class.java)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        log("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log("The service has been created".uppercase(Locale.ROOT))
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        versionNumber = getVersionNumber().replace(".", "").trim()
        log(versionNumber)

        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("The service has been destroyed".uppercase(Locale.ROOT))
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        if (isServiceStarted) return
        log("Starting the foreground service task")
//        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    UA = preferenceDataStoreHelper.getFirstPreference(
                        PreferenceDataStoreConstants.UA,
                        ""
                    )

                    _themeLiveData.postValue(themeDataStore.data.first())
                    _localAttractionLiveData.postValue(localAttractionsDataStore.data.first())
                    _moviesLiveData.postValue(moviesDataStore.data.first())
                    _hotelServicesLiveData.postValue(hotelServicesDataStore.data.first())
                    isGuestCheckedIn = preferenceDataStoreHelper.getFirstPreference(
                        PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN,
                        false
                    )
                    log("UA -> $UA")
                    if (UA.isNotBlank()) {
                        pingFakeServer()
                        callKapingApi()

                    if (Constants.SESSION_ID.isNotEmpty())
                       roomRepository.removeOverTimeMovies()
                    if (Constants.SESSION_ID == "null")
                        roomRepository.deleteRecentMovies()

                    }
                }
                delay(1 * 60 * 1000)
            }
            log("End of the loop for the service")
        }
    }

    private fun stopService() {
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun pingFakeServer() {
        counter++
        log(counter.toString())
    }

    private fun callKapingApi() {

        val themeVersion1 = themeLiveData.value?.version
        if (!themeVersion1.isNullOrEmpty()) {
            themeVersion = themeVersion1
        }
        log(themeVersion1.toString())

        val laVersion1 = localAttractionLiveData.value?.version
        if (!laVersion1.isNullOrEmpty()) {
            laVersion = laVersion1
        }
        log(laVersion1.toString())

        val moviesVersion1 = moviesLiveData.value?.version
        if (!moviesVersion1.isNullOrEmpty()) {
            moviesVersion = moviesVersion1
        }
        log(moviesVersion1.toString())

        val hotelServicesVersion1 = hotelServicesLiveData.value?.version
        if (!hotelServicesVersion1.isNullOrEmpty()) {
            hotelServicesVersion = hotelServicesVersion1
        }
        log(hotelServicesVersion1.toString())

        var epoch =
            parseInt(((Date().getTime() / 1000).toString())).toString(16); //Date => timestamp => HEX
        log("epoch - > $epoch")
        var Prefix = "";
        var Prfixzero = "";
        //Epoch should be 9 charcters, append 0 if not
        if (epoch.length < 9) {
            Prfixzero = (9 - epoch.length).toString();
            if (Prfixzero.equals("1")) {
                Prefix = "0"
            } else if (Prfixzero.equals("2")) {
                Prefix = "00";
            }
            epoch = Prefix + epoch;
            log("prefix epoch ->  $epoch")
            epochTime = epoch
        }
        if (kapingCMD == KapingConstants.KAP_CMD_DO_NOTHING) {
            CMDRES = ""
        } else {
            CMDRES =
                "$kapingCMD$epochTime$transactionId${kapingCmdExecutionResponse}"
        }
        log("CMDRES -> $CMDRES")

        EVENT =
            isScreenOn() + getCurrentPanelNumber() + if (isGuestCheckedIn) KapingConstants.EVENT_CHECK_IN else KapingConstants.EVENT_CHECK_OUT
        log("EVENT -> $EVENT")

        val kapingCall = myApiService.getKapingService(
            Constants.KAPING,
            UA,
            "0",
            versionNumber,
            moviesVersion,
            Constants.DV,
            "1",
            Constants.KAPINGEVENT,
            "0",
            Constants.RBTY,
            Constants.MODE,
            laVersion,
            hotelServicesVersion,
            themeVersion,
            CMDRES,
            "1",
            Constants.INRMVER,
            Constants.LAUVER
        )

        kapingCall.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (response.isSuccessful) {
                        val data = response.body()
                        val result = KapingResponseParsing().getResponseAsObject(
                            data,
                            KapingResponse::class
                        )
                        result?.CMD?.let {
                            result.cmdData = parseCmd(it)
                        }
                        // Handle the data here
                        log(result.toString())
                        kapingCmdExecutionResponse = KapingConstants.PENDING_EXECUTION


                        log(result.toString())

                        AS_FLAG = if (result?.AS.isNullOrEmpty()) {
                            Log.e("true_as", "endless_service $AS_FLAG")
                            true
                        } else {
                            Log.e("false_as", "endless_service $AS_FLAG")
//                        startActivity(Intent(applicationContext, KapingActivity::class.java))
                            false
                        }


                        handleKaping(result)
                    } else {
                        // Handle unsuccessful response
                    }

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

                log(t.toString())
            }

        })

    }

    private fun parseCmd(cmd: String): CmdDto {
        kapingCMD = cmd.substring(0, 2)
        val epochTime = cmd.substring(2, 11)
        transactionId = cmd.substring(11, minOf(cmd.length, 19))
        var cmdDataDto: CmdDataDto? = null
        if (kapingCMD == KapingConstants.KAP_CMD_CHECK_IN || kapingCMD == KapingConstants.KAP_CMD_CHECK_OUT) {
            val cmdData = cmd.substring(19, cmd.length)
            val sessionId = cmdData.substring(5, 15)
            var adultContentDisabled: Boolean? = null
            val message = cmdData.substringAfter(" ").trim()
            var passCode: String? = null
            var parentSessionId: String? = null
            var guestFirstName: String? = null
            var guestLastName: String? = null
            var adultLocked: Boolean? = null
            if (kapingCMD == KapingConstants.KAP_CMD_CHECK_IN) {
                adultContentDisabled = cmdData[0] != '0'
                parentSessionId = cmdData.substring(15, 25)
                val nameAndPass = cmdData.substring(cmdData.indexOf("Welcome"))
                guestFirstName = nameAndPass.split(" ")[1]
                val lastNameAndPass = nameAndPass.split(" ")[2]
                try {
                    guestLastName = lastNameAndPass.substring(0, lastNameAndPass.length - 4)
                    passCode = lastNameAndPass.substring(lastNameAndPass.length - 4)
                    if (passCode == "____") {
                        passCode = null
                        adultLocked = false
                    } else {
                        passCode.toInt()
                        adultLocked = true
                    }
                } catch (e: Exception) {
                    Log.e("TAG Exception", "parseCmd: ${e.message}")
                    guestLastName = lastNameAndPass
                    passCode = null
                    adultLocked = false
                }

            }

            cmdDataDto = CmdDataDto(
                sessionId,
                parentSessionId,
                adultContentDisabled,
                message,
                guestFirstName,
                guestLastName,
                adultLocked,
                passCode
            )

        }
        return CmdDto(kapingCMD, epochTime, transactionId, cmdDataDto)
    }

    private fun handleKaping(kapingResponse: KapingResponse?) {
        when (kapingResponse?.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE,
            KapingConstants.KAP_CMD_CHECK_IN,
            KapingConstants.KAP_CMD_CHECK_OUT,
            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                when (activityStack.last()) {
                    SerialActivity::class.java.simpleName,
                    STBDetailsActivity::class.java.simpleName,
                    RegisterSTBActivity::class.java.simpleName -> {
                        handleCmdInBackground(kapingResponse)
                    }

                    else -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }
                }
            }

            KapingConstants.KAP_CMD_HS_CHANGE -> {
                when (activityStack.last()) {
                    HotelInfoActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchHotelServiceInfo(Constants.UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                when (activityStack.last()) {
                    LocalAttractionActivity::class.java.simpleName,
                    GuestServiceActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchLocalAttractionInfo(Constants.UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                when (activityStack.last()) {
                    MoviesActivity::class.java.simpleName,
                    ExoPlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchSyncList(Constants.UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                when (activityStack.last()) {
                    ShowtimeActivity::class.java.simpleName,
                    ExoPlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchShowtimeData(Constants.UA)
                    }
                }
            }
        }

    }

    private fun handleCmdInRefreshingUi(kapingResponse: KapingResponse) {
        val i = Intent(applicationContext, RefreshingUiActivity::class.java)
        i.putExtra("response", kapingResponse)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(i)
    }

    private fun handleCmdInBackground(kapingResponse: KapingResponse) {
        when (kapingResponse.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> {
                fetchAccountSetupDetails(Constants.ACTIVATE, Constants.UA, Constants.MODE)
            }

            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                fetchThemeDetails(Constants.UA)
            }

            KapingConstants.KAP_CMD_CHECK_IN -> {
                handleCheckInCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> {
                handleCheckOutCmd(kapingResponse)
            }
        }
    }

    private fun fetchAccountSetupDetails(cmd: String, ua: String, mode: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getAccountSetupDetails(cmd, ua, mode)
            if (response != null) {
                updateAccountSetupData(accountSetupDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchThemeDetails(ua: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getThemeDetails(ua)
            if (response != null) {
                updateThemeData(themeDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchHotelServiceInfo(ua: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getHotelServiceInfo(ua)
            if (response != null) {
                updateHotelServices(hotelServicesDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchLocalAttractionInfo(ua: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getLocalAttractionInfo(ua)
            if (response != null) {
                updateLocalAttractions(localAttractionsDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchSyncList(ua: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getMoviesInfo(ua)
            if (response != null) {
                setMoviesResponseData(moviesDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchShowtimeData(ua: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = movieBeamRepository.getShowtimeInfo(ua)
            if (response != null) {
                updateShowTimeData(showtimeDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            true,
            kapingResponse.cmdData?.cmdData
        )
    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            false,
            kapingResponse.cmdData?.cmdData
        )
    }

    fun updateGuestSession(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        guestDetailsDatastore: DataStore<CmdDataDto>,
        isCheckedIn: Boolean,
        guestDetails: CmdDataDto?
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN,
                isCheckedIn
            )
            updateGuestDetails(guestDetailsDatastore, guestDetails)
        }
    }

    private fun updateGuestDetails(
        dataStore: DataStore<CmdDataDto>,
        data: CmdDataDto?
    ) {

        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    sessionId = data?.sessionId,
                    parentSessionId = data?.parentSessionId,
                    adultContentDisabled = data?.adultContentDisabled,
                    message = data?.message,
                    guestFirstName = data?.guestFirstName,
                    guestLastName = data?.guestLastName,
                    adultLocked = data?.adultLocked,
                    passcode = data?.passcode
                )
            }
        }
    }


    fun updateAccountSetupData(
        dataStore: DataStore<AccountSetupResponse>,
        data: AccountSetupResponse
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    address = data.address,
                    adultContent = data.adultContent,
                    airportCode = data.airportCode,
                    amenityPasscodeStatus = data.amenityPasscodeStatus,
                    analogService = data.analogService,
                    appsList = data.appsList,
                    appsVisible = data.appsVisible,
                    appsWorld = data.appsWorld,
                    blankChannelBroadcastType = data.blankChannelBroadcastType,
                    blankChannelLcn = data.blankChannelLcn,
                    blankChannelMajor = data.blankChannelMajor,
                    blankChannelMinor = data.blankChannelMinor,
                    blankTvChannelBroadcastType = data.blankTvChannelBroadcastType,
                    buttonsList = data.buttonsList,
                    castingVisible = data.castingVisible,
                    cityCode = data.cityCode,
                    clipType = data.clipType,
                    cloudEnabled = data.cloudEnabled,
                    companyName = data.companyName,
                    conciergeList = data.conciergeList,
                    contactNo = data.contactNo,
                    contentDetailFlag = data.contentDetailFlag,
                    customerCareNo = data.customerCareNo,
                    defaultLanguage = data.defaultLanguage,
                    email = data.email,
                    enableInroomDining = data.enableInroomDining,
                    enableNdvr = data.enableNdvr,
                    enableShowtime = data.enableShowtime,
                    enableShowtimeStatus = data.enableShowtimeStatus,
                    enableStbLogging = data.enableStbLogging,
                    enableWebRemote = data.enableWebRemote,
                    epgCdnUrl = data.epgCdnUrl,
                    epgDuration = data.epgDuration,
                    feedBackVisible = data.feedBackVisible,
                    fetchServerIp = data.fetchServerIp,
                    fetchServerPort = data.fetchServerPort,
                    flightStatusDisclaimer = data.flightStatusDisclaimer,
                    gatewayIpAddress = data.gatewayIpAddress,
                    gsButtonsList = data.gsButtonsList,
                    guestServicesVisible = data.guestServicesVisible,
                    hotelChannelList = data.hotelChannelList,
                    hotelDetailFlag = data.hotelDetailFlag,
                    hotelDisplayName = data.hotelDisplayName,
                    hotelInfo = data.hotelInfo,
                    hotelModel = data.hotelModel,
                    hotelPlan = data.hotelPlan,
                    hotelPlanId = data.hotelPlanId,
                    hotelServicesVisible = data.hotelServicesVisible,
                    httpStreamingCdnUrl = data.httpStreamingCdnUrl,
                    httpStreamingHotelvideoUrl = data.httpStreamingHotelvideoUrl,
                    id = data.id,
                    itemMenuList = data.itemMenuList,
                    languageWiseHotelInfoList = data.languageWiseHotelInfoList,
                    languagesList = data.languagesList,
                    liveTVVisible = data.liveTVVisible,
                    mainCastingHdmi = data.mainCastingHdmi,
                    mbloggerCloudUrl = data.mbloggerCloudUrl,
                    multiLingual = data.multiLingual,
                    ndvrIp = data.ndvrIp,
                    ndvrPort = data.ndvrPort,
                    pairDeviceVisible = data.pairDeviceVisible,
                    prgGuideVisible = data.prgGuideVisible,
                    proxyIp = data.proxyIp,
                    proxyPort = data.proxyPort,
                    proxyVodIp = data.proxyVodIp,
                    proxyVodPort = data.proxyVodPort,
                    roomNo = data.roomNo,
                    samsungBroadcastType = data.samsungBroadcastType,
                    samsungChannelType = data.samsungChannelType,
                    samsungMainmenuAccessText = data.samsungMainmenuAccessText,
                    samsungModulationType = data.samsungModulationType,
                    selectedAppsList = data.selectedAppsList,
                    showToiletryPriceColumn = data.showToiletryPriceColumn,
                    showVegNonVegImg = data.showVegNonVegImg,
                    showtimesVisible = data.showtimesVisible,
                    softwareDownloadIp = data.softwareDownloadIp,
                    softwareDownloadPort = data.softwareDownloadPort,
                    stbCastingImageUrl = data.stbCastingImageUrl,
                    stbZoneId = data.stbZoneId,
                    streamingChannelBroadcastType = data.streamingChannelBroadcastType,
                    streamingIp = data.streamingIp,
                    streamingPort = data.streamingPort,
                    streamingType = data.streamingType,
                    streamingTypeId = data.streamingTypeId,
                    temperatureUnit = data.temperatureUnit,
                    timeZoneCode = data.timeZoneCode,
                    tvBroadcastType = data.tvBroadcastType,
                    tvSerialControl = data.tvSerialControl,
                    tvVodTuningType = data.tvVodTuningType,
                    type = data.type,
                    vodMgrIp = data.vodMgrIp,
                    vodMgrPort = data.vodMgrPort,
                    vodVisible = data.vodVisible,
                    welcomeScreenVisible = data.welcomeScreenVisible
                )

            }
        }
    }

    fun updateThemeData(
        dataStore: DataStore<ThemeResponse>,
        data: ThemeResponse
    ) {

        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    fontCss = data.fontCss,
                    gradientColor = data.gradientColor,
                    id = data.id,
                    spotLightColor = data.spotLightColor,
                    themeBackgroundFileName = data.themeBackgroundFileName,
                    themeLogoFileNameCloud = data.themeLogoFileNameCloud,
                    themeBgFileName = data.themeBgFileName,
                    themeBgFileNameCloud = data.themeBgFileNameCloud,
                    themeCss = data.themeCss,
                    type = data.type,
                    version = data.version,
                    themeBackgroundFileNameCloud = data.themeBackgroundFileNameCloud,
                    themeLogoFileName = data.themeLogoFileName
                )

            }
        }
    }

    private fun updateHotelServices(
        dataStore: DataStore<HotelServiceResponse>,
        data: HotelServiceResponse
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    servicesList = data.servicesList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    private fun updateLocalAttractions(
        dataStore: DataStore<LocalAttractionResponse>,
        data: LocalAttractionResponse
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    servicesList = data.servicesList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    private fun setMoviesResponseData(
        dataStore: DataStore<MoviesResponse>,
        data: MoviesResponse
    ) {

        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    adultDayPassPrice = data.adultDayPassPrice,
                    freeContentList = data.freeContentList,
                    freeGenreList = data.freeGenreList,
                    premiumContentList = data.premiumContentList,
                    premiumGenreList = data.premiumGenreList,
                    id = data.id,
                    type = data.type,
                    version = data.version
                )

            }
        }
    }

    fun updateShowTimeData(
        dataStore: DataStore<ShowTimeResponse>,
        data: ShowTimeResponse
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    id = data.id,
                    shoContentList = data.shoContentList,
                    shoGenreList = data.shoGenreList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    private fun getVersionNumber(): String {
        return BuildConfig.VERSION_NAME
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        /*val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityOptions.makeBasic()
        } else {
            ActivityOptions.makeTaskLaunchBehind()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            options.isPendingIntentBackgroundActivityLaunchAllowed = true
        }*/

        val pendingIntent: PendingIntent =
            Intent(this, MainMenuActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                Notification.Builder(this, notificationChannelId) else Notification.Builder(this)

        Log.e("createNotification: ", "Endless")

        return builder
            .setContentTitle("Endless Service")
            .setContentText("This is your favorite endless service working")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    private fun isScreenOn(): String {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return if (pm.isInteractive) KapingConstants.POWER_MODE_ON else KapingConstants.POWER_MODE_STAND_BY
    }

    private fun getCurrentPanelNumber(): String {
        when (activityStack.last()) {
            //TODO Register Stb Page
            STBDetailsActivity::class.java.simpleName -> return PanelConstants.LOADER_SCREEN
            MainMenuActivity::class.java.simpleName -> return PanelConstants.MAIN_MENU
            MoviesActivity::class.java.simpleName -> return PanelConstants.VOD
            HotelInfoActivity::class.java.simpleName -> return PanelConstants.HOTEL_SERVICES
            //TODO Live services
            MovieDetailFragment::class.java.simpleName -> return PanelConstants.MOVIE_DETAIL_PAGE
            ProgramGuideActivity::class.java.simpleName -> return PanelConstants.PROGRAM_GUIDE
            HelpInfoFragment::class.java.simpleName -> return PanelConstants.HELP_AND_INFO
            GuestServiceActivity::class.java.simpleName -> return PanelConstants.GUEST_SERVICES
            AppWorldActivity::class.java.simpleName -> return PanelConstants.APP_WORLD
            ExoPlayerActivity::class.java.simpleName -> return PanelConstants.MOVIE_SHOWTIME_PLAYER_PAGE
            PrgGuidePlayerActivity::class.java.simpleName -> return PanelConstants.FULL_SCREEN_TV
            ShowtimeActivity::class.java.simpleName -> return PanelConstants.SHOWTIME_CONTENT_LISTENING
            ShowtimeDetailFragment::class.java.simpleName -> return PanelConstants.SHOWTIME_CONTENT_DETAIL_PAGE
            CastingActivity::class.java.simpleName -> return PanelConstants.CASTING_PAGE
            //TODO Pairing Page
            //TODO Inroom Dining Page
            //TOdo Food Delivery
            //TODO Crackle
            //TODO NDVR
            //TODO CALENDER

            else -> return PanelConstants.MAIN_MENU
        }
    }

}
