package com.diipl.moviebeam.worker

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
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.deleteHSFolder
import com.diipl.moviebeam.utils.deleteLAFolder
import com.diipl.moviebeam.utils.deleteThemeFolder
import com.diipl.moviebeam.utils.getGradientColor
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
    private val preferenceHandler: PreferenceHandler,
    private val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    private val hotelServiceDataStore: DataStore<HotelServiceResponse>,
    private val themeDataStore: DataStore<ThemeResponse>,
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
            Log.e(TAG, "doWork: $action")
            when (action) {
                ACTION_ALL -> {
                    updateThemeData()
                    updateLAData()
                    updateHSData()
                    Result.success()
                }

                ACTION_HS -> {
                    updateHSData()
                    Result.success()
                }
                ACTION_LA -> {
                    updateLAData()
                    Result.success()
                }
                ACTION_THEME -> {
                    updateThemeData()
                    Result.success()
                }
                else -> {
                    Log.e(TAG, "doWork: Unknown Work action: $action")
                    Result.failure()
                }
            }
//            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Failed with exception: ${e.message}", e)
            Result.failure()
        }

    }

    private suspend fun updateHSData() = coroutineScope {
        Log.e(TAG, "updateHSData: Downloading HS Images", )
        deleteHSFolder()

        val data = hotelServiceDataStore.data.first()

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
        Log.e(TAG, "updateHSData: Downloading HS Images Done", )
    }

    private suspend fun updateLAData() = coroutineScope {
        Log.e(TAG, "updateLAData: Downloading LA Images", )
        deleteLAFolder()

        val data = localAttractionDataStore.data.first()

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
        Log.e(TAG, "updateLAData: Downloading LA Images Done", )
    }

    private suspend fun updateThemeData() = coroutineScope {
        Log.e(TAG, "updateThemeData: Downloading Theme Images", )
        try {
            deleteThemeFolder()

            val data = themeDataStore.data.first()

            preferenceHandler.updateDatastoreVariables(
                gradientStartColor = data.gradientColor,
                gradientEndColor = data.spotLightColor
            )

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

                ThemeDetails.GRADIENT_COLOR_START = data.gradientColor
                ThemeDetails.GRADIENT_COLOR_END = data.spotLightColor
                ThemeDetails.GRADIENT = null
                ThemeDetails.GRADIENT = getGradientColor()
                ThemeDetails.BG_IMAGE = result[0]
                ThemeDetails.LOGO_IMAGE = result[1]
            } else {
                Log.e(TAG, "updateThemeData: Await results are null!")
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
