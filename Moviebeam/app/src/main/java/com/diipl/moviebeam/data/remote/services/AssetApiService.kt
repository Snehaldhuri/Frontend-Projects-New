package com.diipl.moviebeam.data.remote.services

import com.diipl.moviebeam.data.dto.sysInfo.SysInfoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AssetApiService {

    @GET("FlightStatusServlet")
    suspend fun getFlightStatus(
        @Query("Q") cmd: String,
        @Query("UA") ua: String,
        @Query("CALLTYPE") callType: String,
        @Query("APCODE") apCode: String,
        @Query("MODE") mode: String
    ): Response<String>

    @POST("SysInfoServlet")
    suspend fun sendSysInfo(
        @Query("UA") ua: String,
        @Body body: SysInfoDTO
    ): Response<String>

}