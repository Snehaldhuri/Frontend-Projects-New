package com.diipl.moviebeam

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "App"
@HiltAndroidApp
open class App : Application(), LifecycleEventObserver {

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val eve : String = when(event){
            Lifecycle.Event.ON_CREATE-> "Event -> ON_CREATE"
            Lifecycle.Event.ON_START-> "Event -> ON_START"
            Lifecycle.Event.ON_RESUME-> "Event -> ON_RESUME"
            Lifecycle.Event.ON_PAUSE-> "Event -> ON_PAUSE"
            Lifecycle.Event.ON_STOP-> "Event -> ON_STOP"
            Lifecycle.Event.ON_DESTROY-> "Event -> ON_DESTROY"
            Lifecycle.Event.ON_ANY-> "Event -> ON_ANY"
        }
        Log.e("Lifecycle Event", eve)

        when(event){
            Lifecycle.Event.ON_PAUSE -> {
                Log.e("Lifecycle Event", "ON_PAUSE to start MainMenuActivity")
//               sendBroadcast(Intent(this, StartReceiver::class.java).putExtra("onstop", "RESTART"))
                val i = Intent(this, MainMenuActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
            else -> {

            }
        }

    }

}
