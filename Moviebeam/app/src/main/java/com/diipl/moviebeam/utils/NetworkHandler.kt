package com.diipl.moviebeam.utils

import com.diipl.moviebeam.data.Resource
import retrofit2.Response
import javax.inject.Inject

open class NetworkHandler @Inject constructor(
    private val networkUtil: NetworkUtils
) {
    suspend fun <T> safeAPiCall(api: suspend () -> Response<T>): Resource<T> {
        try {
            val result = api()
            if (result.isSuccessful) {
                val body = result.body()
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

