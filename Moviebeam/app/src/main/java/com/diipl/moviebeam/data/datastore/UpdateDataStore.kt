package com.diipl.moviebeam.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.Service
import com.diipl.moviebeam.data.dto.hotelservice.Services
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.data.dto.localattraction.LAServices
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.deleteHSFolder
import com.diipl.moviebeam.utils.deleteLAFolder
import com.diipl.moviebeam.utils.deleteThemeFolder
import com.diipl.moviebeam.utils.saveHSImage
import com.diipl.moviebeam.utils.saveLAImage
import com.diipl.moviebeam.utils.saveThemeImage
import com.diipl.moviebeam.utils.saveThemeImageServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class UpdateDataStore @Inject constructor(
    val weatherDataStore: DataStore<WeatherResponse>,
    val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    val themeDataStore: DataStore<ThemeResponse>,
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
        /*hotelServiceDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }*/

        Log.e(TAG, "updateHSData: Downloading HS Images", )
        deleteHSFolder()
        val servicesList = mutableListOf<Services>()
        val resp = async(Dispatchers.IO) {
            data.servicesList.forEach { services ->
                val serv = mutableListOf<Service>()
                services.serviceList.forEach { service ->
                    val list = mutableListOf<String>()
                    val newList = mutableListOf<String>()

                    val deferredImages = service.serviceImageListCloud.map { imageUrl ->
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

                    val newImages = service.serviceImageListNewCloud.map { imageUrl ->
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
                        serviceImageListCloud = list.toImmutableList(),
                        serviceImageListNewCloud = newList.toImmutableList()
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
        Log.e(TAG, "updateHSData: ${res.isNotEmpty()}")
        if (res.isNotEmpty()) {
            try {
                hotelServiceDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        id = data.id,
                        servicesList = servicesList.toImmutableList(),
                        type = data.type,
                        version = data.version
                    )
                }
                Constants.isWorkDone++
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }
        Log.e(TAG, "updateHSData: Downloading HS Images Done", )
    }

    suspend fun updateLAData(data: LocalAttractionResponse) = coroutineScope {
        /*localAttractionDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }*/

        Log.e(TAG, "updateLAData: Downloading LA Images", )
        deleteLAFolder()
        val servicesList = mutableListOf<LAServices>()
        val resp = async(Dispatchers.IO) {
            data.servicesList.forEach { services ->
                val serv = mutableListOf<LAService>()
                services.serviceList.forEach { service ->
                    val list = mutableListOf<String>()
                    val newList = mutableListOf<String>()
                    val deferredImages = service.serviceImageListCloud.map { imageUrl ->
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

                    val newImages = service.serviceImageListNewCloud.map { imageUrl ->
                        async(Dispatchers.IO) {
                            try {
                                saveLAImage(imageUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to save image: ${e.message}")
                                null
                            }
                        }
                    }

                    newList.addAll(newImages.awaitAll().filterNotNull())

                    val imagePathPosterCloud = saveLAImage(service.imagePathPosterCloud)
                    val imagePathPosterNewCloud = saveLAImage(service.imagePathPosterNewCloud)
                    val imagePathSushiCloud = saveLAImage(service.imagePathSushiCloud)

                    val updatedService = service.copy(
                        imagePathPosterCloud = imagePathPosterCloud,
                        imagePathPosterNewCloud = imagePathPosterNewCloud,
                        imagePathSushiCloud = imagePathSushiCloud,
                        serviceImageListCloud = list.toImmutableList(),
                        serviceImageListNewCloud = newList.toImmutableList()
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
        if (res.isNotEmpty()) {
            try {
                localAttractionDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        id = data.id,
                        servicesList = servicesList.toImmutableList(),
                        type = data.type,
                        version = data.version
                    )
                }
                Constants.isWorkDone++
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }
        Log.e(TAG, "updateLAData: Downloading LA Images Done", )
    }

    suspend fun updateThemeData(data: ThemeResponse) = coroutineScope {
        Log.e(TAG, "updateThemeData: Downloading Theme Images", )
        try {
            deleteThemeFolder()
            val themeBackgroundFileName = async {
                getThemeFileName(
                    data.themeBackgroundFileNameCloud,
                    data.themeBackgroundFileName
                )
            }
            val themeLogoFileName = async {
                getThemeFileName(
                    data.themeLogoFileNameCloud,
                    data.themeLogoFileName
                )
            }

            val result = awaitAll(
                themeBackgroundFileName, themeLogoFileName
            )
            if (result[0] != null && result[1] != null) {
                themeDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        accountId = data.accountId,
                        spotLightColor = data.spotLightColor,
                        fontCss = data.fontCss,
                        gradientColor = data.gradientColor,
                        themeBackgroundFileName = result[0],
                        themeLogoFileName = result[1],
                        id = data.id,
                        themeBgFileName = data.themeBgFileName,
                        themeBackgroundFileNameCloud = data.themeBackgroundFileNameCloud,
                        themeCss = data.themeCss,
                        themeBgFileNameCloud = data.themeBgFileNameCloud,
                        themeLogoFileNameCloud = data.themeLogoFileNameCloud,
                        type = data.type,
                        version = data.version
                    )
                }
                Constants.isWorkDone++
            } else {

            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update DataStore: ${e.message}")
        }
        Log.e(TAG, "updateThemeData: Downloading Theme Images Done", )

    }

    private suspend fun getThemeFileName(cloudFileName: String?, localFileName: String?): String? {
        return if (cloudFileName != null) {
            saveThemeImage(cloudFileName)
        } else {
            saveThemeImageServer(localFileName)
        }
    }

}