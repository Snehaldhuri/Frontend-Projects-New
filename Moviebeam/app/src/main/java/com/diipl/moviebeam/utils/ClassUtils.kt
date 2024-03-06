package com.diipl.moviebeam.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
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
import java.text.SimpleDateFormat
import java.util.Calendar
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
    // UA + ":" + ReleaseId + ":" + ProductId + ":" + Price + ":" + TimeStamp + ":" + SessionId + ":" + 5
    return this.movieData?.let {
        "${Constants.UA}:${it.releaseId}:${it.productId}:${it.price}:${System.currentTimeMillis()}:${Constants.SESSION_ID}:5"
    }.toString()
}

fun isRentalMovieTimeOver(): Boolean {
    val timestamp1 = System.currentTimeMillis()
    val timestamp2 = Constants.RENTAL_TIME // 24 hours ago

    // Convert timestamps to Calendar objects
    val calendar1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val calendar2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    // Compare timestamps with a 24-hour difference
    val is24HoursApart = calendar1.after(
        Calendar.getInstance().apply { timeInMillis = timestamp2 + (24 * 60 * 60 * 1000) })


    return is24HoursApart
}

fun String.toTimestamp(): Long {
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
    Log.e("toTimestamp: ", this)
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

fun getCurrentPanelNumber(): String {
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
