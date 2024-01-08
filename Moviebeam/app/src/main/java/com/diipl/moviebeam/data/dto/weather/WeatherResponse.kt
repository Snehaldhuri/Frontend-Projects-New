package com.diipl.moviebeam.data.dto.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val accountId: Int = 0,
    val dewPoint: String = "",
    val durationMin: Double = 0.0,
    val high: String = "",
    val highForLingual: String = "",
    val humidity: String = "",
    val id: Int = 0,
    val location: String = "",
    val low: String = "",
    val lowForLingual: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val tempCondition: String = "",
    val tempConditionUrl: String = "",
    val tempConditionUrlCloud: String = "",
    val type: String = "",
    val visibility: String = "",
    val weatherProviderImage: String = "",
    val weatherProviderImageCloud: String = "",
    val windSpeed: String = ""
)