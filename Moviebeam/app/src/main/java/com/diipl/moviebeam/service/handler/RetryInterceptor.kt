package com.diipl.moviebeam.service.handler

import android.content.Context
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.rebootDevice
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor(
    val context: Context,
    private val maxRetries: Int = INITIAL_RETRY,
    private val delayMs: Long = INITIAL_DELAY,
) : Interceptor {

    private val TAG = "RetryInterceptor"
    private val hardwareAPI by lazy { HardwareAPI(context) }
    private val preferenceHandler by lazy { PreferenceHandler(context) }

    companion object {
        const val INITIAL_RETRY = 3
        const val INITIAL_DELAY = 1000 * 10L
        const val FINAL_ERROR_MSG = "Error during 3 retries!"
        var IS_RESPONSE_OK = true

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        BaseActivity.currentActivity?.launchLogger()

        var attempt = 0
        val response = chain.proceed(chain.request())
        var exception: Exception? = null

        while (attempt < maxRetries) {
            try {
                IS_RESPONSE_OK = true

                if (response.code == 200) {
                    logE("RetryInterceptor: attempt count: $attempt, Response data --> ${response.request.url},  ${response.code},  ${response.isSuccessful},  ${response.message}")
                    return response
                } else {
                    throw Exception("Cancelled by error code: ${response.code}, url: ${response.request.url}")
                }
            } catch (e: Exception) {
                logE("intercept: attempt count: $attempt, URL: ${response.request.url},  Exception msg: ${e.localizedMessage}")
                exception = e
                IS_RESPONSE_OK = false
            }

            attempt++
            if (attempt <= maxRetries) {
                // Delay for the specified time before the next retry
                runBlocking { delay(delayMs) }
            }
        }

        if (attempt == maxRetries)
            handleRebootCmd()
        // Throw the last exception if all retries fail
        throw exception ?: Exception(FINAL_ERROR_MSG)
    }

    private fun handleRebootCmd() {
        logE("handleRebootCmd: $FINAL_ERROR_MSG")

        BaseActivity.currentActivity?.let {
            when (it.javaClass.simpleName) {
                RegisterSTBActivity::class.java.simpleName -> {
                    preferenceHandler.updateDatastoreVariables(isStbAllocated = false)
                    it.rebootDevice()
                }

                STBDetailsActivity::class.java.simpleName -> {
                    preferenceHandler.updateDatastoreVariables(isAllDataFetched = IS_RESPONSE_OK)
                    it.rebootDevice()
                }

                else -> {}
            }
        }

    }


}