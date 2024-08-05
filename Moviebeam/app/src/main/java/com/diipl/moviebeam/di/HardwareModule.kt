package com.diipl.moviebeam.di

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.android.tv.settings.aidl.common.ISeiCommonApi
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.room.dao.ProgramGuideDao
import com.diipl.moviebeam.room.dao.RentalMovieDao
import com.diipl.moviebeam.room.db.MoviesDatabase
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HardwareModule {

    private val TAG = "HardwareModule"

    @Singleton
    @Provides
    fun getHardwareInstance(@ApplicationContext context: Context): HardwareAPI {
        return HardwareAPI(context)
    }

}

class HardwareAPI(val context: Context) : ServiceConnection {

    val TAG = "HardwareAPI"
    var myService: ISeiCommonApi? = null
    var bindSuccess = false

    init  {
        val intent = Intent().also {
            it.component = ComponentName(
                "com.android.tv.settings",
                "com.android.tv.settings.aidl.common.SeiCommonApiService"
            )
        }
        context.bindService(intent, this, Application.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        myService = ISeiCommonApi.Stub.asInterface(service)
        bindSuccess = true
        Log.e(TAG, "onServiceConnected: $bindSuccess  -> ${myService?.deviceSn}")
    }

    override fun onServiceDisconnected(name: ComponentName) {
        bindSuccess = false
        myService = null
        Log.e(TAG, "onServiceDisconnected: ")
    }

}