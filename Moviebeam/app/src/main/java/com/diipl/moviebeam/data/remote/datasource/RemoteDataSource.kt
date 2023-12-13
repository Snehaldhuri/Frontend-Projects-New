package com.diipl.moviebeam.data.remote.datasource

import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.remote.services.ApiService
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.NetworkHandler
import com.diipl.moviebeam.utils.NetworkUtils
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val apiService: ApiService
) : NetworkHandler(networkUtils){

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        val result = safeAPiCall {
            apiService.getWeather(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, WeatherResponse::class)
    }

    suspend fun getHotelServiceInfo(accountId: Int): HotelServiceResponse? {
        val result = safeAPiCall {
            apiService.getHotelServices(accountId)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, HotelServiceResponse::class)
    }

    suspend fun getThemeDetails(ua: String): ThemeResponse? {
        val result = safeAPiCall {
            apiService.getThemeDetails(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, ThemeResponse::class)
    }

    suspend fun getDateTimeData(ua: String): DateTimeResponse? {
        val result = safeAPiCall {
            apiService.getDateTime(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, DateTimeResponse::class)
    }

}