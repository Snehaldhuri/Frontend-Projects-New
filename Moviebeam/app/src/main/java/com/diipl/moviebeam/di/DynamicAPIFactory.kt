package com.diipl.moviebeam.di

import com.diipl.moviebeam.data.remote.services.MoviesAPIService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.DYNAMIC_RETRO
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DynamicAPIFactory @Inject constructor(
    @Named(DYNAMIC_RETRO) private val retrofitBuilder: Retrofit.Builder,
    private val okHttpClient: OkHttpClient
) {

    fun createApiService(baseUrl: String): MoviesAPIService {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(MoviesAPIService::class.java)
    }
}

