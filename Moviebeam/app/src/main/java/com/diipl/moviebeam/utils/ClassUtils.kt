package com.diipl.moviebeam.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.TypeConverter
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.room.models.RentalMovieModel
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
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.text.SimpleDateFormat
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
    val startColor = Constants.GRADIENT_COLOR_START.ifEmpty { Constants.DEFAULTGRADIENTSTARTCOLOR }
    val endColor = Constants.GRADIENT_COLOR_END.ifEmpty { Constants.DEFAULTGRADIENTENDCOLOR }
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    )
    gradientDrawable.cornerRadius = 20f
    gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
    gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

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
        val fileURL = "https://testmdm.movie-beam.com/files/files-by-google-1-2729-610141523-0-release.apk"
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
    val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
    val address = networkInterfaces[1].interfaceAddresses[1]
            Collections.list(NetworkInterface.getNetworkInterfaces())

    Constants.IP_ADDRESS = address.address?.hostAddress ?: "0.0.0.0"
    Constants.IP_NET_MASK = getNetmaskFromPrefixLength(address.networkPrefixLength.toInt())

    currentActivity?.let {
        val connectivityManager = it.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = it.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        connectivityManager.activeNetwork?.let { network ->
            // Get the LinkProperties for the active network
            val linkProperties: LinkProperties? = connectivityManager.getLinkProperties(network)
            // Get the default gateway from the LinkProperties
            val defaultGateway = linkProperties?.routes?.get(2)?.gateway?.hostAddress.toString()
            Constants.IP_GATEWAY = defaultGateway
        }

        // WIFI DETAILS
        if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (wifiManager.isWifiEnabled) {
                val connectionInfo = wifiManager.connectionInfo
                val strength = WifiManager.calculateSignalLevel(connectionInfo.rssi, 5)
            }
        }

        // DEVICE UPTIME
        val uptimeMillis = System.currentTimeMillis() - SystemClock.uptimeMillis()
        val uptime = System.currentTimeMillis() - uptimeMillis

    }
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
            else -> "UNKNOWN NETWORK"
        }
    } else {
        return when (connectivityManager.activeNetworkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> "WIFI"
            ConnectivityManager.TYPE_MOBILE -> "MOBILE DATA"
            ConnectivityManager.TYPE_ETHERNET -> "LAN"
            ConnectivityManager.TYPE_BLUETOOTH -> "BLUETOOTH"
            ConnectivityManager.TYPE_VPN -> "VPN"
            else -> "UNKNOWN NETWORK"
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
        PendingIntent.getBroadcast(this, generateUniqueRequestCode(endTime), intent, PendingIntent.FLAG_IMMUTABLE)

    // Set the alarm to trigger at the specified time
    alarmManager.setExact(AlarmManager.RTC, endTime, pendingIntent)
}

private fun generateUniqueRequestCode(endTime: Long): Int {
    // Generate a unique requestCode, for example based on current time
    return endTime.toInt()
}