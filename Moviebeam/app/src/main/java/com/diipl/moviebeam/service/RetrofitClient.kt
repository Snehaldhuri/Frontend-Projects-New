package com.diipl.moviebeam.service

import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private const val BASE_URL_LG_REST = "https://stb.moviebeam.com:1930/LG/rest/"
    private const val BASE_URL_ACCOUNT_SETUP = "https://stb.moviebeam.com:1926/"

    fun createLgRestService(): LgRestApiService{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_LG_REST)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(LgRestApiService::class.java)
    }

    fun createAccountSetupService(): AccountSetupApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_ACCOUNT_SETUP)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(AccountSetupApiService::class.java)
    }

}