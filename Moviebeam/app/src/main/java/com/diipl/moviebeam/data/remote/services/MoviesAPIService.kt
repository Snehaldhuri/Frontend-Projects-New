package com.diipl.moviebeam.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface MoviesAPIService {

    @GET("StbServlet")
    @JvmSuppressWildcards
    suspend fun getRentalMovieAccess(
        @QueryMap request: Map<String, Any>
    ): Response<String>

/*
    @GET("StbServlet")
    suspend fun getRentalMovieAccess(
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
*/

}