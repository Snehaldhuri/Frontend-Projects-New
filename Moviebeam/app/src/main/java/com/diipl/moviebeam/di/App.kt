package com.diipl.moviebeam.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    private val TAG = "App"

    @Inject
    lateinit var workerFactory : HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

       /* Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e(TAG, "onCreate: ${e.localizedMessage}")
//            if (BaseActivity.activityStack.isEmpty())
                Intent(applicationContext, SerialActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(this)
                }
        }*/

    }

}