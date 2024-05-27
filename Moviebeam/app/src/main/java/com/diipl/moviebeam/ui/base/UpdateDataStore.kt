package com.diipl.moviebeam.ui.base

import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class UpdateDataStore @Inject constructor(
    val weatherDataStore: DataStore<WeatherResponse>,
    val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    val hotelServiceDataStore: DataStore<HotelServiceResponse>
) {

    private val TAG = "UpdateDataStore"

    suspend fun updateWeatherData(data: WeatherResponse) {
        weatherDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                accountId = data.accountId,
                dewPoint = data.dewPoint,
                durationMin = data.durationMin,
                high = data.high,
                highForLingual = data.highForLingual,
                humidity = data.humidity,
                id = data.id,
                location = data.location,
                low = data.low,
                lowForLingual = data.lowForLingual,
                sunrise = data.sunrise,
                sunset = data.sunset,
                tempCondition = data.tempCondition,
                tempConditionUrl = data.tempConditionUrl,
                tempConditionUrlCloud = data.tempConditionUrlCloud,
                type = data.type,
                visibility = data.visibility,
                weatherProviderImage = data.weatherProviderImage,
                weatherProviderImageCloud = data.weatherProviderImageCloud,
                windSpeed = data.windSpeed
            )

        }
    }

    suspend fun updateHSData(data: HotelServiceResponse) = coroutineScope {
        hotelServiceDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }
    }

    suspend fun updateLAData(data: LocalAttractionResponse) = coroutineScope {
        localAttractionDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }
    }
}