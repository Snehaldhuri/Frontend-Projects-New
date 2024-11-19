package com.diipl.moviebeam.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MoviesAPIService {

    @GET("StbServlet")
    @JvmSuppressWildcards
    suspend fun getRentalMovieAccess(
        @QueryMap request: Map<String, Any>
    ): Response<String>

    @GET("StbServlet")
    @JvmSuppressWildcards
    suspend fun setRentalReversal(@QueryMap request: Map<String, Any>): Response<String>

    @GET("StbServlet")
    @JvmSuppressWildcards
    suspend fun buyPassRequest(@QueryMap queryMap: Map<String, Any>): Response<String>

    @GET("StbServlet")
    @JvmSuppressWildcards
    suspend fun getRentalNewMovieAccess(
        @Query("Q") q: String,
        @Query("UA") UA: String,
        @Query("RID") RID: Int,
        @Query("PID") PID: Int,
        @Query("PRICE") price: Double,
        @Query("TIMESTAMP") timeStamp: Long,
        @Query("SEEK") seek: Long,
        @Query("SID") sessionID: String,
        @Query("A") a: Int,
        @Query("RA") ra: Int,
        @Query("CTYPE") cType: String,
        @Query("SEEKTYPE") seekType: Long,
        @Query("RENTALID") rentalID: String,
        @Query("CONTENT_TYPE_ID") contentTypeID: Int,
        @Query("PRODUCT_TYPE") productType: Int,
        @Query("VODMID") vodMID: Long,
        @Query("AID") AID: Long,
        @Query("MODE") mode: String
    ): Response<String>

    @GET("VODManager/ContentRequestServlet")
    @JvmSuppressWildcards
    suspend fun getVodDataAccess(
//        @Query("VODMGRIP") vodMgrIp: String,
//        @Query("VODMGRPORT") vodMgrPort: String,
        @Query("VODMID") vodMid: Int = 1,
        @Query("TRANSID") transId: Long,
        @Query("A") streamingType: String,
        @Query("M") mode: String = "UNICAST",
        @Query("UA") userAgent: String,
        @Query("RID") rentalId: Int,
        @Query("PID") productId: Int,
        @Query("PRIORITY") priority: Int = 5,
        @Query("FILENAME") fileName: String,
        @Query("CTYPE") contentType: String,
        @Query("SEEK") seek: Long,
        @Query("JSON") json: Boolean = true
    ): Response<String>
}