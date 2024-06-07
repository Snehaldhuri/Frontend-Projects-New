package com.diipl.moviebeam.ui.splash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.diipl.moviebeam.R
import com.diipl.moviebeam.service.ACTION_USB_PERMISSION
import com.diipl.moviebeam.service.requestUsbPermissionForCompatibleDev
import com.diipl.moviebeam.utils.clearCache
import kotlin.system.exitProcess

class BlankActivity : AppCompatActivity() {

    private val TAG = "BlankActivity"
    private val usbManager: UsbManager by lazy { getSystemService(USB_SERVICE) as UsbManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)

        //clearCache()

        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)

        requestUsbPermissionForCompatibleDev(this, usbManager)

    }

    private fun initSet() {
        usbManager.deviceList.values.forEach {
           /* if (isCompatibleDevice(it)) {
                usbDevice = it
                val isOk = usbManager.hasPermission(usbDevice)
                if (!isOk) {

                }
            }*/
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(usbReceiver)
    }


    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    val isGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    if (isGranted && device != null) {
                        clearCache()
//                        context.startActivity(Intent(this@BlankActivity, STBDetailsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//                        finish()
                        exitProcess(0)
                    } else {
                        finish()
                    }
                }
            }
        }
    }
}