package com.diipl.moviebeam.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.TypeConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.service.ClearCredentialsReceiver
import com.diipl.moviebeam.service.TickerMsgReceiver
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.currentActivity
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HelpInfoFragment
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.movies.MovieDetailFragment
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.programguide.PrgGuidePlayerActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.serial_info.SerialActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeDetailFragment
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


fun <T : Any> T.toQueryMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()

    this::class.declaredMemberProperties.forEach { prop ->
        val serializedName = prop.javaField?.getAnnotation(SerializedName::class.java)?.value
        val key = serializedName ?: prop.name
        val value = prop.call(this)
        map[key] = value as Any
    }

    return map
}

fun String.isNotAllowed(): Boolean {
    var result = true
    when (this) {
        MainMenuActivity::class.java.simpleName -> result = true
        SerialActivity::class.java.simpleName -> result = false
        STBDetailsActivity::class.java.simpleName -> result = true
        RegisterSTBActivity::class.java.simpleName -> result = false
    }
    return result
}

inline fun <reified T> T.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

fun RentalMovieModel.getRentalDetails(): String {
    val timeStamp = System.currentTimeMillis()
    // UA + ":" + ReleaseId + ":" + ProductId + ":" + Price + ":" + TimeStamp + ":" + SessionId + ":" + 5
    return this.movieData?.let {
        "${Constants.UA}:${it.releaseId}:${it.productId}:${it.price}:${timeStamp / 1000}:${Constants.SESSION_ID}:5"
    }.toString()
}

fun String.toTimestamp(): Long {
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
    return try {
        val date = dateFormat.parse(this)
        date.time
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun getWidthInPercent(context: Context, percent: Int): Int {
    val width = context.resources.displayMetrics.widthPixels
    return (width * percent) / 100
}

fun getHeightInPercent(context: Context, percent: Int): Int {
    val width = context.resources.displayMetrics.heightPixels
    return (width * percent) / 100
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}

fun replaceDegreeSymbol(temp: String?): String {
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

fun ExoPlayer?.getLastSeek(): Long {
    if (this != null) {
        if (this.contentPosition.toTimeFormat() == this.duration.toTimeFormat()) {
            return 0
        }
        return this.contentPosition
    }
    return 0
}

fun getGradientColor(): GradientDrawable {
    if (Constants.GRADIENT != null)
        return Constants.GRADIENT!!
    val startColor = Constants.GRADIENT_COLOR_START.ifEmpty { Constants.DEFAULTGRADIENTSTARTCOLOR }
    val endColor = Constants.GRADIENT_COLOR_END.ifEmpty { Constants.DEFAULTGRADIENTENDCOLOR }
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TR_BL,
        intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    )
    gradientDrawable.cornerRadius = 20f
    gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

    gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
    return gradientDrawable
}

fun getGradientColorForTable(): GradientDrawable {
    val startColor = Constants.GRADIENT_COLOR_START.ifEmpty { Constants.DEFAULTGRADIENTSTARTCOLOR }
    val endColor = Constants.GRADIENT_COLOR_END.ifEmpty { Constants.DEFAULTGRADIENTENDCOLOR }
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TR_BL,
        intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    )
    gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
    gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
    return gradientDrawable
}

fun View.handleFocusChange() {
    setOnFocusChangeListener { _, b ->
        if (b) {
            background = getGradientColor()
        } else {
            setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }
}


fun RecyclerView.setItemFocused() {
    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        if (childView != null) {
            val viewHolder = getChildViewHolder(childView)
            if (viewHolder.itemView.hasFocus()) {
                return
            }
        }
    }
    this.post {
        this.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
    }
}

fun Long.toDateFormat(): String {
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
    val date = Date(this)
    return dateFormat.format(date)
}

fun Long.toSimpleTimeFormat(): String {
    val seconds = this / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secondsRemaining = seconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, secondsRemaining)
}

fun Long.toTimeFormat(): String {
    var time = ""
    var minute = ""
    var secs = ""
    val min = this / 1000 / 60
    val sec = this / 1000 % 60
    minute = if (min < 10) "0$min" else "" + min
    secs = if (sec < 10) "0$sec" else "" + sec
    time = "$minute:$secs"
    return time
}

fun Activity.startDownload() = CoroutineScope(Dispatchers.Default).launch {
    try {
        val fileURL =
            "https://testmdm.movie-beam.com/files/files-by-google-1-2729-610141523-0-release.apk"
        val url = URL(fileURL)
        withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val directory = File(externalMediaDirs[0].path + "/APK")
                if (!directory.exists()) directory.mkdirs()
                val file = File(directory, url.path.substringAfterLast("/"))
                val outputStream = FileOutputStream(file)
                val inputStream = connection.inputStream
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                inputStream.close()
                outputStream.close()
                Log.e("startDownload:", " Completed  --->  ${file.absolutePath}")
//                startInstall(file.absolutePath)
            } else {
                // Handle the error or show a message if download fails
                Log.e("startDownload:", "Failed")
            }
            connection.disconnect()
        }

    } catch (e: Exception) {
        Log.e("startDownload:", " Exception: ${e.message}")
    }
}

fun Activity.startInstall(file: String) {
    val apkFile =
        "/storage/emulated/0/Android/media/com.diipl.moviebeam/APK/Moviebeam_Prod_V(2.2.4)_20240315-debug.apk"

    try {
        val `in` = FileInputStream(apkFile)
        val packageInstaller: PackageInstaller = packageManager.packageInstaller
        val params = SessionParams(
            SessionParams.MODE_FULL_INSTALL
        )
        params.setAppPackageName(packageName)
        // set params
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)
        val out = session.openWrite("COSU", 0, -1)
        val buffer = ByteArray(65536)
        var c: Int
        while (`in`.read(buffer).also { c = it } != -1) {
            out.write(buffer, 0, c)
        }
        session.fsync(out)
        `in`.close()
        out.close()
        session.commit(
            createIntentSender(
                this,
                sessionId,
                packageName
            )
        )
        Log.e("startInstall", "Installation session committed")
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    } catch (e: java.lang.Exception) {
        Log.e("startInstall", "PackageInstaller error: " + e.message)
    }
}

fun createIntentSender(context: Context?, sessionId: Int, packageName: String?): IntentSender {
    val intent = Intent("INSTALL_COMPLETE")
    if (packageName != null) {
        intent.putExtra("PACKAGE_NAME", packageName)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        sessionId,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    return pendingIntent.intentSender
}

fun Context.getApkLists() {
    val mainIntent = Intent(Intent.ACTION_MAIN, null)
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    val apps = packageManager.queryIntentActivities(mainIntent, 0)
    for (info in apps) {
        val file = File(info.activityInfo.applicationInfo.publicSourceDir)
        Log.e("getApkLists", "onResume: ${file.absolutePath}")
    }
}

fun getCurrentPanelNumber(): String {
    if (BaseActivity.activityStack.isNotEmpty()) {
        when (BaseActivity.activityStack.last()) {
            RegisterSTBActivity::class.java.simpleName -> return PanelConstants.BLUE_SCREEN
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
    return PanelConstants.MAIN_MENU
}

fun setIPInfo() = CoroutineScope(Dispatchers.IO).launch {

    // NETWORK DETAILS
    try {
        val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        val address = networkInterfaces[1].interfaceAddresses[1]

        Constants.IP_ADDRESS = address.address?.hostAddress ?: "0.0.0.0"
        Constants.IP_NET_MASK = getNetmaskFromPrefixLength(address.networkPrefixLength.toInt())

        currentActivity?.let {
            val connectivityManager =
                it.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiManager =
                it.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            connectivityManager.activeNetwork?.let { network ->
                // Get the LinkProperties for the active network
                val linkProperties: LinkProperties? = connectivityManager.getLinkProperties(network)
                // Get the default gateway from the LinkProperties
                val defaultGateway = linkProperties?.routes?.get(2)?.gateway?.hostAddress.toString()
                Constants.IP_GATEWAY = defaultGateway
            }

            // WIFI DETAILS
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (wifiManager.isWifiEnabled) {
                    val connectionInfo = wifiManager.connectionInfo
                    val strength = WifiManager.calculateSignalLevel(connectionInfo.rssi, 5)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("setIPInfo: ", "Exception :  ${e.localizedMessage}")
    }

// DEVICE UPTIME
    val uptimeMillis = System.currentTimeMillis() - SystemClock.uptimeMillis()
    val uptime = System.currentTimeMillis() - uptimeMillis

}

private fun getNetmaskFromPrefixLength(prefixLength: Int): String {
    var length = prefixLength
    require(!(length < 0 || length > 32)) { "255.255.255.255" }

    // Calculate the netmask bytes based on the prefix length
    val netmaskBytes = ByteArray(4)
    for (i in 0..3) {
        val bits = length.coerceAtMost(8)
        netmaskBytes[i] = (0xFF shl 8 - bits).toByte()
        length -= bits
    }
    val netmaskAddress = InetAddress.getByAddress(netmaskBytes)
    return netmaskAddress.hostAddress ?: "255.255.255.255"
}

fun getConnectivityType(context: Context): String {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork
        val actNw = connectivityManager.getNetworkCapabilities(nw)
        return when {
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WIFI"
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "MOBILE DATA"
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "LAN"
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "BLUETOOTH"
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> "VPN"
            else -> "NO NETWORK"
        }
    } else {
        return when (connectivityManager.activeNetworkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> "WIFI"
            ConnectivityManager.TYPE_MOBILE -> "MOBILE DATA"
            ConnectivityManager.TYPE_ETHERNET -> "LAN"
            ConnectivityManager.TYPE_BLUETOOTH -> "BLUETOOTH"
            ConnectivityManager.TYPE_VPN -> "VPN"
            else -> "NO NETWORK"
        }
    }
}

class Converters {
    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? {
        val mapType = object : TypeToken<Map<String, String>?>() {}.type
        return Gson().fromJson(value, mapType)
    }
}

fun readFileToString(fileName: String): String {
    val str = StringBuilder()
    File(fileName).forEachLine {
        str.append(it)
    }
    return str.toString()
}

fun Context.scheduleMsgEndTask(tickerDTO: TvTickerDTO) {
    val sdf = SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
    val endTime = sdf.parse(tickerDTO.etStr!!)!!.time
    val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, TickerMsgReceiver::class.java)
    intent.putExtra(Constants.TICKER_DTO_PARAM, tickerDTO)
//    intent.putExtra("START_TIME", startTimeStr)
//    intent.putExtra("END_TIME", endTimeStr)
    val pendingIntent =
        PendingIntent.getBroadcast(
            this,
            generateUniqueRequestCode(endTime),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

    // Set the alarm to trigger at the specified time
    alarmManager.setExact(AlarmManager.RTC, endTime, pendingIntent)
}

private fun generateUniqueRequestCode(endTime: Long): Int {
    // Generate a unique requestCode, for example based on current time
    return endTime.toInt()
}

fun ConstraintLayout.loadBg() {
    val url = Constants.BG_IMAGE
    Log.e("TAG", "loadBg: $url")
    if (!url.isNullOrEmpty())
        Glide.with(this).load(url)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
}

fun String.toInteger(): Int? {
    var result: Int? = null
    if (this.isNotEmpty()) {
        result = this.toInt()
    }
    return result
}

fun Context.clearCache() {
    cacheDir.delete()
    cacheDir.deleteRecursively()
    codeCacheDir.delete()
    codeCacheDir.deleteRecursively()
}

fun fetchCurrentProgramKey(cal: Calendar = Calendar.getInstance()): String {
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

val BASE_FILE_PATH = currentActivity?.externalMediaDirs?.get(0)?.absolutePath

val HS_FILE_PATH = "$BASE_FILE_PATH/HS/"
val LA_FILE_PATH = "$BASE_FILE_PATH/LA/"
val THEME_FILE_PATH = "$BASE_FILE_PATH/THEME/"

suspend fun saveImageServer(imgUrl: String?, filePath: String): String? {
    var path: String? = null
    try {
        val uri = Uri.parse(imgUrl)
        val imageName = uri.getQueryParameter("imageName")
        val url = uri.toURL()
        val imageData = withContext(Dispatchers.IO) { url.readBytes() }

        path = "$filePath${imageName}"
        writeByteArrayToFile(path, imageData)
    } catch (e: Exception) {
        Log.e("TAG", "Failed to save image: ${e.message}")
    }
    return path
}

suspend fun saveImage(imgUrl: String?, filePath: String): String? {
    var path: String?
    try {
        val url = URL(imgUrl)
        val imageData = withContext(Dispatchers.IO) { url.readBytes() }
        val extension = "." + url.path.substringAfterLast(".").lowercase()
        path = "$filePath${System.currentTimeMillis()}$extension"
        writeByteArrayToFile(path, imageData)
    } catch (e: Exception) {
        Log.e("saveImage", "Exception: ${e.localizedMessage}")
        path = imgUrl
    }
    return path
}

suspend fun saveHSImage(imgUrl: String?) = saveImage(imgUrl, HS_FILE_PATH)
suspend fun saveLAImage(imgUrl: String?) = saveImage(imgUrl, LA_FILE_PATH)
suspend fun saveThemeImage(imgUrl: String?) = saveImage(imgUrl, THEME_FILE_PATH)

suspend fun saveThemeImageServer(imgUrl: String?) = saveImageServer(imgUrl, THEME_FILE_PATH)

fun deleteFolder(filePath: String) {
    val file = File(filePath)
    if (file.exists()) {
        file.deleteRecursively()
    }
}

fun deleteHSFolder() = deleteFolder(HS_FILE_PATH)
fun deleteLAFolder() = deleteFolder(LA_FILE_PATH)
fun deleteThemeFolder() = deleteFolder(THEME_FILE_PATH)

private fun writeByteArrayToFile(filePath: String, byteArray: ByteArray) {
    try {
        val file = File(filePath)
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }
        file.writeBytes(byteArray)
    } catch (e: IOException) {
        Log.e("writeByteArrayToFile", "IOException: ${e.message}")
        e.printStackTrace()
    }
}

fun Uri.toURL(): URL {
    return URL(this.toString())
}

fun Context.clearCredentials() {
    LoggingService.sendMessageToWebSocket(
        "Clearing Application credentials.",
        getCurrentPanelNumber()
    )
    if (Constants.APP_LIST.isEmpty()) {
        LoggingService.sendMessageToWebSocket(
            "App List is empty.",
            getCurrentPanelNumber()
        )
        return
    }
    val intent = Intent(Constants.MDM_CLEAR_CREDENTIALS_ACTION).apply {
        setPackage(Constants.MDM_PACKAGE_NAME)
        putStringArrayListExtra(Constants.APP_LIST_PARAM, Constants.APP_LIST)
    }
    sendBroadcast(intent)

    LoggingService.sendMessageToWebSocket(
        "Clearing Application credentials done.",
        getCurrentPanelNumber()
    )
}

fun Context.scheduleClearCredentialsTask(checkOutTime: String?) {
    checkOutTime?.let {
        val sdf = SimpleDateFormat(Constants.CHECK_OUT_TIME_DATE_FORMAT, Locale.ENGLISH)
        val endTime = sdf.parse(it)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ClearCredentialsReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                Constants.CLEAR_CREDENTIALS_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_MUTABLE
            )

        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endTime.hours)
            set(Calendar.MINUTE, endTime.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time is before now, add one day to ensure it triggers tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Set the repeating alarm to go off at 11 AM every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}

fun Activity.launchLogger() {
    lateinit var loggingService: LoggingService

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LoggingService.LoggingServiceBinder
            loggingService = binder.getService()
            loggingService.startWebSocket()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
    val serviceIntent = Intent(this, LoggingService::class.java)
    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
}

fun compareVersions(apkVersion: String): Boolean {
    val a = apkVersion.replace(".", "").toInt()
    val b = BuildConfig.VERSION_NAME.replace(".", "").toInt()
    return a != b
}
