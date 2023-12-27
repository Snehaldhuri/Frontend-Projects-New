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

    @GET("hotel/theme")
    suspend fun getThemeDetails(
        @Query("UA") ua: String
    ): Response<String>

    @GET("hotel/datetime")
    suspend fun getDateTime(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/newsHeader")
    suspend fun getNewsHeader(
        @Query("UA") ua: String,
        @Query("languageId") languageId: Int
    ): Response<String>

    @GET("content/news")
    suspend fun getNewsDetails(
        @Query("newsId") newsId: Int
    ): Response<String>

}