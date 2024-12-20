package com.diipl.moviebeam.service.remote

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.ui.base.BlankActivity

private const val TAG = "UsbIrService"

const val ACTION_USB_PERMISSION = "${BuildConfig.APPLICATION_ID}.USB_PERMISSION"

interface IIrService {
    fun transmit(freq: Int, pattern: IntArray)

    fun isConnected(): Boolean

}

class UsbIrService(private val context: Context) : IIrService {

    private val usbManager: UsbManager by lazy { context.getSystemService(USB_SERVICE) as UsbManager }
    private lateinit var usbDevice: UsbDevice
    private var instance : IIrService? = null
    private var mConnection: UsbDeviceConnection? = null
    private var epOUT: UsbEndpoint? = null
    private var epIN: UsbEndpoint? = null

    companion object {

        private var _usbPackCnt: Byte = 1
        private var _cmdCnt: Byte = 0
        fun getCmdId(): Byte {
            if (_cmdCnt < 127) _cmdCnt++ else _cmdCnt = 1
            return _cmdCnt
        }

        fun getUsbPackId(): Byte {
            if (_usbPackCnt < 15) _usbPackCnt++ else _usbPackCnt = 1
            return _usbPackCnt
        }
    }

    init {
        usbManager.deviceList.values.forEach {
            if (isCompatibleDevice(it)) {
                usbDevice = it
                val isOk = usbManager.hasPermission(usbDevice)
                if (isOk) {
                    getInstance()
                } else {
                    val i = Intent(context, BlankActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(i)
                }
            }
        }
    }

    override fun isConnected() : Boolean = mConnection!=null && epIN != null && epOUT != null


    fun getInstance(): IIrService? {
        if (instance == null && isCompatibleDevice(usbDevice)) {
            var endpointIN: UsbEndpoint? = null
            var endpointOUT: UsbEndpoint? = null
            val usbInterface = usbDevice.getInterface(0)

            for (i in 0 until usbInterface.endpointCount) {
                usbInterface.getEndpoint(i).let { endpoint ->
                    if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        when (endpoint.direction) {
                            UsbConstants.USB_DIR_IN -> endpointIN = endpoint
                            UsbConstants.USB_DIR_OUT -> endpointOUT = endpoint
                            else -> Log.e(TAG, "undefined endpoints direction")
                        }
                    }
                }
            }

            if (endpointIN == null || endpointOUT == null) {
                Log.e(TAG, "Failed setting endpoint")
                instance = null
                return instance
            }

            val connection = usbManager.openDevice(usbDevice)
            if (connection == null || !connection.claimInterface(usbInterface, true)) {
                Log.e(TAG, "open device FAIL!")
                instance = null
                return instance
            }

            mConnection = connection
            epOUT = endpointOUT
            epIN = endpointIN

            Log.d(TAG, "open device SUCCESS!")
            return instance
        }
        return instance
    }

    override fun transmit(freq: Int, pattern: IntArray) {

        val tqIrWriteFragments = mutableListOf<Byte>().apply {
            add(83)                                 // const. S
            add(84)                                 // const. T
            add(getCmdId())                         // cmdId
            add(68)                                 // D - Transfer mode
            add(freq.toByte())                                  // freq - ignored, not worked
            addAll(consumeIrToByteCode(pattern))    // payload
            add(69)                                 // const. E
            add(78)                                 // const. N
        }.chunked(56)                           // max payload size 61 byte (-5 bit header)

        val cmdUsbPackId = getUsbPackId()
        val fragmentCount = tqIrWriteFragments.size
        val toTransfer = mutableListOf<List<Byte>>()

        tqIrWriteFragments.forEachIndexed { index, fragment ->
            val fragmentHeader = listOf(
                2,                  // buf[0] const. ReportId = 2
                fragment.size + 3,  // buf[1] packet size
                cmdUsbPackId,       // buf[2] cmd id
                fragmentCount,      // buf[3] total fragment count
                index + 1,          // buf[4] fragment id always > 0
            ).map { it.toByte() }

            toTransfer.add(fragmentHeader.plus(fragment))
        }

        toReady()
        toTransfer.forEach {
            bulkTransfer(it)
        }
        toSleep()
    }

    private fun toReady() {
        bulkTransfer(
            byteArrayOf(
                2,
                9,
                getUsbPackId(),
                1,
                1,
                83,
                84,
                getCmdId(),
                83,
                69,
                78
            ).toList()
        ) // S - SendMode
    }

    private fun toSleep() {
        bulkTransfer(
            byteArrayOf(
                2,
                9,
                getUsbPackId(),
                1,
                1,
                83,
                84,
                getCmdId(),
                76,
                69,
                78
            ).toList()
        ) // L -  IdleMode
    }

    private fun bulkTransfer(data: List<Byte>) {
        val byteWrite = data.toByteArray()
        try {
            mConnection?.let {connection->
                connection.bulkTransfer(epOUT, byteWrite, byteWrite.size, 400)
                val bytesRead = epIN?.let { ByteArray(it.maxPacketSize) }
                bytesRead?.let { connection.bulkTransfer(epIN, bytesRead, it.size, 400) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.localizedMessage)
        }
    }
}

@SuppressLint("WrongConstant")
fun requestUsbPermissionForCompatibleDev(context: Context, usbManager: UsbManager) {
    for (device: UsbDevice in usbManager.deviceList.values) {
        if (isCompatibleDevice(device) && !usbManager.hasPermission(device)) {
            val requestCode = 0
            val intent = Intent(ACTION_USB_PERMISSION)
            val piFlags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            val pi = PendingIntent.getBroadcast(context, requestCode, intent, piFlags)
            usbManager.requestPermission(device, pi)
        }
    }
}

fun consumeIrToByteCode(consumeIrPattern: IntArray): ArrayList<Byte> {
    val usbTickPattern = consumeIrPattern.map { it / 16 }
    val result = ArrayList<Byte>(usbTickPattern.size)
    for ((index, tickCount) in usbTickPattern.withIndex()) {
        result.addAll(usbTickToUsbByteCode(tickCount, index % 2 == 0))
    }
    return result
}

fun usbTickToUsbByteCode(tickCount: Int, isOn: Boolean): List<Byte> {
    val maximumBlockSize = 127
    var i = tickCount
    val result = ArrayList<Int>()
    while (i > 0) {
        var sendBlockSize = i
        if (sendBlockSize > maximumBlockSize) {
            sendBlockSize = maximumBlockSize
        }
        i -= sendBlockSize
        if (isOn) {
            sendBlockSize = sendBlockSize or 128
        }
        result.add(sendBlockSize)
    }
    return result.map { it.toByte() }
}

fun isCompatibleDevice(device: UsbDevice): Boolean {
    if (device.interfaceCount != 1) return false
    if (device.getInterface(0).endpointCount == 0) return false
    if (device.vendorId == 4292 && device.productId == 33896) return true
    if (device.vendorId == 1118 && device.productId == 33896) return true
    return false
}