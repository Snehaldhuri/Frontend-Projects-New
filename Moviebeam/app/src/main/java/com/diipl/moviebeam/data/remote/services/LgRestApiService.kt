package com.diipl.moviebeam.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LgRestApiService {

    @GET("content/weather")
    suspend fun getWeather(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/hotelServices")
    suspend fun getHotelServices(
        @Query("accountId") accountId: Int
    ): Response<String>

    @GET("content/localAttraction")
    suspend fun getLocalAttraction(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/releasesAmenity")
    suspend fun getMovies(
        @Query("UA") ua: String
    ): Response<String>

    @GET("hotel/theme")
    suspend fun getThemeDetails(
        @Query("UA") ua: String
    ): Response<String>

    @GET("hotel/datetime")
    suspend fun getDateTime(
        @Query("UA") ua: String
    ): Response<String>

}