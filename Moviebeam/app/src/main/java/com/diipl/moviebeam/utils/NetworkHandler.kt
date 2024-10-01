package com.diipl.moviebeam.utils

import android.util.Log
import com.diipl.moviebeam.data.Resource
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import retrofit2.Response
import javax.inject.Inject

open class NetworkHandler @Inject constructor(
    private val networkUtil: NetworkUtils
) {
    suspend inline fun <reified T> safeAPiCall(api: suspend () -> Response<T>): Resource<T> {
        try {
            val result = api()
            if (result.isSuccessful) {
                val body = result.body()
                body.runCatching {  }
                return Resource.Success(body)
            }
            logE(result.message())
            return Resource.DataError(msg = result.message(), code = result.code())
        } catch (e: Exception) {
            e.message?.let { logE(it) }
            return Resource.DataError(msg = e.message)
        }
    }
}

