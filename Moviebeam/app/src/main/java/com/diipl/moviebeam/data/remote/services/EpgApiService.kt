package com.diipl.moviebeam.data.remote.services

import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.sysInfo.SoftwareResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface EpgApiService {

    @GET
    suspend fun getEPGFromCloud(
        @Url url: String
    ): Response<EPGResponse>

    @GET("process/softwareDetail?stbTypeId=5")
    suspend fun getSoftwareUpdateDetails(): Response<SoftwareResponseDTO>

}