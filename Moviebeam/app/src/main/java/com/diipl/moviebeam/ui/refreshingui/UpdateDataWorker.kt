package com.diipl.moviebeam.ui.refreshingui

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.Service
import com.diipl.moviebeam.data.dto.hotelservice.Services
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.data.dto.localattraction.LAServices
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.deleteHSFolder
import com.diipl.moviebeam.utils.deleteLAFolder
import com.diipl.moviebeam.utils.deleteThemeFolder
import com.diipl.moviebeam.utils.saveHSImage
import com.diipl.moviebeam.utils.saveLAImage
import com.diipl.moviebeam.utils.saveThemeImage
import com.diipl.moviebeam.utils.saveThemeImageServer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import okhttp3.internal.toImmutableList

@HiltWorker
class UpdateDataWorker @AssistedInject constructor(
    private val moviesDataStore: DataStore<MoviesResponse>,
    private val showTimeDataStore: DataStore<ShowTimeResponse>,
    private val weatherDataStore: DataStore<WeatherResponse>,
    private val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    private val hotelServiceDataStore: DataStore<HotelServiceResponse>,
    private val themeDataStore: DataStore<ThemeResponse>,
    private val movieBeamRepository: MovieBeamRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val ACTION = "ACTION"
        const val ACTION_ALL = "ACTION_ALL"
        const val ACTION_HS = "ACTION_HS"
        const val ACTION_LA = "ACTION_LA"
        const val ACTION_THEME = "ACTION_THEME"
    }

    private val TAG = "UpdateDataWorker"

    override suspend fun doWork(): Result {
        Log.e(TAG, "doWork: START")
        return try {
            val action = inputData.getString(ACTION)
            when (action) {
                ACTION_ALL -> {
                    updateThemeData()
                    updateLAData(localAttractionDataStore.data.first())
                    updateHSData(hotelServiceDataStore.data.first())
                }

                ACTION_HS -> updateHSData(hotelServiceDataStore.data.first())
                ACTION_LA -> updateLAData(localAttractionDataStore.data.first())
                ACTION_THEME -> updateThemeData()
                else -> Log.e(TAG, "doWork: Unknown Work action: $action")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Failed with exception: ${e.message}", e)
            Result.failure()
        }

    }


    private suspend fun updateHSData(data: HotelServiceResponse) = coroutineScope {
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
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }

    }

    private suspend fun updateLAData(data: LocalAttractionResponse) = coroutineScope {
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
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update DataStore: ${e.message}")
            }
        }

    }

    private suspend fun updateThemeData() = coroutineScope {
        try {
            deleteThemeFolder()
            themeDataStore.updateData { currentPreferences ->
                val themeBackgroundFileName = getThemeFileName(
                    currentPreferences.themeBackgroundFileNameCloud,
                    currentPreferences.themeBackgroundFileName
                )
                val themeLogoFileName = getThemeFileName(
                    currentPreferences.themeLogoFileNameCloud,
                    currentPreferences.themeLogoFileName
                )

                currentPreferences.copy(
                    themeBackgroundFileName = themeBackgroundFileName,
                    themeLogoFileName = themeLogoFileName
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update DataStore: ${e.message}")
        }
    }

    private suspend fun getThemeFileName(cloudFileName: String?, localFileName: String?): String? {
        return if (cloudFileName != null) {
            saveThemeImage(cloudFileName)
        } else {
            saveThemeImageServer(localFileName)
        }
    }

}
