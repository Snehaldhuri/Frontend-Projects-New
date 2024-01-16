package com.diipl.moviebeam.ui.kappingservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import androidx.databinding.ktx.BuildConfig
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.log
import com.google.gson.GsonBuilder
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.KapingResponseParsing
import com.diipl.moviebeam.utils.log
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
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
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

    private val _themeLiveData = MutableLiveData<ThemeResponse>()
    val themeLiveData: LiveData<ThemeResponse> get() = _themeLiveData

    private val _localAttractionLiveData = MutableLiveData<LocalAttractionResponse>()
    val localAttractionLiveData: LiveData<LocalAttractionResponse> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<MoviesResponse>()
    val moviesLiveData: LiveData<MoviesResponse> get() = _moviesLiveData

    private val _hotelServicesLiveData = MutableLiveData<HotelServiceResponse>()
    val hotelServicesLiveData: LiveData<HotelServiceResponse> get() = _hotelServicesLiveData

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var localAttractionsDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

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
        versionNumber = getVersionNumber().replace(".","").trim()
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
                    pingFakeServer()
                    fetchData()
                    UA = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.UA, "")

                   _themeLiveData.postValue(themeDataStore.data.first())
                    _localAttractionLiveData.postValue(localAttractionsDataStore.data.first())
                    _moviesLiveData.postValue(moviesDataStore.data.first())
                    _hotelServicesLiveData.postValue(hotelServicesDataStore.data.first())

                    pingFakeServer()
                    callKapingApi()
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

    fun fetchData() {

        val call: Call<String> = myApiService.getstbMasterService(Constants.UA, "", "", "", "")

        Constants.timer = "hsdsh"
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    val result =
                        ApiResponseParsing().getResponseAsObject(data, StbMasterResponse::class)
    private fun callKapingApi() {

        val themeVersion1 = themeLiveData.value?.version
        if (!themeVersion1.isNullOrEmpty()){
            themeVersion = themeVersion1
        }
        log(themeVersion1.toString())

        val laVersion1 = localAttractionLiveData.value?.version
        if (!laVersion1.isNullOrEmpty()){
            laVersion = laVersion1
        }
        log(laVersion1.toString())

        val moviesVersion1 = moviesLiveData.value?.version
        if (!moviesVersion1.isNullOrEmpty()){
            moviesVersion = moviesVersion1
        }
        log(moviesVersion1.toString())

        val hotelServicesVersion1 = hotelServicesLiveData.value?.version
        if (!hotelServicesVersion1.isNullOrEmpty()){
            hotelServicesVersion = hotelServicesVersion1
        }
        log(hotelServicesVersion1.toString())

        if (CMDRES.isEmpty()){
            CMDRES = Constants.CMDRES
        }

        val kapingCall = myApiService.getKapingService(Constants.KAPING,UA,"0",versionNumber,moviesVersion,Constants.DV,"1",
            Constants.KAPINGEVENT, "0",Constants.RBTY,Constants.MODE,laVersion,hotelServicesVersion,themeVersion,
            CMDRES,"1",Constants.INRMVER,Constants.LAUVER)

        kapingCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    val result = KapingResponseParsing().getResponseAsObject(data, KapingResponse::class)

                    val cmdres = result?.CMD
                    cmdres?.let {

                        CMDRES= (it.substring(0, minOf(it.length, 19)))+"00"
                        log("CMDRES -> $CMDRES")
                    }
                    // Handle the data here
                    log(result.toString())
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

                log(t.toString())
            }

        })

    }

    private fun getVersionNumber(): String {
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
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
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainMenuActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

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