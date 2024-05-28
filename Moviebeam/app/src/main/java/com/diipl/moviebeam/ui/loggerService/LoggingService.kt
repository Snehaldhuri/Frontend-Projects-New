package com.diipl.moviebeam.ui.loggerService

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.diipl.moviebeam.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoggingService : Service() {

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
        startWebSocket()
        return START_STICKY
    }


    fun startWebSocket() {
        client = OkHttpClient.Builder()
            .build()

        //TODO change url for release
        val request = Request.Builder()
            .url("ws://mblog.moviebeam.com:20000")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e(TAG, "WebSocket connection opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e(TAG, "WebSocket Received message: $text")

            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.e(TAG, "WebSocket connection closed")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket connection failure: ${t.message}")
            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"websocket onDestroy called")
    }

    private fun stopWebSocket() {
        webSocket?.cancel()
        webSocket = null
    }

    companion object {
        private const val TAG = "LoggingService"
        private var webSocket: WebSocket? = null
        private val sdf = SimpleDateFormat("EEE. MMM d, yyyy hh:mm:ss a", Locale.ENGLISH)
        private val formattedDate = sdf.format(Date())

        fun sendMessageToWebSocket(message: String, panel:String) {
            if (webSocket != null){
                val isSent = webSocket?.send("{\"UA\":\"${Constants.UA}\",\"HID\":\"${Constants.ACCOUNT_ID}\",\"TSP\":\"${formattedDate}\",\"Msg\":\"$message\",\"Panel\":\"$panel\"}")
                Log.e(TAG, "sendMessageToWebSocket: $isSent  ${webSocket!!.queueSize()}")
            }
            else {
                Log.e(TAG, "Websocket3 Failed to send message: WebSocket is not initialized or sending failed")
            }
        }
    }
}
