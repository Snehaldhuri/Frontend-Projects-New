package com.diipl.moviebeam.utils

import android.app.Service
import android.content.Context
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeviceUtils @Inject constructor(
    @ApplicationContext val context : Context
) {
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getScreenResolution(): String {
        val wm = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val widthPixels = metrics.widthPixels
        val heightPixels = metrics.heightPixels
        return "$widthPixels x $heightPixels"
    }
}