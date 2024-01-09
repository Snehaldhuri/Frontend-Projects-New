package com.diipl.moviebeam.service

import retrofit2.http.GET
import retrofit2.http.Query

interface LgRestApiService {

    @GET("content/weather")
    suspend fun getWeather(
        @Query("UA") ua: String
    ): String

    @GET("hotel/theme")
    suspend fun getTheme(
        @Query("UA") ua: String
    ): String

    @GET("hotel/datetime")
    suspend fun getDateTime(
        @Query("UA") ua: String
    ): String

    @GET("content/hotelServices")
    suspend fun getHotelServices(
        @Query("accountId") accountId: Int
    ): String

    //for device versions
    @GET("process/stbMaster")
    suspend fun getstbMaster(
        @Query("UA") UA: String,
        @Query("SRNO") SRNO: String,
        @Query("MACADDR") MACADDR: String,
        @Query("WIFI_MACADDR") WIFI_MACADDR: String,
        @Query("TYPE") TYPE: String
    ): String



}