package com.diipl.moviebeam.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.dto.logs.LogDTO
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.toInteger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LoggingService : Service() {

    //Variables from datastore
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            applicationContext
        )
    }

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    private lateinit var client: OkHttpClient
    private val binder = LoggingServiceBinder()
    var updateJob: Job? = null

    inner class LoggingServiceBinder : Binder() {
        fun getService(): LoggingService {
            return this@LoggingService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        initData()
        updateData()

    }

    private fun updateData() {
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                if (accountId == "") {
                    initData()
                } else {
                    updateJob?.cancel()
                }
                delay(1000 * 10)
            }
        }
    }

    private fun initData() = CoroutineScope(Dispatchers.Default).launch {
        preferenceHandler.loadAllData()
        delay(100)
        accountId = preferenceHandler.accountID
        stbRoomNo = preferenceHandler.roomNo
        ua = preferenceHandler.UA
        ipAddress = preferenceHandler.ipAddress
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceStarted)
            startForegroundService()
        else {
            stopForeground(STOP_FOREGROUND_REMOVE)
            startForegroundService()
        }
        startWebSocket()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "LoggingServiceChannel")
            .setContentTitle("Logging Service")
            .setContentText("Service is running")
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    fun startWebSocket() {
        if (!isServiceStarted) {
            isServiceStarted = true
            Log.e(TAG, "startWebSocket: Starting")
            client = OkHttpClient.Builder()
                .build()

            val request = Request.Builder()
                .url(BuildConfig.WS_URL)
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isServiceStarted = true
                    Log.e(TAG, "WebSocket connection opened")
                    updateNetworkStatus(true)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.e(TAG, "WebSocket Received message: $text")
                    updateNetworkStatus(true)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    isServiceStarted = false
                    Log.e(TAG, "WebSocket connection closed")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    isServiceStarted = false
                    Log.e(TAG, "WebSocket connection failure: ${t.message}")
                    if (!t.message.equals(NO_INTERNET_MSG))
                        startWebSocket()
                    else {
                        updateNetworkStatus(false)
                        onDestroy()
                    }
                }
            })
        } else {
            Log.e(TAG, "startWebSocket: Already started")
        }

    }

    private fun updateNetworkStatus(isAvailable: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.NETWORK_STATUS,
                isAvailable
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "websocket onDestroy called")
        stopWebSocket()
    }

    private fun stopWebSocket() {
        Log.e(TAG, "stopWebSocket called")
        isServiceStarted = false
        webSocket?.cancel()
        webSocket = null
        client.dispatcher.executorService.shutdown()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        private const val TAG = "LoggingService"

        const val NO_INTERNET_MSG =
            "Unable to resolve host \"mblog.moviebeam.com\": No address associated with hostname"

        private var webSocket: WebSocket? = null
        private val sdf = SimpleDateFormat("EEE. MMM d, yyyy hh:mm:ss a", Locale.ENGLISH)
        private var formattedDate = sdf.format(Date())
        var isServiceStarted = false

        private var accountId: String = ""
        private var stbRoomNo: String = ""
        private var ua: String = ""
        private var ipAddress = "0.0.0.0"

        val CHECK = "C"
        val SCREEN_SWITCHING = "N"
        val CHANNEL_TUNING = "CN"
        val ERROR = "E"
        val RENTAL = "R"
        val HOME_VIDEO = "U"
        val SIGNAL = "S"
        val INFO = "I"

        private fun getPriority(type: String): String {
            return when (type) {
                CHECK, SCREEN_SWITCHING, CHANNEL_TUNING -> "N"
                ERROR, RENTAL -> "C"
                HOME_VIDEO, SIGNAL -> "H"
                INFO -> "L"
                else -> "N"
            }
        }

        fun sendMessageToWebSocket(message: String, type: String) {
            val hid = if (accountId.isNotEmpty()) accountId.toInteger() else 0
            if (webSocket != null && isServiceStarted) {
                formattedDate = sdf.format(Date())
                val msgDto = LogDTO(
                    T = type,
                    P = getPriority(type),
                    UA = ua,
                    HID = hid,
                    ROOMNO = stbRoomNo,
                    IP = ipAddress,
                    TSP = formattedDate,
                    Panel = getCurrentPanelNumber().toInteger(),
                    M = message
                )

                val customJson =
                    """{"T":"I","P":"${msgDto.P}","UA":"${msgDto.UA}","HID":${msgDto.HID},"ROOMNO":"${msgDto.ROOMNO}","IP":"${msgDto.IP}","TSP":"${msgDto.TSP}","Panel":${msgDto.Panel},"M":"${msgDto.M}"}"""

                val isSent = webSocket?.send(customJson)

                Log.d(TAG, "sendMessageToWebSocket: $isSent  $customJson")

                if (isSent == false) {
                    BaseActivity.currentActivity?.launchLogger()
                }
            } else {
                isServiceStarted = false
                webSocket?.cancel()
                webSocket = null
                Log.e(
                    TAG,
                    "Websocket3 Failed to send message: WebSocket is not initialized or sending failed"
                )
            }
        }
    }

}
