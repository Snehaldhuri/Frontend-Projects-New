package com.diipl.moviebeam.data.remote.services

import retrofit2.Call
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
        @Query("accountId") accountId: String
    ): Response<String>

    @GET("content/localAttraction")
    suspend fun getLocalAttraction(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/releasesAmenity")
    suspend fun getMovies(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/showTimeReleases")
    suspend fun getShowtime(
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

    @GET("content/newsHeader")
    suspend fun getNewsHeader(
        @Query("UA") ua: String,
        @Query("languageId") languageId: Int
    ): Response<String>

    @GET("content/news")
    suspend fun getNewsDetails(
        @Query("newsId") newsId: Int
    ): Response<String>

    @GET("content/news")
    suspend fun getNewsDetails(
        @Query("UA") ua: String,
        @Query("newsId") newsId: Int
    ): Response<String>

    @GET("process/stbMaster")
    suspend fun processStbMaster(
        @Query("UA") ua: String,
        @Query("SRNO") srNo: String,
        @Query("MACADDR") macAddress: String,
        @Query("WIFI_MACADDR") wifiMacAddress: String,
        @Query("TYPE") stbType: String
    ): Response<String>

    @GET("StbServlet")
    suspend fun kaping(
        @Query("Q") Q: String,
        @Query("UA") UA: String,
        @Query("DRID") DRID: String,
        @Query("SW") SW: String,
        @Query("CLISTVER") CLISTVER: String,
        @Query("DV") DV: String,
        @Query("TNS") TNS: String,
        @Query("EVENT") EVENT: String,
        @Query("SID") SID: String,
        @Query("RBTY") RBTY: String,
        @Query("MODE") MODE: String,
        @Query("LAVER") LAVER: String,
        @Query("HSVER") HSVER: String,
        @Query("THMVER") THMVER: String,
        @Query("CMDRES") CMDRES: String,
        @Query("CALLBACKFLG") CALLBACKFLG: String,
        @Query("INRMVER") INRMVER: String,
        @Query("LAUVER") LAUVER: String
    ): Response<String>


    @GET("content/serviceRequest")
    suspend fun getLaundry(
        @Query("UA") UA: String,
        @Query("serviceId") serviceId: String
    ): Response<String>

    @GET("process/guestFeedback")
    suspend fun sendGuestFeedback(
        @Query("UA") ua: String,
        @Query("FEEDBACK") feedback: String,
        @Query("STB_TIME") stbTime: String
    ): Response<String>

    @GET("process/softwareDetails")
    suspend fun getSoftwareUpdateDetails(
        @Query("stbTypeId") stbTypeId: Int,
        @Query("UA") ua: String
    ): Response<String>

    @GET("StbServlet")
    fun getKapingService(
        @Query("Q") Q: String,
        @Query("UA") UA: String,
        @Query("DRID") DRID: String,
        @Query("SW") SW: String,
        @Query("CLISTVER") CLISTVER: String,
        @Query("DV") DV: String,
        @Query("TNS") TNS: String,
        @Query("EVENT") EVENT: String,
        @Query("SID") SID: String,
        @Query("RBTY") RBTY: String,
        @Query("MODE") MODE: String,
        @Query("LAVER") LAVER: String,
        @Query("HSVER") HSVER: String,
        @Query("THMVER") THMVER: String,
        @Query("CMDRES") CMDRES: String,
        @Query("CALLBACKFLG") CALLBACKFLG: String,
        @Query("INRMVER") INRMVER: String,
        @Query("LAUVER") LAUVER: String
    ): Call<String>

    @GET("content/lcnNew")
    suspend fun getChannelList(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/epgNew/v3")
    suspend fun getEPGDataFromServer(
        @Query("UA") ua: String
    ): Response<String>

    @GET("content/tvTickerInfo")
    suspend fun getTvTickerMessages(
        @Query("UA") ua: String
    ): Response<String>

    @GET("hotel/message")
    suspend fun getGuestMessages(
        @Query("UA") ua: String,
        @Query("guestSessionId") guestSessionId: String
    ): Response<String>

}