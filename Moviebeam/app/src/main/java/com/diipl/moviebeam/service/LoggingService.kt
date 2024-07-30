package com.diipl.moviebeam.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.diipl.moviebeam.data.dto.logs.LogDTO
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.toInteger
import com.diipl.moviebeam.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoggingService : Service() {

    //Variables from datastore
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    private lateinit var client: OkHttpClient
    private val binder = LoggingServiceBinder()

    inner class LoggingServiceBinder : Binder() {
        fun getService(): LoggingService {
            return this@LoggingService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        this.initializeDatastoreParams()
        startWebSocket()
        return START_STICKY
    }

    fun startWebSocket() {
        if (!isServiceStarted) {

            client = OkHttpClient.Builder()
                .build()

            //TODO change url for release
            val request = Request.Builder()
                .url("ws://mblog.moviebeam.com:20000")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isServiceStarted = true
                    Log.e(TAG, "WebSocket connection opened")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.e(TAG, "WebSocket Received message: $text")

                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    isServiceStarted = false
                    Log.e(TAG, "WebSocket connection closed")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    isServiceStarted = false
                    Log.e(TAG, "WebSocket connection failure: ${t.message}")
                }
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "websocket onDestroy called")
    }

    private fun stopWebSocket() {
        webSocket?.cancel()
        webSocket = null
    }

    companion object {
        private const val TAG = "LoggingService"
        private var webSocket: WebSocket? = null
        private val sdf = SimpleDateFormat("EEE. MMM d, yyyy hh:mm:ss a", Locale.ENGLISH)
        private var formattedDate = sdf.format(Date())
        private var isServiceStarted = false

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
            if (webSocket != null) {
                formattedDate = sdf.format(Date())
                val msgDto = LogDTO(
                    T = type,
                    P = getPriority(type),
                    UA = ua,
                    HID = accountId.toInteger(),
                    ROOMNO = stbRoomNo,
                    IP = ipAddress,
                    TSP = formattedDate,
                    Panel = getCurrentPanelNumber().toInteger(),
                    M = message
                )
                val isSent = webSocket?.send(msgDto.toJson())
//                    webSocket?.send("{\"UA\":\"${Constants.UA}\",\"HID\":\"${Constants.ACCOUNT_ID}\",\"TSP\":\"$formattedDate\",\"Msg\":\"$message\",\"Panel\":\"$panel\"}")
                Log.e(TAG, "sendMessageToWebSocket: $isSent  ${webSocket!!.queueSize()}")
                if (isSent == false) {
                    BaseActivity.currentActivity?.launchLogger()
                }
            } else {
                Log.e(
                    TAG,
                    "Websocket3 Failed to send message: WebSocket is not initialized or sending failed"
                )
            }
        }
    }

    private fun initializeDatastoreParams() {
        CoroutineScope(Dispatchers.Default).launch {
            accountId = getAccountId()
            stbRoomNo = getStbRoomNo()
            ua = getUa()
            ipAddress = getIpAddress()
        }
    }

    private suspend fun getAccountId(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
            ""
        )
    }

    private suspend fun getStbRoomNo(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
            ""
        )
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    private suspend fun getIpAddress(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.IP_ADDRESS_KEY,
            "0.0.0.0"
        )
    }

}
