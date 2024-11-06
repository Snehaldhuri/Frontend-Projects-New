package com.diipl.moviebeam.service.handler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(val context: Context, private val maxRetries: Int = INITIAL_RETRY, private val delayMs: Long = INITIAL_DELAY) : Interceptor {

    private val TAG = "RetryInterceptor"
    private val hardwareAPI by lazy { HardwareAPI(context) }

    companion object {
        const val INITIAL_RETRY = 3
        const val INITIAL_DELAY = 1000*10L
        const val FINAL_ERROR_MSG = "Error during 3 retries!"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response
        var exception: IOException? = null


        while (attempt < maxRetries) {
            try {
                Log.e(TAG, "intercept: attempt -> $attempt")
                response = chain.proceed(chain.request())

                Log.e(TAG, "intercept: $attempt,--> ${response.request.url},  ${response.code},  ${response.isSuccessful},  ${response.message}")

                if (response.isSuccessful) return response
            } catch (e: IOException) {
                Log.e(TAG, "intercept: ${e.localizedMessage}")
                exception = e
            }

            attempt++
            if (attempt < maxRetries) {
                // Delay for the specified time before the next retry
                runBlocking { delay(delayMs) }
            }
        }

        if (attempt == maxRetries)
            handleRebootCmd()
        // Throw the last exception if all retries fail
        throw exception ?: IOException(FINAL_ERROR_MSG)
    }

    private fun handleRebootCmd() {
        Log.e(TAG, "handleRebootCmd: $FINAL_ERROR_MSG")

        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_STB -> {
                hardwareAPI.myService?.rebootDevice()
            }
            else -> {
                val intent = Intent()
                intent.component =
                    ComponentName(Constants.MDM_PACKAGE_NAME, KapingConstants.MDM_RESTART_ACTIVITY_NAME)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

}