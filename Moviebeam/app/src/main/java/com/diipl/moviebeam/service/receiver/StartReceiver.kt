package com.diipl.moviebeam.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.diipl.moviebeam.service.kappingservice.Actions
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.service.kappingservice.ServiceState
import com.diipl.moviebeam.service.kappingservice.getServiceState
import com.diipl.moviebeam.ui.base.BlankActivity
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.logD

class StartReceiver : BroadcastReceiver() {

    private val TAG = "StartReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) {
            Log.e(TAG, "onReceive: ${intent.action}")
            try {
                val preference = SharedPreference(context)
                if (preference.irFrequencyModel == null) {
                    preference.irFrequencyModel = IRUtils.SELECTED_IR_MODEL
                }
                if (preference.btCommandModel == null) {
                    preference.btCommandModel = IRUtils.SELECTED_BT_MODEL
                }
                context.clearCache()
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.localizedMessage}")
            }
        }
        if ((intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) && getServiceState(
                context
            ) == ServiceState.STARTED
        ) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    logD("Starting the service in >=26 Mode from a BroadcastReceiver")
                    context.startForegroundService(it)
                    return
                }
                logD("Starting the service in < 26 Mode from a BroadcastReceiver")
                context.startService(it)
            }
        }
        /* if (intent.extras?.getString("onstop").equals("RESTART")) {
             val i = Intent(context, MainMenuActivity::class.java)
             i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
             context.startActivity(i)

         }*/
        if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            val i = Intent(context, BlankActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)
            context.clearCache()
        }
    }
}