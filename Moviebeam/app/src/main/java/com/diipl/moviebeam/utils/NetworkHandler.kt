package com.diipl.moviebeam.utils

import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import retrofit2.Response
import javax.inject.Inject


open class NetworkHandler @Inject constructor(
    private val networkUtil: NetworkUtils
) {
    suspend fun <T> safeAPiCall(api: suspend () -> Response<T>): Resource<T> {
        try {
            if (networkUtil.isNetworkConnected) {
                val result = api()
                if (result.isSuccessful) {
                    val body = result.body()
                    return Resource.Success(body)
                }
                return Resource.DataError(msg = result.message(), code = result.code())
            } else {
                return Resource.DataError(msg = Constants.INTERNET_ERROR_MESSAGE)
            }
        } catch (e: Exception) {
            return Resource.DataError(msg = e.message)
        }
    }
}

