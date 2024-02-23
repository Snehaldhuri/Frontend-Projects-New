package com.diipl.moviebeam.ui.kappingservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassSync
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalSyncResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.kaping.CmdDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_CONTENT_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_FINISH_TIME
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.KapingResponseParsing
import com.diipl.moviebeam.utils.PanelConstants
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.log
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

private const val TAG = "EndlessService"

@AndroidEntryPoint
class EndlessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private var counter = 0
    private val myApiService = createRetrofitService()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var UA = ""
    private var versionNumber = ""
    private var themeVersion = ""
    private var laVersion = ""
    private var moviesVersion = ""
    private var hotelServicesVersion = ""
    private var CMDRES = ""
    private var EVENT = ""
    private var kapingCMD = ""
    private var epochTime = ""
    private var transactionId = ""
    private var kapingCmdExecutionResponse = "00"

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
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var roomRepository: RoomRepository


    companion object {
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

        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(homePressReceiver, filter)

        val notification = createNotification()
        startForeground(1, notification)
    }

    private val homePressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            intent.let {
                if (it.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                    val reason = it.getStringExtra("reason")
                    if (reason == "homekey") {
                        if (BaseActivity.currentActivity?.javaClass?.simpleName != MainMenuActivity::class.java.simpleName) {
                            startActivity(Intent(context, MainMenuActivity::class.java).also { i ->
                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            Log.e(TAG, "onReceive: 0")
                            return
                        } else {
                            Log.e(TAG, "onReceive: 1")
                            return
                        }
                    } else {
                        Log.e(TAG, "onReceive: 2")
                        return
                    }
                }
            }
        }
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

                    pingFakeServer()
                    callKapingApi()

                    if (Constants.SESSION_ID.isNotEmpty()) {
                        roomRepository.removeOverTimeMovies()
                    }
                    if (Constants.SESSION_ID == "null") {
                        roomRepository.deleteRecentMovies()
                        roomRepository.deleteRecentShows()
                        removeAdultData()
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
        CMDRES = if (kapingCMD == "00") {
            ""
        } else {
            "$kapingCMD$epochTime$transactionId$kapingCmdExecutionResponse"
        }
        log("CMDRES -> $CMDRES")

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
                    handleKaping(result)
                } else {
                    // Handle unsuccessful response
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
        if (kapingCMD == "07" || kapingCMD == "08") {
            val cmdData = cmd.substring(19, cmd.length)
            val sessionId = cmdData.substring(5, 15)
            var adultContentDisabled: Boolean? = null
            val message = cmdData.substringAfter(" ").trim()
            var passCode: String? = null
            var parentSessionId: String? = null
            var guestFirstName: String? = null
            var guestLastName: String? = null
            var adultLocked: Boolean? = null
            if (kapingCMD == "07") {
                adultContentDisabled = cmdData[0] == '0'
                parentSessionId = cmdData.substring(15, 25)
                val nameAndPass = cmdData.substring(cmdData.indexOf("Welcome"))
                guestFirstName = nameAndPass.split(" ")[1]
                val lastNameAndPass = nameAndPass.split(" ")[2]
//                guestLastName = lastNameAndPass.substring(0, lastNameAndPass.length-4)
//                passCode = lastNameAndPass.substring(lastNameAndPass.length-5)
//                if(passCode != "____"){
//                    passCode = null
//                    adultLocked = true
//                }else
//                    adultLocked = false

                guestLastName = lastNameAndPass
                adultLocked = false
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
            KapingConstants.KAP_CMD_THEME_CHANGE,
            KapingConstants.KAP_CMD_REBOOT -> {
                handleCmdInRefreshingUi(kapingResponse)
            }

            KapingConstants.KAP_CMD_HS_CHANGE -> {
                if (activityStack.size > 0) {
                    when (activityStack.last()) {
                        PanelConstants.HOTEL_SERVICE_ACTIVITY_LOCAL_NAME -> {
                            handleCmdInRefreshingUi(kapingResponse)
                        }

                        else -> {
                            fetchHotelServiceInfo(Constants.UA)
                        }
                    }

                } else {
                    fetchHotelServiceInfo(Constants.UA)
                }
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                if (activityStack.size > 0) {

                    when (activityStack.last()) {
                        PanelConstants.Local_ATTRACTION_ACTIVITY_LOCAL_NAME,
                        PanelConstants.GUEST_SERVICE_ACTIVITY_LOCAL_NAME -> {
                            handleCmdInRefreshingUi(kapingResponse)
                        }

                        else -> {
                            fetchLocalAttractionInfo(Constants.UA)
                        }
                    }
                } else {
                    fetchLocalAttractionInfo(Constants.UA)
                }
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                if (activityStack.size > 0) {

                    when (activityStack.last()) {
                        PanelConstants.MOVIES_ACTIVITY_LOCAL_NAME,
                        PanelConstants.EXO_PLAYER_ACTIVITY_LOCAL_NAME -> {
                            handleCmdInRefreshingUi(kapingResponse)
                        }

                        else -> {
                            fetchSyncList(Constants.UA)
                        }
                    }
                } else {
                    fetchSyncList(Constants.UA)
                }
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                if (activityStack.size > 0) {

                    when (activityStack.last()) {
                        PanelConstants.SHOWTIME_ACTIVITY_LOCAL_NAME,
                        PanelConstants.EXO_PLAYER_ACTIVITY_LOCAL_NAME -> {
                            handleCmdInRefreshingUi(kapingResponse)
                        }

                        else -> {
                            fetchShowtimeData(Constants.UA)
                        }
                    }
                } else {
                    fetchShowtimeData(Constants.UA)
                }
            }

            KapingConstants.KAP_CMD_SYNC_RECENT_VIEWED -> {
                CoroutineScope(Dispatchers.IO).launch {
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
                                        model.sessionID = Constants.SESSION_ID
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
                        Log.e(TAG, "handleKaping: $syncResponse")
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


        }

    }

    private fun updateAdultContent(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(ADULT_CONTENT_STATUS, enabled)
        }
    }

    private fun removeAdultData() {
        CoroutineScope(Dispatchers.IO).launch {
            updateAdultContent(false)
            preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, false)
        }
    }

    private fun fetchHotelServiceInfo(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getHotelServiceInfo(ua)
            if (response == null) {
                //Error
            } else {
                updateHotelServices(hotelServicesDataStore, response)
            }
        }
    }

    private fun fetchLocalAttractionInfo(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getLocalAttractionInfo(ua)
            if (response == null) {
                //Error
            } else {
                updateLocalAttractions(localAttractionsDataStore, response)
            }
        }
    }

    private fun fetchSyncList(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getMoviesInfo(ua)
            if (response == null) {
                //Error
            } else {
                setMoviesResponseData(moviesDataStore, response)
            }
        }
    }

    private fun fetchShowtimeData(ua: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = movieBeamRepository.getShowtimeInfo(ua)
            if (response == null) {
                //Error
            } else {
                updateShowTimeData(showtimeDataStore, response)
            }
        }
    }

    private fun updateHotelServices(
        dataStore: DataStore<HotelServiceResponse>,
        data: HotelServiceResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
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

        CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
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

    private fun handleCmdInRefreshingUi(kapingResponse: KapingResponse) {
        kapingResponse.CMD?.let {
            when (kapingResponse.cmdData?.cmd) {
                KapingConstants.KAP_CMD_CHECK_OUT -> removeAdultData()
                else -> {
                    if (it.length > 19) {
                        val isEnabled = it[19] == '1'
                        updateAdultContent(isEnabled)
                    }
                }
            }
        }

        val i = Intent(applicationContext, RefreshingUiActivity::class.java)
        i.putExtra("response", kapingResponse)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(i)
    }

    private fun getVersionNumber(): String {
        try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "0.0"
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

}
