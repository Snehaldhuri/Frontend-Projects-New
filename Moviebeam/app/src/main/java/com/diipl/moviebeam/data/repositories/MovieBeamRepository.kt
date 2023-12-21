package com.diipl.moviebeam.data.repositories

import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.remote.datasource.RemoteDataSource
import javax.inject.Inject

class MovieBeamRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        return remoteDataSource.getWeatherData(ua)
    }

    suspend fun getHotelServiceInfo(accountId: Int): HotelServiceResponse? {
        return remoteDataSource.getHotelServiceInfo(accountId)
    }

    suspend fun getThemeDetails(ua: String): ThemeResponse? {
        return remoteDataSource.getThemeDetails(ua)
    }

    suspend fun getDateTimeData(ua: String): DateTimeResponse? {
        return remoteDataSource.getDateTimeData(ua)
    }

    suspend fun getAccountSetupDetails(
        cmd: String,
        ua: String,
        mode: String
    ): AccountSetupResponse? {
        return remoteDataSource.getAccountSetupDetails(cmd, ua, mode)
    }

    suspend fun getFlightStatus(
        cmd: String,
        ua: String,
        callType: String,
        apCode: String,
        mode: String
    ): FlightStatusResponse? {
        return remoteDataSource.getFlightStatus(cmd, ua, callType, apCode, mode)
    }

    suspend fun getLocalAttractionInfo(ua: String): LocalAttractionResponse? {
        return remoteDataSource.getLocalAttractionInfo(ua)
    }

}