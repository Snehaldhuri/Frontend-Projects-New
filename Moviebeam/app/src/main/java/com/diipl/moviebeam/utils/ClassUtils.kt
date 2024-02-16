package com.diipl.moviebeam.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.diipl.moviebeam.Constants
import com.google.gson.annotations.SerializedName
import java.util.Calendar
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

fun isRentalMovieTimeOver(): Boolean{
    val timestamp1 = System.currentTimeMillis()
    val timestamp2 = Constants.RENTAL_TIME // 24 hours ago

    // Convert timestamps to Calendar objects
    val calendar1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val calendar2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    // Compare timestamps with a 24-hour difference
    val is24HoursApart = calendar1.after(Calendar.getInstance().apply { timeInMillis = timestamp2 + (24 * 60 * 60 * 1000) })


    return is24HoursApart
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
