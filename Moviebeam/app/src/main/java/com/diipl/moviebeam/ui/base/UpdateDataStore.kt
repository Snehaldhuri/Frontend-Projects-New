package com.diipl.moviebeam.ui.base

import android.util.Log
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.Service
import com.diipl.moviebeam.data.dto.hotelservice.Services
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.data.dto.localattraction.LAServices
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.utils.deleteHSFolder
import com.diipl.moviebeam.utils.deleteLAFolder
import com.diipl.moviebeam.utils.saveHSImage
import com.diipl.moviebeam.utils.saveLAImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.internal.toImmutableList
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
        deleteHSFolder()
        val servicesList = mutableListOf<Services>()
        val resp = async(Dispatchers.IO) {
            data.servicesList.forEach { services ->
                val serv = mutableListOf<Service>()
                services.serviceList.forEach { service ->
                    val list = mutableListOf<String>()
                    val newList = mutableListOf<String>()

                    val deferredImages = service.serviceImageList.map { imageUrl ->
                        async(Dispatchers.IO) {
                            try {
                                saveHSImage(imageUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to save image: ${e.message}")
                                null
                            }
                        }
                    }

                    list.addAll(deferredImages.awaitAll().filterNotNull())

                    val newImages = service.serviceImageListNew.map { imageUrl ->
                        async(Dispatchers.IO) {
                            try {
                                saveHSImage(imageUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to save image: ${e.message}")
                                null
                            }
                        }
                    }

                    newList.addAll(newImages.awaitAll().filterNotNull())

                    val updatedService = service.copy(
                        serviceImageList = list.toImmutableList(),
                        serviceImageListNew = newList.toImmutableList()
                    )
                    serv.add(updatedService)
                }
                val updatedServices = services.copy(
                    serviceList = serv.toImmutableList()
                )
                servicesList.add(updatedServices)
            }

        }

        val res = awaitAll(resp)

        if (res.isNotEmpty()){
            try {
                hotelServiceDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        id = data.id,
                        servicesList = servicesList.toImmutableList(),
                        type = data.type,
                        version = data.version
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }

    }

    suspend fun updateLAData(data: LocalAttractionResponse) = coroutineScope {
        deleteLAFolder()
        val servicesList = mutableListOf<LAServices>()
        val resp = async(Dispatchers.IO) {
            data.servicesList.forEach {services ->
                val serv = mutableListOf<LAService>()
                services.serviceList.forEach { service ->
                    val list = mutableListOf<String>()
                    val newList = mutableListOf<String>()
                    val deferredImages = service.serviceImageList.map { imageUrl ->
                        async(Dispatchers.IO) {
                            try {
                                saveLAImage(imageUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to save image: ${e.message}")
                                null
                            }
                        }
                    }

                    list.addAll(deferredImages.awaitAll().filterNotNull())

                    val newImages = service.serviceImageListNew.map { imageUrl ->
                        async(Dispatchers.IO) {
                            try {
                                saveHSImage(imageUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to save image: ${e.message}")
                                null
                            }
                        }
                    }

                    newList.addAll(newImages.awaitAll().filterNotNull())

                    val updatedService = service.copy(
                        imagePathPoster = saveLAImage(service.imagePathPoster),
                        imagePathPosterNew = saveLAImage(service.imagePathPosterNew),
                        imagePathSushi = saveLAImage(service.imagePathSushi),
                        serviceImageListNew = newList.toImmutableList(),
                        serviceImageList = list.toImmutableList(),
                    )
                    serv.add(updatedService)
                }
                val updatedServices = services.copy(
                    serviceList = serv.toImmutableList()
                )
                servicesList.add(updatedServices)
            }
        }

        val res = awaitAll(resp)
        Log.e(TAG, "updateLAData: ${res.isNotEmpty()}")
        if (res.isNotEmpty()){
            try {
                localAttractionDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        id = data.id,
                        servicesList = servicesList.toImmutableList(),
                        type = data.type,
                        version = data.version
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }

    }
}