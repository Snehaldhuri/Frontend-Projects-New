package com.diipl.moviebeam.service.kappingservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tv.settings.aidl.regular.IDeviceNameConfigureCallback
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.datastore.UpdateDataStore
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassSync
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalSyncResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.sysInfo.SysInfoDTO
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.kaping.CmdDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_CONTENT_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_FINISH_TIME
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.NETWORK_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.dialogs.AdultContentDialog
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.localattraction.LocalAttractionActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.movies.MovieDetailFragment
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.newprogramguide.NewProgramGuideActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.programguide.PrgGuidePlayerActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiActivity
import com.diipl.moviebeam.ui.serial_info.SerialActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.GLOBAL_LOOP_SEC
import com.diipl.moviebeam.utils.Constants.MDM_PACKAGE_NAME
import com.diipl.moviebeam.utils.DeviceUtils
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.KapingResponseParsing
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.clearCredentials
import com.diipl.moviebeam.utils.compareVersions
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.isNotAllowed
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.logK
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.scheduleMsgEndTask
import com.diipl.moviebeam.utils.setIPInfo
import com.diipl.moviebeam.utils.toInteger
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toTimestamp
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class EndlessService : Service() {

    //Variables from datastore
    private var accountId: String = ""
    private var serialNo = ""
    private var ua = ""
    private var stbRoomNo: String = ""
    private var epgStartTime = ""
    private var epgEndTime = ""
    private var channelCount = 0
    private var appList = ArrayList<String>()

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
    private var showtimeVersion = ""
    private var hotelServicesVersion = ""
    private var CMDRES = ""
    private var EVENT = ""
    private var epochTime = ""
    private var transactionId = ""
    private var isEPGServerApiCalled = false
    private var isNetworkAvailable = true

    private val _accountSetupLiveData = MutableLiveData<AccountSetupResponse>()
    val accountSetupLiveData: LiveData<AccountSetupResponse> get() = _accountSetupLiveData

    private val _themeLiveData = MutableLiveData<ThemeResponse>()
    val themeLiveData: LiveData<ThemeResponse> get() = _themeLiveData

    private val _localAttractionLiveData = MutableLiveData<LocalAttractionResponse>()
    val localAttractionLiveData: LiveData<LocalAttractionResponse> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<MoviesResponse>()
    val moviesLiveData: LiveData<MoviesResponse> get() = _moviesLiveData

    private val _showtimeLiveData = MutableLiveData<ShowTimeResponse>()
    val showtimeLiveData: LiveData<ShowTimeResponse> get() = _showtimeLiveData

    private val _hotelServicesLiveData = MutableLiveData<HotelServiceResponse>()
    val hotelServicesLiveData: LiveData<HotelServiceResponse> get() = _hotelServicesLiveData

    private val _channelListLiveData = MutableLiveData<ChannelListResponse>()
    val channelListLiveData: LiveData<ChannelListResponse> get() = _channelListLiveData

    private val _guestDetailsLiveData = MutableLiveData<Resource<CmdDataDto>>()
    val guestDetailsLiveData: LiveData<Resource<CmdDataDto>> get() = _guestDetailsLiveData

    private var _isGuestCheckedInLiveData = MutableLiveData<Boolean>()
    val isGuestCheckedInLiveData: LiveData<Boolean> get() = _isGuestCheckedInLiveData

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var deviceUtils: DeviceUtils

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
    lateinit var channelListDatastore: DataStore<ChannelListResponse>

    @Inject
    lateinit var tickerDatastore: DataStore<TickerResponse>

    @Inject
    lateinit var messageDatastore: DataStore<MessageResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var updateDataStore: UpdateDataStore

    @Inject
    lateinit var hardwareAPI: HardwareAPI

    companion object {
        var isServiceStarted = false
        var AS_FLAG = false
        var kapingCMD = KapingConstants.KAP_CMD_DO_NOTHING
        var kapingCmdExecutionResponse = KapingConstants.PENDING_EXECUTION

        val gson = GsonBuilder().setLenient().create()

        private fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
                .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS).build()
        }

        fun createRetrofitService(): LgRestApiService {
            val retrofit = Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.BASE_URL_ACCOUNT_SETUP).client(provideOkHttpClient()).build()

            return retrofit.create(LgRestApiService::class.java)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        logD("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logD("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            logD("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> logE("This should never happen. No action in the received intent")
            }
        } else {
            logE("with a null intent. It has been probably restarted by the system.")
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        logD("The service has been created".uppercase(Locale.ROOT))
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        versionNumber = getVersionNumber()
        logD(versionNumber)
        this.initializeDatastoreParams()

        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
//        filter.addAction(Intent.CATEGORY_HOME)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(homePressReceiver, filter)

        // TODO
        var isSwitched = false
        var count = 0
        CoroutineScope(Dispatchers.IO).launch {
           try {
               while (activityStack.last()?.isNotEmpty() == true) {
                   if (activityStack.last() != RegisterSTBActivity::class.java.simpleName)
                       if (activityStack.last() != STBDetailsActivity::class.java.simpleName) {
                           preferenceDataStoreHelper.putPreference(NETWORK_STATUS, isNetworkAvailable)
                           if (!isNetworkAvailable && !isSwitched) {
                               BaseActivity.currentActivity?.launchLogger()
                               isSwitched = true
                               startMainMenu()
                               count = 0
                           }
                           if (isNetworkAvailable && isSwitched) {
                               BaseActivity.currentActivity?.launchLogger()
                               isSwitched = false
                               if (count == 0) {
                                   startMainMenu()
                                   count++
                               }
                           }
                       }
                   delay(1000 * 2)
               }
           } catch (e: Exception){
               Log.e("TAG", "activityStack: ${e.localizedMessage}")
               BaseActivity.currentActivity?.launchLogger()
               MainMenuActivity::class.java.startActivity()
           }
        }

        val notification = createNotification()
        startForeground(1, notification)

    }

    private val homePressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BaseActivity.currentActivity?.javaClass?.simpleName?.isNotAllowed() == true) {
                intent.let {
                    when (it.action) {
                        Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                            val reason = it.getStringExtra("reason")
                            if (reason == "homekey") {
//                                if (BaseActivity.currentActivity?.javaClass?.simpleName?.isNotAllowed() == true) {
                                if (activityStack.last() == AppWorldActivity::class.java.simpleName) {
                                    if (AppWorldActivity.NETFLIX_LAUNCHED) {
                                        val sessionId = GuestDetails.SESSION_ID
                                        val url =
                                            "${Constants.BASE_URL_LG_REST}content/netflixAccess/enter?sessionId=$sessionId"

                                        val requestBody = createRequestBody(
                                            stbRoomNo, ua, 2
                                        )

                                        postRequest(url, requestBody)
                                        AppWorldActivity.NETFLIX_LAUNCHED = false
                                        return
                                    } else {
                                        startMainMenu()
                                        return
                                    }
                                } else {
                                    if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_CHROMECAST) {
                                        if(activityStack.last() == ProgramGuideActivity::class.java.simpleName){
                                            (BaseActivity.currentActivity as NewProgramGuideActivity).switchToHDMI()
                                        } else  if(activityStack.last() == DisconnectedPrgActivity::class.java.simpleName){
                                            (BaseActivity.currentActivity as DisconnectedPrgActivity).switchToHDMI()
                                        }
                                        MainMenuActivity::class.java.startActivity()
                                        return
                                    } else {
                                        MainMenuActivity::class.java.startActivity()
                                        return
                                    }
                                }
                            } else {
                                return
                            }
                        }

                        Intent.ACTION_SCREEN_OFF -> {

                        }

                        Intent.ACTION_SCREEN_ON -> {
                            CoroutineScope(Dispatchers.Default).launch {
                                delay(10000)
                                startMainMenu()
                            }
                        }

                        Intent.ACTION_MEDIA_BUTTON -> {
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun startMainMenu() {
        startActivity(Intent(applicationContext, MainMenuActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    private fun <T> Class<T>.startActivity() {
        startActivity(Intent(applicationContext, this).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }

    private fun createRequestBody(roomNo: String, UA: String, accessType: Int): String {
        val netflixDetails = JSONObject().apply {
            put("stbRoomNo", roomNo)
            put("ua", UA)
            put("accessType", accessType)
        }
        return netflixDetails.toString()
    }

    private fun postRequest(url: String, requestBody: String) {
        val client = OkHttpClient()

        val request = Request.Builder().url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody)).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                logE("Network error: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    logD("netflixDataModel url success")
                } else {
                    val responseBody = response.body?.string() ?: "No response body"
                    val responseCode = response.code
                    logE("netflixDataModel url failed. Response code: $responseCode, Response body: $responseBody")
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        logD("The service has been destroyed".uppercase(Locale.ROOT))
        unregisterReceiver(homePressReceiver)
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    @Suppress("NullSafeMutableLiveData")
    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        if (isServiceStarted) return
        logD("Starting the foreground service task")
//        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire()
            }
        }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            delay(15000)
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    setIPInfo()

                    UA = preferenceDataStoreHelper.getFirstPreference(
                        PreferenceDataStoreConstants.UA, ""
                    )
                    _accountSetupLiveData.postValue(accountSetupDataStore.data.first())
                    _themeLiveData.postValue(themeDataStore.data.first())
                    _localAttractionLiveData.postValue(localAttractionsDataStore.data.first())
                    _moviesLiveData.postValue(moviesDataStore.data.first())
                    _showtimeLiveData.postValue(showtimeDataStore.data.first())
                    _hotelServicesLiveData.postValue(hotelServicesDataStore.data.first())
                    _channelListLiveData.postValue(channelListDatastore.data.first())
                    isGuestCheckedIn = preferenceDataStoreHelper.getFirstPreference(
                        PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY, false
                    )
                    logD("UA -> $UA")
                    if (UA.isNotBlank()) {
                        pingFakeServer()
                        callKapingApi()

                        if (GuestDetails.SESSION_ID.isNotEmpty() && GuestDetails.SESSION_ID != "null") roomRepository.removeOverTimeMovies()

                        if (GuestDetails.SESSION_ID == "null") {
                            roomRepository.deleteRecentMovies()
                            roomRepository.deleteRecentShows()
                            removeAdultData()
                        }
                    }
                }
                delay(GLOBAL_LOOP_SEC * 1000L)
            }
            logD("End of the loop for the service")
        }
    }


    private fun stopService() {
        logD("Stopping the foreground service")
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
            logE("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun pingFakeServer() {
        counter++
        logD(counter.toString())

        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.getFirstPreference(ADULT_DAY_PASS_FINISH_TIME, 0).let {
                if (it <= System.currentTimeMillis()) {
                    preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, false)
                }
            }
        }

    }

    private fun callKapingApi() {

        val themeVersion1 = themeLiveData.value?.version
        if (!themeVersion1.isNullOrEmpty()) {
            themeVersion = themeVersion1
        }
//        log(themeVersion1.toString())

        val laVersion1 = localAttractionLiveData.value?.version
        if (!laVersion1.isNullOrEmpty()) {
            laVersion = laVersion1
        }
//        log(laVersion1.toString())

        val moviesVersion1 = moviesLiveData.value?.version
        if (!moviesVersion1.isNullOrEmpty()) {
            moviesVersion = moviesVersion1
        }
//        log(moviesVersion1.toString())

        val showtimeVersion1 = showtimeLiveData.value?.version
        if (!showtimeVersion1.isNullOrEmpty()) {
            showtimeVersion = showtimeVersion1
        }
//        log(showtimeVersion1.toString())

        val hotelServicesVersion1 = hotelServicesLiveData.value?.version
        if (!hotelServicesVersion1.isNullOrEmpty()) {
            hotelServicesVersion = hotelServicesVersion1
        }
//        log(hotelServicesVersion1.toString())

        var epoch =
            parseInt(((Date().time / 1000).toString())).toString(16); //Date => timestamp => HEX
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
            epochTime = epoch
        }
        CMDRES = if (kapingCMD == "00") {
            ""
        } else {
            "$kapingCMD$epochTime$transactionId$kapingCmdExecutionResponse"
        }
        logD("CMDRES -> $CMDRES")

        EVENT =
            isScreenOn() + getCurrentPanelNumber() + if (isGuestCheckedIn) KapingConstants.EVENT_CHECK_IN else KapingConstants.EVENT_CHECK_OUT
        logD("EVENT -> $EVENT")

        val kapingCall = myApiService.getKapingService(
            Constants.KAPING,
            UA,
            "0",
            versionNumber,
            moviesVersion,
            Constants.DV,
            "1",
            EVENT,
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
                call: Call<String>, response: Response<String>
            ) {
                if (response.isSuccessful) {
                    isNetworkAvailable = true
                    val data = response.body()
                    val result = KapingResponseParsing().getResponseAsObject(
                        data, KapingResponse::class
                    )
                    result?.CMD?.let {
                        result.cmdData = parseCmd(it)
                    }
                    // Handle the data here
                    result?.CMD?.let { cmd -> logCmdSignal(cmd) }
//                    log(result.toString())
                    kapingCmdExecutionResponse = KapingConstants.PENDING_EXECUTION

                    AS_FLAG = if (result?.AS.isNullOrEmpty()) {
                        updateStbAllocationStatus(preferenceDataStoreHelper, true)
                        true
                    } else {
                        updateStbAllocationStatus(preferenceDataStoreHelper, false)
                        if (activityStack.last() != RegisterSTBActivity::class.java.simpleName) {
                            RegisterSTBActivity::class.java.startActivity()
                        }
                        false
                    }

                    handleKaping(result)
                } else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                isNetworkAvailable = false
                logE(t.toString())
            }

        })

    }

    private val commandMessages = mapOf(
        KapingConstants.KAP_CMD_CHECK_IN to "Signal for check in command",
        KapingConstants.KAP_CMD_CHECK_OUT to "Signal for check out command",
        KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE to "Signalling to get account set up",
        KapingConstants.KAP_CMD_REBOOT to "Signal to reboot the system",
        KapingConstants.KAP_CMD_SEND_SYS_INFO to "Signalling to send sys info",
        KapingConstants.KAP_CMD_THEME_CHANGE to "Signal to get theme",
        KapingConstants.KAP_CMD_LA_CHANGE to "Signal to get LA",
        KapingConstants.KAP_CMD_HS_CHANGE to "Signalling to get HS",
        KapingConstants.KAP_CMD_FETCH_SYNC_LIST to "Signal to Fetch Sync List",
        KapingConstants.KAP_CMD_ENABLE_DISABLE_ADULT_CONTENT to "Signalling to enable/Disable Adult Content",
        KapingConstants.KAP_CMD_GET_EPG_DATA to "Signalling to Get EPG Data",
        KapingConstants.KAP_CMD_GET_CHANNEL_LIST to "Signalling to Get Channel List Data",
        KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA to "Signal to Fetch Showtime Data",
        KapingConstants.KAP_CMD_GET_GUEST_MESSAGES to "Signalling to get guest messages",
        KapingConstants.KAP_CMD_SYNC_RECENT_VIEWED to "Signalling to sync recently viewed",
        KapingConstants.KAP_CMD_SYNC_ADULT_DAYPASS to "Signalling to sync adult daypass",
        KapingConstants.KAP_CMD_GET_TICKER_MESSAGES to "Signalling to get ticker messages",
        KapingConstants.KAP_CMD_SOFTWARE_UPDATE to "Signalling to get Software Update"
    )

    private fun logCmdSignal(cmd: String) {
        commandMessages[cmd]?.let { logK(it) }
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
                adultContentDisabled = cmdData[0] == '0'
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
                        passCode.toInteger()
                        adultLocked = true
                    }
                } catch (e: Exception) {
                    logE("parseCmd: ${e.message}")
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


            if (adultLocked != null) {
                updateAdultContent(adultLocked)
            } else updateAdultContent(false)
            if (passCode != null) {
                updateParentalPassCode(passCode)
            } else {
                updateParentalPassCode("____")
            }

        }
        return CmdDto(kapingCMD, epochTime, transactionId, cmdDataDto)
    }

    private fun updateParentalPassCode(passCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            sharedPreference.adultPassCode = passCode
        }
    }

    private fun handleKaping(kapingResponse: KapingResponse?) {

//        Log.e(TAG, "handleKaping: ${kapingResponse?.cmdData?.cmd}")

        when (kapingResponse?.cmdData?.cmd) {

            KapingConstants.KAP_CMD_SOFTWARE_UPDATE -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val response = movieBeamRepository.getSoftwareUpdateDetails(
                        BuildConfig.BUILD_TYPE_ID,
                        ua
                    )
                    if (response != null) {
                        val isUpgradeable = compareVersions(response.softwareVersion)
                        if (isUpgradeable) {
                            logD("handleKaping: isUpgradable: $isUpgradeable  ${BuildConfig.VERSION_NAME} $response")
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.component =
                                ComponentName(
                                    MDM_PACKAGE_NAME,
                                    KapingConstants.MDM_SOFTWARE_ACTIVITY
                                )
                            intent.putExtra("softwareData", response.toJson())
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                    }
                }

            }

            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE, KapingConstants.KAP_CMD_CHECK_IN, KapingConstants.KAP_CMD_CHECK_OUT, KapingConstants.KAP_CMD_THEME_CHANGE -> {
                removeAdultData()
                when (activityStack.last()) {
                    SerialActivity::class.java.simpleName, STBDetailsActivity::class.java.simpleName, RegisterSTBActivity::class.java.simpleName -> {
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
                        fetchHotelServiceInfo(accountId)
                    }
                }
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                when (activityStack.last()) {
                    LocalAttractionActivity::class.java.simpleName, GuestServiceActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchLocalAttractionInfo(UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                when (activityStack.last()) {
                    MoviesActivity::class.java.simpleName, MovieDetailFragment::class.java.simpleName, AdultContentDialog::class.java.simpleName, ExoPlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchSyncList(UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                when (activityStack.last()) {
                    ShowtimeActivity::class.java.simpleName, ExoPlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchShowtimeData(UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_GET_CHANNEL_LIST -> {
                when (activityStack.last()) {
                    ProgramGuideActivity::class.java.simpleName, PrgGuidePlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchChannelList(UA)
                    }
                }
            }

            KapingConstants.KAP_CMD_GET_EPG_DATA -> {
                when (activityStack.last()) {
                    ProgramGuideActivity::class.java.simpleName, PrgGuidePlayerActivity::class.java.simpleName -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }

                    else -> {
                        fetchEPGData()
                    }
                }
            }

            KapingConstants.KAP_CMD_SYNC_RECENT_VIEWED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    kapingResponse.CMD?.let { str ->
                        val data = str.substring(19, str.length)
                        val syncResponse = data.fromJson<RentalSyncResponse>()
                        moviesLiveData.value?.let { res ->
                            syncResponse.syncList.forEach { sync ->
                                res.premiumContentList?.forEach {
                                    if (sync.releaseId == it.releaseId && sync.productId == it.productId) {
                                        val model = RentalMovieModel()
                                        model.movieData = it
                                        model.rentalID = sync.rentalId
                                        model.sessionID = GuestDetails.SESSION_ID
                                        model.currentSeek = sync.seek
                                        model.startTimeStamp = sync.rentalTime.toTimestamp()
                                        model.lastTimeStamp = System.currentTimeMillis()
                                        roomRepository.insertRentalMovies(model)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            KapingConstants.KAP_CMD_SYNC_ADULT_DAYPASS -> {
                CoroutineScope(Dispatchers.IO).launch {
                    kapingResponse.CMD?.let { str ->
                        val data = str.substring(19, str.length)
                        val syncResponse = data.fromJson<AdultDayPassSync>()
                        syncResponse.dayPassList.forEach {
                            it.let {
                                preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, true)
                                preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_FINISH_TIME,
                                    it.dayPassRentalTime.toTimestamp().also { time ->
                                        time.plus(24 * 60 * 60 * 1000)
                                    })
                            }
                        }
                    }

                }
            }

            KapingConstants.KAP_CMD_ENABLE_DISABLE_ADULT_CONTENT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    kapingResponse.CMD?.let { str ->
                        val isEnabled = str[19] == '1'
                        updateAdultContent(isEnabled)
                    }
                }
            }

            KapingConstants.KAP_CMD_SEND_SYS_INFO -> {
                CoroutineScope(Dispatchers.IO).launch {
                    handleSysInfoCmd()
                }
            }

            KapingConstants.KAP_CMD_REBOOT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    handleRebootCmd()
                }
            }

            KapingConstants.KAP_CMD_GET_TICKER_MESSAGES -> {
                CoroutineScope(Dispatchers.IO).launch {
                    handleTickerMsgCmd(UA)
                }
            }

            KapingConstants.KAP_CMD_GET_GUEST_MESSAGES -> {
                when (activityStack.last()) {
                    SerialActivity::class.java.simpleName, STBDetailsActivity::class.java.simpleName, RegisterSTBActivity::class.java.simpleName -> {
                        handleGuestMsgCmd()
                    }

                    else -> {
                        handleCmdInRefreshingUi(kapingResponse)
                    }
                }
            }
        }
    }

    private fun handleCmdInRefreshingUi(kapingResponse: KapingResponse) {
        when (kapingResponse.cmdData?.cmd) {
            KapingConstants.KAP_CMD_CHECK_IN -> {
                resetPopUps(true)
                kapingResponse.CMD?.let {
                    if (it.length > 19) {
                        val isEnabled = it[19] == '1'
                        updateAdultContent(isEnabled)
                    }
                }
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> resetPopUps(false)
        }
        val i = Intent(applicationContext, RefreshingUiActivity::class.java)
        i.putExtra("response", kapingResponse.toJson())
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    private fun handleCmdInBackground(kapingResponse: KapingResponse) {
        when (kapingResponse.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> {
                fetchAccountSetupDetails(Constants.ACTIVATE, UA, Constants.MODE)
            }

            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                fetchThemeDetails(UA)
            }

            KapingConstants.KAP_CMD_CHECK_IN -> {
                resetPopUps(true)
                kapingResponse.CMD?.let {
                    if (it.length > 19) {
                        val isEnabled = it[19] == '1'
                        updateAdultContent(isEnabled)
                    }
                }
                handleCheckInCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> {
                resetPopUps(false)
                handleCheckOutCmd(kapingResponse)
                GuestDetails.SESSION_ID = "null"
            }
        }
    }

    private fun resetPopUps(isIn: Boolean) {
        if (!isIn) sharedPreference.adultPassCode = "____"
        sharedPreference.isAdultLocked = true
        sharedPreference.isMainAdultMCW = false
        sharedPreference.isBtnAdultMCW = false
        sharedPreference.isAdultMCD = false

    }

    private fun fetchAccountSetupDetails(cmd: String, ua: String, mode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getAccountSetupDetails(cmd, ua, mode)
            if (response != null) {
                if(BuildConfig.BUILD_TYPE==Constants.BUILD_TYPE_STB) {
                    hardwareAPI.myService?.setDeviceName(
                        "MBAP_${response.accountId}_${response.roomNo}",
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
                updateAccountSetupData(accountSetupDataStore, response)
                CoroutineScope(Dispatchers.Default).launch {
                    preferenceDataStoreHelper.putPreference(
                        PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
                        response.accountId
                    )
                    preferenceDataStoreHelper.putPreference(
                        PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
                        response.roomNo
                    )
                    preferenceDataStoreHelper.putPreference(
                        PreferenceDataStoreConstants.EPG_CDN_URL_KEY,
                        response.epgCdnUrl + response.accountId + Constants.EPG_CLOUD_URL_SUFFIX
                    )
                    preferenceDataStoreHelper.putPreference(
                        PreferenceDataStoreConstants.CASTING_URL_KEY,
                        response.stbCastingPageUrl
                    )
                    if (response.contentDetailFlag)
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY,
                            response.httpStreamingHotelvideoUrl + response.hotelChannelList[0].fileName
                        )
                }

                scheduleClearCredentialsTask(response.checkOutTime)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Account Setup callback Success")
            } else {
                logE("In Account Setup callback Fail")
            }
        }
    }

    private fun fetchThemeDetails(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getThemeDetails(ua)
            if (response != null) {
                updateThemeData(response)
                ThemeDetails.GRADIENT_COLOR_START = response.gradientColor
                ThemeDetails.GRADIENT_COLOR_END = response.spotLightColor
                ThemeDetails.GRADIENT = null
                ThemeDetails.GRADIENT = getGradientColor()
                ThemeDetails.BG_IMAGE = response.themeBackgroundFileName
                ThemeDetails.LOGO_IMAGE = response.themeLogoFileName
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
//                startUpdateDataWorker(UpdateDataWorker.ACTION_THEME)
                logD("In Theme callback Success")
                logD("Theme current version: ${response.version}")
            } else {
                logE("In Theme callback Fail")
            }
        }
    }

    private fun fetchTickerMessage(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getTvTickerMessages(ua)
            if (response != null) {
                val sdf = SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    response.tvTickerList?.removeIf { msg ->
                        (msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true)
                                || sdf.parse(msg.etStr!!)!!.before(Date())
                    }
                } else {
                    val iterator = response.tvTickerList?.iterator()
                    while (iterator!!.hasNext()) {
                        val msg: TvTickerDTO = iterator.next()
                        if ((msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true)
                            || sdf.parse(msg.etStr!!)!!.before(Date())
                        ) {
                            iterator.remove()
                        }
                    }
                }
                updateTickerMessage(tickerDatastore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                response.tvTickerList?.forEach {
                    applicationContext.scheduleMsgEndTask(it)
                }
                logD("In Ticker message callback Success")
                logD("Ticker current version: ${response.version}")
            } else {
                logE("In Ticker message callback Fail")
            }
        }
    }

    private fun fetchGuestMessage(ua: String, guestSessionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getGuestMessages(ua, guestSessionId)
            if (response != null) {
                updateGuestMessage(messageDatastore, response)
                logD("In Guest message callback Success")
            } else {
                logE("In Guest message callback Fail")
            }
        }
    }

    private fun updateAdultContent(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            sharedPreference.isAdultContentEnabled = enabled
            preferenceDataStoreHelper.putPreference(ADULT_CONTENT_STATUS, enabled)
        }
    }

    private fun removeAdultData() {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, false)
        }
    }

    private fun fetchHotelServiceInfo(accountId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getHotelServiceInfo(accountId)
            if (response != null) {
                updateHotelServices(response)
//                startUpdateDataWorker(UpdateDataWorker.ACTION_HS)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Hotel Services callback Success")
                logD("Hotel Services current version: ${response.version}")
            } else {
                logE("In Hotel Services callback Fail")
            }
        }
    }

    private fun fetchLocalAttractionInfo(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getLocalAttractionInfo(ua)
            if (response != null) {
                updateLocalAttractions(response)
//                startUpdateDataWorker(UpdateDataWorker.ACTION_LA)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Local Attractions callback Success")
                logD("Local Attraction current version: ${response.version}")
            } else {
                logE("In Local Attractions callback Fail")
            }
        }
    }

    private fun fetchSyncList(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getMoviesInfo(ua)
            if (response != null) {
                setMoviesResponseData(moviesDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Releases callback Success")
                logD("Releases current version: ${response.version}")
            } else {
                logE("In Releases callback Fail")
            }
        }
    }

    private fun fetchShowtimeData(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getShowtimeInfo(ua)
            if (response != null) {
                updateShowTimeData(showtimeDataStore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In ShowtimeReleasesCollection callback Success")
                logD("Showtime current version: ${response.version}")
            } else {
                logE("In ShowtimeReleasesCollection callback Fail")
            }
        }
    }

    private fun fetchChannelList(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getChannelList(ua)
            if (response != null) {
                updateChannelList(channelListDatastore, response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                channelCount = response.channelLcnList.size
                updateDatastoreParams(channelCount = channelCount)
                logD("In Channel List callback Success ")
            } else {
                logE("In Channel List callback Fail")
            }
        }
    }

    private fun fetchEPGData() {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                movieBeamRepository.getEPGFromCloud(accountSetupLiveData.value?.epgCdnUrl + accountSetupLiveData.value?.accountId + Constants.EPG_CLOUD_URL_SUFFIX)
            if (response != null) {

                val simpleDateFormatter =
                    SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
                response.let {
                    if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                        logD("In Get EPG Data callback Success")
                        logD("Valid EPG Data Found")
                        processEPGData(response)
                        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                    } else {
                        logE("Invalid EPG Data found")
                        if (!isEPGServerApiCalled) {
                            fetchEPGDataFromServer(UA)
                            isEPGServerApiCalled = true
                        } else {
                            isEPGServerApiCalled = false
                        }
                    }
                }

            } else {
                logE("In Get EPG Data callback Fail")
            }
        }
    }

    private fun fetchEPGDataFromServer(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getEPGDataFromServer(ua)
            if (response != null) {
                processEPGData(response)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Get EPG Data Server callback Success")
            } else {
                logE("In Get EPG Data Server callback Fail")
            }
        }
    }

    private fun sendSysInfo(ua: String, body: SysInfoDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.sendSysInfo(ua, body)
            if (response != null && response == 0) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In Channel List callback Success")
            } else {
                logE("In Channel List callback Fail")
            }
        }
    }

    private suspend fun handleSysInfoCmd() {
        val accountSetupData = accountSetupLiveData.value
        val dateFormatter = SimpleDateFormat("EEE. MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH)
        val body = SysInfoDTO()
        body.HOTELCODE = accountId.toInteger()
        body.ROOM = stbRoomNo.uppercase()
        body.STBTIME = dateFormatter.format(Date())
        body.LASTCALLBACK = dateFormatter.format(Date())
        body.HOTELMODEL = accountSetupData?.hotelModel?.toInteger()
        body.HOTELPLAN = accountSetupData?.hotelPlan
        body.tvBroadcastType = accountSetupData?.tvBroadcastType
        body.streamingType = accountSetupData?.streamingType
        body.UA = ua
        body.SRNO = serialNo
        body.stbIp = getIpAddress()
        body.netMask = getNetMask()
        body.route = getGateway()
        body.connectivityType = networkUtils.getConnectivityType()
        body.VOD_MANAGER_IP = accountSetupData?.vodMgrIp
        body.VOD_MANAGER_PORT = accountSetupData?.vodMgrPort
        body.streamingIp = accountSetupData?.streamingIp
        body.streamingPort = accountSetupData?.streamingPort
        body.fetchIp = accountSetupData?.fetchServerIp
        body.FETCH_PORT = accountSetupData?.fetchServerPort
        body.mgIp = accountSetupData?.softwareDownloadIp
        body.mgPort = accountSetupData?.softwareDownloadPort
        body.swVersion = BuildConfig.VERSION_NAME
        body.SYNCLISTVERSON = moviesVersion
        body.hsVersion = hotelServicesVersion
        body.laVersion = laVersion
        body.THVERSION = themeVersion
        body.epgStart = epgStartTime
        body.epgEnd = epgEndTime
        body.channelCount = channelCount.toString()
        body.inRmVersion = Constants.INRMVER
        body.lauVersion = Constants.LAUVER
        body.shoVersion = showtimeVersion
        body.blankChannelLcn = accountSetupData?.blankChannelLcn
        body.appsCount = accountSetupData?.selectedAppsList?.filter { it.forAndroid }?.size
        body.zoneId = accountSetupData?.stbZoneId.toString()
        body.enableShowtime = accountSetupData?.enableShowtime.toString()
        body.ENABLE_NDVR = accountSetupData?.enableNdvr.toString()
        body.tvResolution = deviceUtils.getScreenResolution()
        body.appsList = accountSetupData?.selectedAppsList?.filter { it.forAndroid }?.joinToString {
            it.label
        }
        body.volumeLevel = getVolumeLevel().toString()
        body.startChannelLcn = "None"
        body.tlxFile = "None"
        body.STORAGE1 = "None"
        body.FREE_STORAGE1 = "None"
        body.dailyUpdateTime = "None"
        body.platformVersion = Build.VERSION.RELEASE
        body.stbModel = Build.MODEL
        body.bootVersion = Build.ID

        sendSysInfo(ua, body)

    }

    private fun getVolumeLevel(): Int {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        updateGuestSession(
            preferenceDataStoreHelper, guestDetailsDatastore, true, kapingResponse.cmdData?.cmdData
        )
        GuestDetails.IS_GUEST_CHECKED_IN = true
        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        CoroutineScope(Dispatchers.Default).launch {
            appList = ArrayList(getAppList())
            updateGuestMessage(messageDatastore, MessageResponse())
            updateGuestSession(
                preferenceDataStoreHelper,
                guestDetailsDatastore,
                false,
                kapingResponse.cmdData?.cmdData
            )
            clearCredentials(appList)
            GuestDetails.IS_GUEST_CHECKED_IN = false
            kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        }
    }

    private fun handleRebootCmd() {
        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_STB -> {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                Log.e("TAG", "handleRebootCmd: BUILD_TYPE_STB Start", )
                hardwareAPI.myService?.rebootDevice()
                Log.e("TAG", "handleRebootCmd: BUILD_TYPE_STB End", )
                Log.e("TAG", "handleRebootCmd: BUILD_TYPE_STB End1", )
            }
            else -> {
                val intent = Intent()
                intent.component =
                    ComponentName(MDM_PACKAGE_NAME, KapingConstants.MDM_RESTART_ACTIVITY_NAME)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun handleTickerMsgCmd(ua: String) {
        fetchTickerMessage(ua)
    }

    private fun handleGuestMsgCmd() {
        getGuestMessages(preferenceDataStoreHelper)
    }

    private fun getGuestMessages(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        CoroutineScope(Dispatchers.IO).launch {
            if (preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY,
                    false
                )
            ) {
                getGuestDetails(guestDetailsDatastore)
            }
        }
    }

    private fun getGuestDetails(dataStore: DataStore<CmdDataDto>) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect {
                it.sessionId?.let {
                    fetchGuestMessage(ua, it)
                }
            }
        }
    }

    private fun updateGuestSession(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        guestDetailsDatastore: DataStore<CmdDataDto>,
        isCheckedIn: Boolean,
        guestDetails: CmdDataDto?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY, isCheckedIn
            )
            guestDetails?.sessionId?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SESSION_ID_KEY, it
                )
            }
            updateGuestDetails(guestDetailsDatastore, guestDetails)
        }
    }

    private fun updateGuestDetails(
        dataStore: DataStore<CmdDataDto>, data: CmdDataDto?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
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


    private fun updateAccountSetupData(
        dataStore: DataStore<AccountSetupResponse>, data: AccountSetupResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
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
                    welcomeScreenVisible = data.welcomeScreenVisible,
                    isEnablePatchWall = data.isEnablePatchWall
                )
            }
        }
    }

    private fun updateThemeData(data: ThemeResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            /*dataStore.updateData { currentPreferences ->
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
            }*/
            updateDataStore.updateThemeData(data)
        }
    }

    private fun updateHotelServices(data: HotelServiceResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDataStore.updateHSData(data)
        }
    }

    private fun updateLocalAttractions(data: LocalAttractionResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDataStore.updateLAData(data)
        }
    }

    private fun setMoviesResponseData(
        dataStore: DataStore<MoviesResponse>, data: MoviesResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDatastoreParams(
                moviesCount = data.freeContentList.size.plus(data.premiumContentList.size),
                cListVersion = data.version
            )
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    adultDayPassPrice = data.adultDayPassPrice,
                    id = data.id,
                    type = data.type,
                    version = data.version,
                    freeContentList = data.freeContentList,
                    freeGenreList = data.freeGenreList,
                    premiumContentList = data.premiumContentList,
                    premiumGenreList = data.premiumGenreList
                )
            }
        }
    }

    private fun updateShowTimeData(
        dataStore: DataStore<ShowTimeResponse>, data: ShowTimeResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDatastoreParams(showsCount = data.shoContentList.size)
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

    private fun updateChannelList(
        dataStore: DataStore<ChannelListResponse>, data: ChannelListResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id, channelLcnList = data.channelLcnList, type = data.type
                )
            }
        }
    }

    private fun updateTickerMessage(
        dataStore: DataStore<TickerResponse>,
        data: TickerResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    tvTickerList = data.tvTickerList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    private fun updateGuestMessage(
        dataStore: DataStore<MessageResponse>,
        data: MessageResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    messagesList = data.messagesList,
                    type = data.type,
                )
            }
        }
    }

    private fun processEPGData(epgResponse: EPGResponse) {

        //Removing all Epg Channels From RoomDB.
        CoroutineScope(Dispatchers.IO).launch {
            roomRepository.removeAllChannels()

            val simpleDateFormatter = SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
            epgResponse.let {
                if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                    logD("EPG Start Time: ${it.ST} & EPG End Time: ${it.ET}")
                    it.ST?.let { st -> epgStartTime = st }
                    it.ET?.let { et -> epgEndTime = et }
                    updateDatastoreParams(epgStartTime = it.ST, epgEndTime = it.ET)
                    val channelList = channelListLiveData.value?.channelLcnList
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
                                        if (channelApi.httpStreaming == true) channel.VP =
                                            channelApi.httpStreamingUrl
                                        else channel.VP = channelApi.VP
                                        channel.param1 = channelApi.param1
                                        channel.param2 = channelApi.param2
                                        channel.httpStreamingUrl = channelApi.httpStreamingUrl
                                        channel.httpStreaming = channelApi.httpStreaming
                                        channel.recordable = channelApi.recordable

                                        channel.channelNameNo =
                                            "${channelApi.CNO}   ${channelApi.CN}"
                                        channel.lastProg = channel.C
                                        channel.prog1Time = "${channel.P1_ST} - ${channel.P1_ET}"

                                        //Mapping EpgMap with Program Map Api
                                        if (channel.P1_ID != null) {
                                            val program1 = it.programsListMap?.get(channel.P1_ID)
                                            if (program1 != null) {
                                                channel.P1_PT = program1.PT
                                                channel.P1_SY = program1.SY
                                                channel.progInfo = program1.PT
                                                channel.progSynopsis = program1.SY
                                                channel.liveProg1 = program1.PT
                                                channel.progInfo1 =
                                                    "${channel.CNO} - ${program1.PT}"
                                            } else {
                                                channel.P1_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P1_SY = Constants.NO_INFORMATION_AVAILABLE
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
                                            nextProgramTime.time = channel.P1_DET?.let { it1 ->
                                                simpleDateFormatter.parse(
                                                    it1
                                                )
                                            }!!
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
                                if (!isFound) iterator.remove()
                                else {
                                    //Removing Duplicate Channels
                                    if (ciMap[channel.CI] != null) iterator.remove()
                                    else ciMap[channel.CI] = true
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

    fun updateStbAllocationStatus(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        isStbAllocated: Boolean,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.IS_STB_ALLOCATED, isStbAllocated
            )
        }

    }

    private fun getVersionNumber(): String {
        return BuildConfig.VERSION_NAME.replace(".", "").trim()
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
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
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
                    this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this, notificationChannelId
            ) else Notification.Builder(this)

        return builder.setContentTitle("Endless Service")
            .setContentText("This is your favorite endless service working")
            .setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    private fun isScreenOn(): String {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return if (pm.isInteractive) KapingConstants.POWER_MODE_ON else KapingConstants.POWER_MODE_STAND_BY
    }

    /*    private fun startUpdateDataWorker(action: String) {
            val inputData = Data.Builder()
                .putString(UpdateDataWorker.ACTION, action)
                .build()

            val request = OneTimeWorkRequestBuilder<UpdateDataWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
        }*/

    private fun updateDatastoreParams(
        moviesCount: Int? = null,
        showsCount: Int? = null,
        cListVersion: String? = null,
        channelCount: Int? = null,
        epgStartTime: String? = null,
        epgEndTime: String? = null
    ) {
        CoroutineScope(Dispatchers.Default).launch {
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
        CoroutineScope(Dispatchers.Default).launch {
            accountId = getAccountId()
            serialNo = getSerialNo()
            ua = getUa()
            stbRoomNo = getStbRoomNo()
            epgStartTime = getEpgSt()
            epgEndTime = getEpgEt()
            channelCount = getChannelCount()
            appList = ArrayList(getAppList())
        }
    }

    private suspend fun getAccountId(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
            ""
        )
    }

    private suspend fun getStbRoomNo(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
            ""
        )
    }

    private suspend fun getSerialNo(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.SERIAL_NO,
            ""
        )
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    private suspend fun getIpAddress(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.IP_ADDRESS_KEY,
            "0.0.0.0"
        )
    }

    private suspend fun getNetMask(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.IP_NET_MASK_KEY,
            "0.0.0.0"
        )
    }

    private suspend fun getGateway(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.IP_GATEWAY_KEY,
            "0.0.0.0"
        )
    }

    private suspend fun getEpgSt(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.EPG_START_TIME_KEY,
            ""
        )
    }

    private suspend fun getEpgEt(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.EPG_END_TIME_KEY,
            ""
        )
    }

    private suspend fun getChannelCount(): Int {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.CHANNEL_COUNT_KEY,
            0
        )
    }

    private suspend fun getAppList(): Set<String> {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.APP_LIST_KEY,
            emptySet()
        )
    }

}
