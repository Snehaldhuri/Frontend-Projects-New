package com.diipl.moviebeam.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("content/weather")
    suspend fun getWeather(
        @Query("UA") ua:String
    ):Response<String>
}