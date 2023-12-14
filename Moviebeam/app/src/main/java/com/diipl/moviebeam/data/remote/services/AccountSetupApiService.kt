package com.diipl.moviebeam.data.remote.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AccountSetupApiService {

    @GET("HotelInfoServlet")
    suspend fun getAccountSetupDetails(
        @Query("Q") cmd: String,
        @Query("UA") ua:String,
        @Query("MODE") mode: String
    ):Response<String>

}