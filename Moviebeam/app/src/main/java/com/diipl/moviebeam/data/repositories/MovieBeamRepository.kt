package com.diipl.moviebeam.data.repositories

import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.remote.datasource.RemoteDataSource
import javax.inject.Inject

class MovieBeamRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        return remoteDataSource.getWeatherData(ua)
    }

}