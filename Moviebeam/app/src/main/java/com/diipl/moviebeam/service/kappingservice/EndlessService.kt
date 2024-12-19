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
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.concierge.ConciergeResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassSync
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalSyncResponse
import com.diipl.moviebeam.data.dto.sysInfo.SysInfoDTO
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.kaping.CmdDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_CONTENT_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_FINISH_TIME
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.NETWORK_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.service.handler.callNetflixAPI
import com.diipl.moviebeam.service.remote.BTService
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.dialogs.AdultContentDialog
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.localattraction.LocalAttractionActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.movies.MovieDetailFragment
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.newprogramguide.NewProgramGuideActivity
import com.diipl.moviebeam.ui.player.ExoPlayerActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.programguide.PrgGuidePlayerActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiActivity
import com.diipl.moviebeam.ui.register_stb.RegisterSTBActivity
import com.diipl.moviebeam.ui.serial_info.SerialActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.GLOBAL_LOOP_SEC
import com.diipl.moviebeam.utils.Constants.MDM_PACKAGE_NAME
import com.diipl.moviebeam.utils.Constants.OPEN_VPN_CONNECT_CMD
import com.diipl.moviebeam.utils.Constants.OPEN_VPN_DISCONNECT_CMD
import com.diipl.moviebeam.utils.Constants.OPEN_VPN_PACKAGE_NAME
import com.diipl.moviebeam.utils.Constants.OPEN_VPN_PROFILE
import com.diipl.moviebeam.utils.Constants.OPEN_VPN_PROFILE_NAME
import com.diipl.moviebeam.utils.DeviceUtils
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.cleanString
import com.diipl.moviebeam.utils.compareVersions
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.getAppVersion
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.isAppInstalled
import com.diipl.moviebeam.utils.isNotAllowed
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.logK
import com.diipl.moviebeam.utils.rebootDevice
import com.diipl.moviebeam.utils.setIPInfo
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.startActivity
import com.diipl.moviebeam.utils.toInteger
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toTimestamp
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "EndlessService"
@AndroidEntryPoint
class EndlessService : Service() {


    private var wakeLock: PowerManager.WakeLock? = null
    private var counter = 0
    private val myApiService = createRetrofitService()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private val versionNumber by lazy { BuildConfig.VERSION_NAME.replace(".", "").trim() }
    private var transactionId = ""
    private var isNetworkAvailable = true

    private val dispatcher = Dispatchers.IO
    private val coroutineScope = CoroutineScope(dispatcher)

    private val _accountSetupLiveData = MutableLiveData<AccountSetupResponse>()
    val accountSetupLiveData: LiveData<AccountSetupResponse> get() = _accountSetupLiveData

    private val _moviesLiveData = MutableLiveData<MoviesResponse>()
    private val moviesLiveData: LiveData<MoviesResponse> get() = _moviesLiveData

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var deviceUtils: DeviceUtils

    @Inject
    lateinit var movieBeamRepository: MovieBeamRepository

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var tickerDatastore: DataStore<TickerResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var sharedPreference: SharedPreference

    private lateinit var btService: BTService

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    private var btnModelList = emptyList<BtnModel>()

    companion object {
        const val FEATURE_NOT_SUPPORTED = "feature not supported"
        var HOME_KEY_PRESSED = false
        var isServiceStarted = false
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
        logD(versionNumber)

        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        filter.addAction(Intent.CATEGORY_HOME)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(homePressReceiver, filter)

        // TODO
        var isSwitched = false
        var count = 0
        coroutineScope.launch {
            try {
                while (activityStack.last()?.isNotEmpty() == true) {
                    if (activityStack.last() != RegisterSTBActivity::class.java.simpleName)
                        if (activityStack.last() != STBDetailsActivity::class.java.simpleName) {
                            preferenceDataStoreHelper.putPreference(
                                NETWORK_STATUS,
                                isNetworkAvailable
                            )
                            if (!isNetworkAvailable && !isSwitched) {
                                isSwitched = true
                                MainMenuActivity::class.java.startActivity()
                                count = 0
                            }
                            if (isNetworkAvailable && isSwitched) {
                                isSwitched = false
                                if (count == 0) {
                                    MainMenuActivity::class.java.startActivity()
                                    count++
                                }
                            }
                        }
                    delay(1000 * 2)
                }
            } catch (e: Exception) {
                Log.e("TAG", "activityStack: ${e.localizedMessage}")
            }
        }

        val notification = createNotification()
        startForeground(1, notification)

    }

    private val homePressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return

            if (activityStack.last()?.isNotAllowed() == true) {
                when (action) {
                    Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                        val reason = intent.getStringExtra("reason")
                        if (reason == "homekey") {
                            handleHomeKeyPress()
                        }
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            delay(10000)
                            MainMenuActivity::class.java.startActivity()
                        }
                    }
                }
            } else return
        }
    }

    private fun handleHomeKeyPress() {
        when (activityStack.last()) {
            AppWorldActivity::class.java.simpleName -> {
                if (AppWorldActivity.NETFLIX_LAUNCHED) {
                    callNetflixAPI(preferenceHandler.roomNo, preferenceHandler.UA, 2)
                    AppWorldActivity.NETFLIX_LAUNCHED = false
                } else {
                    switchToHDMI()
                }
            }
            NewProgramGuideActivity::class.java.simpleName, DisconnectedPrgActivity::class.java.simpleName -> {
                if(activityStack.last() == NewProgramGuideActivity::class.java.simpleName){
                    (BaseActivity.currentActivity as NewProgramGuideActivity).switchToHDMI()
                } else  if(activityStack.last() == DisconnectedPrgActivity::class.java.simpleName){
                    (BaseActivity.currentActivity as DisconnectedPrgActivity).switchToHDMI()
                }
                switchToHDMI()
            }
            else -> {
                switchToHDMI()
            }
        }
    }

    private fun switchToHDMI() {
//        HOME_KEY_PRESSED = true
        MainMenuActivity::class.java.startActivity()
       /* if (::btService.isInitialized && btService.isConnected()) {
            btService.transmit(preferenceHandler.remoteModel.sourceInput)
        }
        return*/
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

                    _accountSetupLiveData.postValue(accountSetupDataStore.data.first())
                    _moviesLiveData.postValue(moviesDataStore.data.first())

                    if (preferenceHandler.UA.isNotBlank()) {
                        pingFakeServer()
                        callKapingApi()

                        if (preferenceHandler.isGuestCheckedIn)
                            roomRepository.removeOverTimeMovies()
                        else {
                            roomRepository.deleteRecentMovies()
                            roomRepository.deleteRecentShows()
                            removeAdultData()
                        }
                    }
                }
//                btService = BTService(applicationContext).also { it.findBondedDevice() }
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

        coroutineScope.launch {
            preferenceDataStoreHelper.getFirstPreference(ADULT_DAY_PASS_FINISH_TIME, 0).let {
                if (it <= System.currentTimeMillis()) {
                    preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, false)
                }
            }
        }

    }

    private fun callKapingApi() {
        // Calculate epoch time as a 9-character hex string
        val epochTime = (Date().time / 1000).toString(16).padStart(9, '0')

        // Build CMDRES
        val cmdRes = if (kapingCMD == "00") {
            ""
        } else {
            "$kapingCMD$epochTime$transactionId$kapingCmdExecutionResponse"
        }

        // Build EVENT string
        val event = buildString {
            append(isScreenOn())
            append(getCurrentPanelNumber())
            append(
                if (preferenceHandler.isGuestCheckedIn)
                    KapingConstants.EVENT_CHECK_IN
                else
                    KapingConstants.EVENT_CHECK_OUT
            )
        }

        // Prepare API call
        val kapingCall = myApiService.getKapingService(
            Constants.KAPING,
            preferenceHandler.UA,
            "0",
            versionNumber,
            preferenceHandler.moviesVersion,
            Constants.DV,
            "1",
            event,
            "0",
            Constants.RBTY,
            Constants.MODE,
            preferenceHandler.laVersion,
            preferenceHandler.hsVersion,
            preferenceHandler.themeVersion,
            cmdRes,
            "1",
            Constants.INRMVER,
            Constants.LAUVER
        )

        // Make the API call
        kapingCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    handleSuccessfulResponse(response.body())
                } else {
                    logE("Kaping API call failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                isNetworkAvailable = false
                logE("Kaping API call failed: ${t.message}")
            }
        })
    }

    // Handle successful API response
    private fun handleSuccessfulResponse(data: String?) {
        isNetworkAvailable = true
        val result = ApiResponseParsing().getResponseAsObject(data, KapingResponse::class)?.apply {
            CMD?.let {
                cmdData = parseCmd(it)
                logCmdSignal(it)
            }
        }

        kapingCmdExecutionResponse = KapingConstants.PENDING_EXECUTION

        result?.let {
            if (it.AS.equals("CLEAR")) {
                handleClearCommand(it)
            } else if (it.AS.isNullOrEmpty()) {
                preferenceHandler.updateDatastoreVariables(isStbAllocated = true)
                handleKaping(result)
            }
        }
    }

    // Handle CLEAR command logic
    private fun handleClearCommand(result: KapingResponse) {
        preferenceHandler.updateDatastoreVariables(isStbAllocated = false, isAllDataFetched = false)
        handleCheckOutCmd(result)

        if (activityStack.last() != RegisterSTBActivity::class.java.simpleName) {
            BaseActivity.currentActivity?.let { currentActivity ->
                Intent(currentActivity, RegisterSTBActivity::class.java).apply {
                    putExtra("btnId", Constants.REBOOT_BTN)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    currentActivity.startActivity(this)
                    currentActivity.finish()
                }
            }
        }
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
        //retrieve data for VPN profile
        if (kapingCMD == KapingConstants.KAP_CMD_CHECK_IN || kapingCMD == KapingConstants.KAP_CMD_CHECK_OUT) {
            val cmdData = cmd.substring(19, cmd.length)
            val sessionId = cmdData.substring(5, 15)
            var adultContentDisabled: Boolean? = null
            val message = cmdData.substringAfter(" ").trim()
            var passCode: String? = null
            var parentSessionId: String? = null
            var guestName: String? = null
            var adultLocked: Boolean? = null
            if (kapingCMD == KapingConstants.KAP_CMD_CHECK_IN) {
                adultContentDisabled = cmdData[0] == '0'
                parentSessionId = cmdData.substring(15, 25)
                val nameAndPass = cmdData.substring(cmdData.indexOf("Welcome"))
                guestName = nameAndPass.substring(8, (nameAndPass.length - 4)).cleanString()
                try {
                    passCode = nameAndPass.substring(nameAndPass.length - 4)
                    if (passCode == "____") {
                        passCode = null
                        adultLocked = false
                    } else {
                        passCode.toInteger()
                        adultLocked = true
                    }
                } catch (e: Exception) {
                    logE("parseCmd: ${e.message}")
                    passCode = null
                    adultLocked = false
                }
            }
            cmdDataDto = CmdDataDto(
                sessionId,
                parentSessionId,
                adultContentDisabled,
                message,
                guestName,
                adultLocked,
                passCode
            )
            updateAdultContent(cmd)
            if (passCode != null) {
                updateParentalPassCode(passCode)
            } else {
                updateParentalPassCode("____")
            }
        }
        return CmdDto(kapingCMD, epochTime, transactionId, cmdDataDto)
    }

    private fun updateParentalPassCode(passCode: String) {
        sharedPreference.adultPassCode = passCode
    }

    private fun handleKaping(kapingResponse: KapingResponse?) = coroutineScope.launch {
        btnModelList = accountSetupLiveData.value?.let {
            Constants.HOME_PAGE_MENU_BUTTON_LIST.filter { model ->
                it.buttonsList.any { it.buttonName == model.btnId }
            }
        }!!

        when (kapingResponse?.cmdData?.cmd) {
            KapingConstants.KAP_CMD_SOFTWARE_UPDATE -> handleSoftwareUpdate()

            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE,
            KapingConstants.KAP_CMD_CHECK_IN,
            KapingConstants.KAP_CMD_CHECK_OUT,
            KapingConstants.KAP_CMD_THEME_CHANGE,
            -> handleAccountUpdate(kapingResponse)

            KapingConstants.KAP_CMD_HS_CHANGE -> handleHSUpdate(kapingResponse)

            KapingConstants.KAP_CMD_LA_CHANGE -> handleLAUpdate(kapingResponse)

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> handleFetchSyncList(kapingResponse)

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> handleShowtimeCmd(kapingResponse)

            KapingConstants.KAP_CMD_GET_CHANNEL_LIST -> handleChannelCmd(kapingResponse)

            KapingConstants.KAP_CMD_GET_EPG_DATA -> handleEPGCmd(kapingResponse)

            KapingConstants.KAP_CMD_SYNC_RECENT_VIEWED -> handleSyncRecentCmd(kapingResponse)

            KapingConstants.KAP_CMD_SYNC_ADULT_DAYPASS -> handleSyncAdultDayPassCmd(kapingResponse)

            KapingConstants.KAP_CMD_ENABLE_DISABLE_ADULT_CONTENT -> handleAdultContentCmd(kapingResponse)

            KapingConstants.KAP_CMD_SEND_SYS_INFO -> handleSysInfoCmd()

            KapingConstants.KAP_CMD_REBOOT -> handleRebootCmd()

            KapingConstants.KAP_CMD_GET_TICKER_MESSAGES -> handleTickerMsgCmd()

            KapingConstants.KAP_CMD_GET_GUEST_MESSAGES -> handleGuestMessagesCmd(kapingResponse)

            KapingConstants.KAP_CMD_CONNECT_VPN -> {
                logD("Signal for VPN Connect command")
                handleVPNCommand(kapingResponse, OPEN_VPN_CONNECT_CMD)
            }

            KapingConstants.KAP_CMD_DISCONNECT_VPN -> {
                logD("Signal for VPN Disconnect command")
                handleVPNCommand(kapingResponse, OPEN_VPN_DISCONNECT_CMD)
            }
        }
    }

    private fun handleGuestMessagesCmd(kapingResponse: KapingResponse) {
        when (activityStack.last()) {
            SerialActivity::class.java.simpleName, STBDetailsActivity::class.java.simpleName, RegisterSTBActivity::class.java.simpleName -> fetchGuestMessage()
            else -> handleCmdInRefreshingUi(kapingResponse)
        }
    }

    private fun handleAdultContentCmd(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.VOD_ID }) {
            updateAdultContent(kapingResponse.CMD)
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("AdultContent Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleSyncAdultDayPassCmd(kapingResponse: KapingResponse) = coroutineScope.launch {
        if (btnModelList.any { it.btnId == Constants.VOD_ID }) {
            kapingResponse.CMD?.let { str ->
                val data = str.substring(19, str.length)
                val syncResponse = data.fromJson<AdultDayPassSync>()
                syncResponse.dayPassList.forEach {
                    it.let {
                        preferenceDataStoreHelper.putPreference(
                            ADULT_DAY_PASS_STATUS,
                            true
                        )
                        preferenceDataStoreHelper.putPreference(
                            ADULT_DAY_PASS_FINISH_TIME,
                            it.dayPassRentalTime.toTimestamp().also { time ->
                                time.plus(24 * 60 * 60 * 1000)
                            })
                    }
                }
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("SyncAdultDayPass Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleSyncRecentCmd(kapingResponse: KapingResponse) = coroutineScope.launch {
        if (btnModelList.any { it.btnId == Constants.VOD_ID }) {
            kapingResponse.CMD?.let { str ->
                val data = str.substring(19, str.length)
                val syncResponse = data.fromJson<RentalSyncResponse>()
                moviesLiveData.value?.let { res ->
                    syncResponse.syncList.forEach { sync ->
                        res.premiumContentList.forEach {
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
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("SyncRecent List Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleEPGCmd(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.PRG_GUIDE_ID } && BuildConfig.BUILD_TYPE_ID != 6) {
            when (activityStack.last()) {
                ProgramGuideActivity::class.java.simpleName, PrgGuidePlayerActivity::class.java.simpleName -> {
                    handleCmdInRefreshingUi(kapingResponse)
                }

                else -> {
                    fetchEPGData()
                }
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("EPG Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleChannelCmd(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.PRG_GUIDE_ID }) {
            if (accountSetupLiveData.value?.isPlayBoxTv == true) {
                logD("PlayBox TV is enabled")
                kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
                logE("Channel List Cmd $FEATURE_NOT_SUPPORTED")
                return
            }
            when (activityStack.last()) {
                NewProgramGuideActivity::class.java.simpleName, ProgramGuideActivity::class.java.simpleName, PrgGuidePlayerActivity::class.java.simpleName -> handleCmdInRefreshingUi(kapingResponse)
                else -> fetchChannelList()
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("Channel List Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleShowtimeCmd(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.SHOWTIMES_ID }) {
            when (activityStack.last()) {
                ShowtimeActivity::class.java.simpleName, ExoPlayerActivity::class.java.simpleName -> handleCmdInRefreshingUi(kapingResponse)
                else -> fetchShowtimeData()
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("Showtime Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleFetchSyncList(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.VOD_ID }) {
            when (activityStack.last()) {
                MoviesActivity::class.java.simpleName, MovieDetailFragment::class.java.simpleName, AdultContentDialog::class.java.simpleName, ExoPlayerActivity::class.java.simpleName ->
                    handleCmdInRefreshingUi(kapingResponse)
                else -> fetchSyncList()

            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("FetchSyncList Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleLAUpdate(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.LOCAL_ATTRACTION_ID }) {
            when (activityStack.last()) {
                LocalAttractionActivity::class.java.simpleName, GuestServiceActivity::class.java.simpleName -> handleCmdInRefreshingUi(kapingResponse)
                else -> fetchLocalAttractionInfo()
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("LA Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleHSUpdate(kapingResponse: KapingResponse) {
        if (btnModelList.any { it.btnId == Constants.HOTEL_SERVICES_ID }) {
            when (activityStack.last()) {
                HotelInfoActivity::class.java.simpleName -> handleCmdInRefreshingUi(kapingResponse)
                else -> fetchHotelServiceInfo()
            }
        } else {
            kapingCmdExecutionResponse = KapingConstants.FEATURE_NOT_SUPPORTED
            logE("HS Cmd $FEATURE_NOT_SUPPORTED")
        }
    }

    private fun handleAccountUpdate(kapingResponse: KapingResponse) {
        removeAdultData()
        when (activityStack.last()) {
            SerialActivity::class.java.simpleName, STBDetailsActivity::class.java.simpleName, RegisterSTBActivity::class.java.simpleName -> handleCmdInBackground(kapingResponse)
            else -> handleCmdInRefreshingUi(kapingResponse)
        }
    }

    private fun handleSoftwareUpdate() = coroutineScope.launch {
        val response = withContext(Dispatchers.IO) {
            movieBeamRepository.getSoftwareUpdateDetails()
        }

        response?.let {
            if (it.fileName.isNotEmptyOrNull() && compareVersions(it.softwareVersion)) {
                logD("softwareData -> currentVersion: ${BuildConfig.VERSION_NAME}  response: $it")

                Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(
                        MDM_PACKAGE_NAME,
                        KapingConstants.MDM_SOFTWARE_ACTIVITY
                    )
                    putExtra("softwareData", it.toJson())
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(this)
                }
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun handleVPNCommand(response: KapingResponse, command: String) {
        val profileID = response.CMD.toString().substring(19)

        if (!isAppInstalled(OPEN_VPN_PACKAGE_NAME)) {
            showToast("Open VPN app not available")
            return
        }

        if (profileID.isEmpty()) {
            showToast("Invalid VPN ID")
            return
        }

        Intent(Intent.ACTION_MAIN).apply {
            setClassName(OPEN_VPN_PACKAGE_NAME, command)
            putExtra(OPEN_VPN_PROFILE_NAME, "$OPEN_VPN_PROFILE$profileID")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }

    }

    private fun handleCmdInRefreshingUi(kapingResponse: KapingResponse) {
        when (kapingResponse.cmdData?.cmd) {
            KapingConstants.KAP_CMD_CHECK_IN -> {
                resetPopUps(true)
                updateAdultContent(kapingResponse.CMD)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> resetPopUps(false)
        }
        Intent(applicationContext, RefreshingUiActivity::class.java).apply {
            putExtra("response", kapingResponse.toJson())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun handleCmdInBackground(kapingResponse: KapingResponse) {
        when (kapingResponse.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> fetchAccountSetupDetails()

            KapingConstants.KAP_CMD_THEME_CHANGE -> fetchThemeDetails()

            KapingConstants.KAP_CMD_CHECK_IN -> {
                resetPopUps(true)
                updateAdultContent(kapingResponse.CMD)
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

    private fun fetchAccountSetupDetails() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchAccountData(true)
            if (isComplete)
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        }
    }

    private fun fetchThemeDetails() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchThemeDetails()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Theme current version: ${preferenceHandler.themeVersion}")
            }
        }
    }

    private fun fetchGuestMessage() = coroutineScope.launch {
        if (preferenceHandler.isGuestCheckedIn) movieBeamRepository.fetchGuestMessage()
    }

    private fun fetchHotelServiceInfo() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchHotelService()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Hotel Services current version: ${preferenceHandler.hsVersion}")
            }
        }
    }

    private fun fetchLocalAttractionInfo() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchLocalAttractions()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Local Attraction current version: ${preferenceHandler.laVersion}")
            }
        }
    }

    private fun fetchSyncList() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchMoviesMore()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Releases current version: ${preferenceHandler.moviesVersion}")
            }
        }
    }

    private fun fetchShowtimeData() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchShowtime()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Showtime current version: ${preferenceHandler.showTimeVersion}")
            }
        }
    }

    private fun fetchChannelList() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchChannelList()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }
        }
    }

    private fun fetchEPGData() {
        CoroutineScope(Dispatchers.Main).launch {
            movieBeamRepository.fetchEpgData()
            movieBeamRepository.epgStatus.observeForever {
                when (it) {
                    EPGHandler.STATUS_OK -> {
                        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                        this.cancel()
                    }
                }
            }
        }
    }

    private fun sendConciergeInfo(serviceId: String, body: ConciergeResponse) {
        coroutineScope.launch {
            val response = movieBeamRepository.sendConciergeInfo(serviceId, body)
            if (response != null) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("In ConciergeInfo callback Success")
            } else {
                logE("In ConciergeInfo callback Fail")
            }
        }
    }

    private fun handleSysInfoCmd() = coroutineScope.launch {
        val accountSetupData = accountSetupLiveData.value
        val dateFormatter = SimpleDateFormat("EEE. MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH)
        val body = SysInfoDTO()
        body.HOTELCODE = preferenceHandler.accountID.toInteger()
        body.ROOM = preferenceHandler.roomNo.uppercase()
        body.STBTIME = dateFormatter.format(Date())
        body.LASTCALLBACK = dateFormatter.format(Date())
        body.HOTELMODEL = accountSetupData?.hotelModel?.toInteger()
        body.HOTELPLAN = accountSetupData?.hotelPlan
        body.tvBroadcastType = accountSetupData?.tvBroadcastType
        body.streamingType = accountSetupData?.streamingType
        body.UA = preferenceHandler.UA
        body.SRNO = preferenceHandler.serialNo
        body.stbIp = preferenceHandler.ipAddress
        body.netMask = preferenceHandler.netMask
        body.route = preferenceHandler.gatewayIP
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
        body.SYNCLISTVERSON = preferenceHandler.moviesVersion
        body.hsVersion = preferenceHandler.hsVersion
        body.laVersion = preferenceHandler.laVersion
        body.THVERSION = preferenceHandler.themeVersion
        body.epgStart = preferenceHandler.epgStartTime
        body.epgEnd = preferenceHandler.epgEndTime
        body.channelCount = preferenceHandler.channelCount.toString()
        body.inRmVersion = Constants.INRMVER
        body.lauVersion = Constants.LAUVER
        body.shoVersion = preferenceHandler.showTimeVersion
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
        body.mdmVersion = getAppVersion(MDM_PACKAGE_NAME)
        body.playBoxTvVersion = getAppVersion(Constants.PBTV_PACKAGE_NAME)

        val response = movieBeamRepository.sendSysInfo(body)
        if (response != null && response == 0) {
            kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            logD("In System Info callback Success")
        } else {
            logE("In System Info callback Fail")
        }

    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        movieBeamRepository.updateGuestSession(true, kapingResponse.cmdData?.cmdData)
        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        movieBeamRepository.updateGuestSession(false, kapingResponse.cmdData?.cmdData)
        kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
    }

    private fun handleRebootCmd() {
        BaseActivity.currentActivity?.let {
            it.rebootDevice()
            kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        }
    }

    private fun handleTickerMsgCmd() {
        coroutineScope.launch {
            val isComplete = movieBeamRepository.fetchTickerMessages()
            if (isComplete) {
                kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                logD("Ticker current version: ${tickerDatastore.data.first().version}")
            }
        }
    }

    private fun updateAdultContent(CMD: String?) = coroutineScope.launch {
        CMD?.let {
            if (it.length > 19) {
                val isEnabled = it[19] == '1'
                sharedPreference.isAdultContentEnabled = isEnabled
                preferenceDataStoreHelper.putPreference(ADULT_CONTENT_STATUS, isEnabled)
            }
        }
    }

    private fun removeAdultData() = coroutineScope.launch {
        preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, false)
    }

    private fun getVolumeLevel(): Int {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(AudioManager.STREAM_MUSIC)
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

}
