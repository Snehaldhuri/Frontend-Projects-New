package com.diipl.moviebeam.service.handler

import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

open class NetworkHandler @Inject constructor(
    private val networkUtil: NetworkUtils
) {
    inline fun <reified T> safeAPiCall(api: () -> Response<T>): Resource<T> {
        try {
            val result = api()
            runCatching { logD("API call status: ${result.code()},  URL: ${result.raw().request.url}, Message: ${result.body()}") }
            if (result.isSuccessful) {
                val body = result.body()
                return Resource.Success(body)
            }
            return Resource.DataError(msg = result.message(), code = result.code())
        } catch (e: Exception) {
            e.message?.let { logE(it) }
            return Resource.DataError(msg = e.message)
        }
    }
}

fun callNetflixAPI(roomNo: String, ua: String, accessType: Int) {
    val netflixDetails = JSONObject().apply {
        put("stbRoomNo", roomNo)
        put("ua", ua)
        put("accessType", accessType)
    }
    postRequest(netflixDetails.toString())
}

private fun postRequest(requestBody: String) {
    val url = "${Constants.BASE_URL_LG_REST}content/netflixAccess/enter?sessionId=${GuestDetails.SESSION_ID}"

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            logE("Network error: ${e.message}")
        }

        override fun onResponse(call: Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                logD("sessionId url success")
            } else {
                val responseBody = response.body?.string() ?: "No response body"
                val responseCode = response.code
                logE("sessionId Failed to call URL. Response code: $responseCode, Response body: $responseBody")
            }
        }
    })
}

