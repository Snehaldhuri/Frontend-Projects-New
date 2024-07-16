package com.diipl.moviebeam.service.kappingservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.diipl.moviebeam.ui.splash.BlankActivity
import com.diipl.moviebeam.utils.Constants.isRebooted
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.log

class StartReceiver : BroadcastReceiver() {

    private val TAG = "StartReceiver"
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT){
            Log.e(TAG, "onReceive: ${intent.action}")
//            context.showToast(intent.action!!)
            isRebooted = 1
//            GLOBAL_LOOP_SEC = 30
            try {
              /*  Intent().apply {
                    component = ComponentName(
                        context.packageName,
                        SerialActivity::class.java.name
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(this)
                }*/
                val preference = SharedPreference(context)
                if (preference.irFrequencyModel == null){
                    preference.irFrequencyModel = IRUtils.SELECTED_BRAND
                }
                context.clearCache()
            } catch (e: Exception){
                Log.e(TAG, "Exception: ${e.localizedMessage}")
            }
        }
        if ((intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log("Starting the service in >=26 Mode from a BroadcastReceiver")
                    context.startForegroundService(it)
                    return
                }
                log("Starting the service in < 26 Mode from a BroadcastReceiver")
                context.startService(it)
            }
        }
       /* if (intent.extras?.getString("onstop").equals("RESTART")) {
            val i = Intent(context, MainMenuActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)

        }*/
        if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED){
            val i = Intent(context, BlankActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(i)
            context.clearCache()
        }
    }
}