package com.diipl.moviebeam.data.remote.datasource

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

}