package com.diipl.moviebeam.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.diipl.moviebeam.data.dto.remote.BTCommandModel
import com.diipl.moviebeam.utils.Constants.MDM_GRANT_PERMISSION
import com.diipl.moviebeam.utils.Constants.MDM_PACKAGE_NAME
import com.diipl.moviebeam.utils.IRUtils.Companion.FREQ_38_KHZ
import com.diipl.moviebeam.utils.IRUtils.Companion.FREQ_40_KHZ
import com.diipl.moviebeam.utils.IRUtils.Companion.LG
import com.diipl.moviebeam.utils.IRUtils.Companion.LG_DELAY
import com.diipl.moviebeam.utils.IRUtils.Companion.SAMSUNG
import com.diipl.moviebeam.utils.IRUtils.Companion.SAM_DELAY
import com.diipl.moviebeam.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.UUID

interface IBtService {
    fun transmit(command: ByteArray)
}

private const val TAG = "BTService"

class BTService(private val context: Context, lifecycle: Lifecycle) : IBtService, DefaultLifecycleObserver {

    private var servicesList = mutableListOf<BluetoothGattService>()
    private var btGatt: BluetoothGatt? = null
    private val gattService: BluetoothGattService? by lazy { getBleGattServiceInstance() }

    init {
        lifecycle.addObserver(this)
    }

    fun isConnected() : Boolean = btGatt != null && gattService != null

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    Log.e(TAG, "onConnectionStateChange: STATE_CONNECTED")
                    btGatt!!.discoverServices()
                }

                BluetoothGatt.STATE_CONNECTING -> Log.e(
                    TAG,
                    "onConnectionStateChange: STATE_CONNECTING"
                )

                BluetoothGatt.STATE_DISCONNECTED -> Log.e(
                    TAG,
                    "onConnectionStateChange: STATE_DISCONNECTED"
                )

                BluetoothGatt.STATE_DISCONNECTING -> Log.e(
                    TAG,
                    "onConnectionStateChange: STATE_DISCONNECTING"
                )
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            servicesList = gatt.services
            Log.e(TAG, "onServicesDiscovered: ---BluetoothGattService---")
            for (gattService in servicesList) {
                Log.e(
                    TAG,
                    "gattService.getUuid() " + gattService.uuid
                )
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int,
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            val uuid = characteristic.uuid
            if (uuid == UUID_CMD) {
                val value = String(characteristic.value, StandardCharsets.UTF_8)
                Log.e(TAG, "onCharacteristicWrite: back value=$value")
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun findBondedDevice() {
        Log.e(TAG, "findBondedDevice: Bluetooth device type, Low Energy - LE-only")
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e(TAG, "findBondedDevice: ${isBTPermissionGranted()}")
                if (!isBTPermissionGranted()) {
                    grantPermission()
                    return
                }
            }

            val devices = bluetoothAdapter.bondedDevices
            for (device in devices) {
                Log.e(
                    TAG,
                    "findBondedDevice: Type ->  ${device.name}  ${device.address}  ${device.type} "
                )
                if (device.name == REMOTE_NAME) {
                    if (device.type == BluetoothDevice.DEVICE_TYPE_LE) {
                        connectGatt(device)
                        break
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun isBTPermissionGranted(): Boolean =
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED)

    private fun grantPermission() {
        try {
            Log.e(TAG, "grantPermission: ")
            Intent(Intent.ACTION_VIEW).apply {
                component = ComponentName(MDM_PACKAGE_NAME, MDM_GRANT_PERMISSION)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
            CoroutineScope(Dispatchers.Default).launch {
                delay(1000)
                findBondedDevice()
            }
        } catch (e: Exception) {
            context.showToast("Please grant BT permissions!")
            Log.e(TAG, "grantPermission: Exception ->  ${e.localizedMessage}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectGatt(device: BluetoothDevice) {
        Log.e(TAG, "connectGatt: ${device.name}")
        btGatt = device.connectGatt(context, true, gattCallback)
    }

    private fun getBleGattServiceInstance(): BluetoothGattService? {
        for (gattService in servicesList) {
            if (gattService.uuid == UUID_SERVICE_DEVICE) {
                return gattService
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    override fun transmit(command: ByteArray) {
        if (btGatt != null) {
            if (gattService != null) {
                val characteristic = gattService!!.getCharacteristic(UUID_CMD)
                characteristic.setValue(command)

                btGatt!!.writeCharacteristic(characteristic)
            } else context.showToast(MSG_SERVICE_NOT_CONNECTED)
        } else context.showToast(MSG_BT_NOT_CONNECTED)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }

    companion object {
        val REMOTE_NAME = "MBCC Remote"
        val MSG_SERVICE_NOT_CONNECTED = "Service is not established with BT Remote. \\nPlease check your BT Remote connection!"
        val MSG_BT_NOT_CONNECTED = "BT is not connected to $REMOTE_NAME!"
        val UUID_SERVICE_DEVICE = UUID.fromString("6e40ff01-b5a3-f393-e0a9-e50e24dcca9e")
        val UUID_CMD = UUID.fromString("6e40ff02-b5a3-f393-e0a9-e50e24dcca9e")

        private val LG_TV = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0x1B)
        private val LG_HDMI1 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0XCE.toByte())
        private val LG_HDMI2 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0XCC.toByte())
        private val LG_HDMI3 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0xE9.toByte())
        private val LG_HDMI4 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0xDA.toByte())
        private val LG_OK = byteArrayOf()
        private val LG_TV0 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X10)
        private val LG_TV1 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X11)
        private val LG_TV2 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X12)
        private val LG_TV3 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X13)
        private val LG_TV4 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X14)
        private val LG_TV5 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X15)
        private val LG_TV6 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X16)
        private val LG_TV7 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X17)
        private val LG_TV8 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X18)
        private val LG_TV9 = byteArrayOf(0x48, 0x4C, 0x0C, 0x02, 0X19)

        private val sam_TV = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x1B)
        private val sam_HDMI1 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0xE9.toByte())
        private val sam_HDMI2 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0xBE.toByte())
        private val sam_HDMI3 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0xC2.toByte())
        private val sam_HDMI4 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0xC2.toByte())
        private val sam_OK = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x68)
        private val sam_TV0 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x11)
        private val sam_TV1 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x04)
        private val sam_TV2 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x05)
        private val sam_TV3 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x06)
        private val sam_TV4 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x08)
        private val sam_TV5 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x09)
        private val sam_TV6 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x0a)
        private val sam_TV7 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x0C)
        private val sam_TV8 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x0d)
        private val sam_TV9 = byteArrayOf(0x48, 0x4C, 0x0C, 0x01, 0x0E)


        val lgModel = BTCommandModel(
            LG,
            FREQ_38_KHZ,
            LG_DELAY,
            LG_HDMI1,
            LG_HDMI2,
            LG_HDMI3,
            LG_OK,
            LG_TV,
            LG_TV0,
            LG_TV1,
            LG_TV2,
            LG_TV3,
            LG_TV4,
            LG_TV5,
            LG_TV6,
            LG_TV7,
            LG_TV8,
            LG_TV9
        )

        val samsungModel = BTCommandModel(
            SAMSUNG,
            FREQ_40_KHZ,
            SAM_DELAY,
            sam_HDMI1,
            sam_HDMI2,
            sam_HDMI3,
            sam_OK,
            sam_TV,
            sam_TV0,
            sam_TV1,
            sam_TV2,
            sam_TV3,
            sam_TV4,
            sam_TV5,
            sam_TV6,
            sam_TV7,
            sam_TV8,
            sam_TV9
        )


    }

}