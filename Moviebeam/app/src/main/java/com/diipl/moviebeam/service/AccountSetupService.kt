package com.diipl.moviebeam.service

import retrofit2.http.GET
import retrofit2.http.Query

interface AccountSetupService {

    @GET("HotelInfoServlet")
    suspend fun getAccountSetupDetails(
        @Query("Q") cmd: String,
        @Query("UA") ua:String,
        @Query("MODE") mode: String
    ):String

}