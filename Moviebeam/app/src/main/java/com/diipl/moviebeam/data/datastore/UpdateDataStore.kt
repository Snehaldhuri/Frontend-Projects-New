package com.diipl.moviebeam.data.datastore

import androidx.datastore.core.DataStore
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.replaceDegreeSymbol
import com.diipl.moviebeam.worker.UpdateDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateDataStore @Inject constructor(
    private val weatherDataStore: DataStore<WeatherResponse>,
    private val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    private val themeDataStore: DataStore<ThemeResponse>,
    private val accountDataStore: DataStore<AccountSetupResponse>,
    private val preferenceHandler: PreferenceHandler,
    private val hotelServiceDataStore: DataStore<HotelServiceResponse>,
    private val channelListDataStore: DataStore<ChannelListResponse>,
    private val tickerDataStore: DataStore<TickerResponse>,
    private val moviesDataStore: DataStore<MoviesResponse>,
    private val showTimeDataStore: DataStore<ShowTimeResponse>,
    private val guestDetailsDatastore: DataStore<CmdDataDto>,
    private val guestMessageDataStore: DataStore<MessageResponse>,
    private val workManager: WorkManager,
) {

    suspend fun updateAccountData(data: AccountSetupResponse) {
        preferenceHandler.updateAccountData(data)
        accountDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                accountId = data.accountId,
                address = data.address,
                adultContent = data.adultContent,
                airportCode = data.airportCode,
                amenityPasscodeStatus = data.amenityPasscodeStatus,
                analogService = data.analogService,
                appsList = data.appsList,
                appsVisible = data.appsVisible,
                appsWorld = data.appsWorld,
                blankChannelBroadcastType = data.blankChannelBroadcastType,
                blankChannelLcn = data.blankChannelLcn,
                blankChannelMajor = data.blankChannelMajor,
                blankChannelMinor = data.blankChannelMinor,
                blankTvChannelBroadcastType = data.blankTvChannelBroadcastType,
                buttonsList = data.buttonsList,
                castingVisible = data.castingVisible,
                cityCode = data.cityCode,
                clipType = data.clipType,
                cloudEnabled = data.cloudEnabled,
                companyName = data.companyName,
                conciergeList = data.conciergeList,
                contactNo = data.contactNo,
                contentDetailFlag = data.contentDetailFlag,
                customerCareNo = data.customerCareNo,
                defaultLanguage = data.defaultLanguage,
                email = data.email,
                enableInroomDining = data.enableInroomDining,
                enableNdvr = data.enableNdvr,
                enableShowtime = data.enableShowtime,
                enableShowtimeStatus = data.enableShowtimeStatus,
                enableStbLogging = data.enableStbLogging,
                enableWebRemote = data.enableWebRemote,
                epgCdnUrl = data.epgCdnUrl,
                epgDuration = data.epgDuration,
                feedBackVisible = data.feedBackVisible,
                fetchServerIp = data.fetchServerIp,
                fetchServerPort = data.fetchServerPort,
                flightStatusDisclaimer = data.flightStatusDisclaimer,
                gatewayIpAddress = data.gatewayIpAddress,
                gsButtonsList = data.gsButtonsList,
                guestServicesVisible = data.guestServicesVisible,
                hotelChannelList = data.hotelChannelList,
                hotelDetailFlag = data.hotelDetailFlag,
                hotelDisplayName = data.hotelDisplayName,
                hotelInfo = data.hotelInfo,
                hotelModel = data.hotelModel,
                hotelPlan = data.hotelPlan,
                hotelPlanId = data.hotelPlanId,
                hotelServicesVisible = data.hotelServicesVisible,
                httpStreamingCdnUrl = data.httpStreamingCdnUrl,
                httpStreamingHotelvideoUrl = data.httpStreamingHotelvideoUrl,
                id = data.id,
                itemMenuList = data.itemMenuList,
                languageWiseHotelInfoList = data.languageWiseHotelInfoList,
                languagesList = data.languagesList,
                liveTVVisible = data.liveTVVisible,
                mainCastingHdmi = data.mainCastingHdmi,
                mbloggerCloudUrl = data.mbloggerCloudUrl,
                multiLingual = data.multiLingual,
                ndvrIp = data.ndvrIp,
                ndvrPort = data.ndvrPort,
                pairDeviceVisible = data.pairDeviceVisible,
                prgGuideVisible = data.prgGuideVisible,
                proxyIp = data.proxyIp,
                proxyPort = data.proxyPort,
                proxyVodIp = data.proxyVodIp,
                proxyVodPort = data.proxyVodPort,
                roomNo = data.roomNo,
                samsungBroadcastType = data.samsungBroadcastType,
                samsungChannelType = data.samsungChannelType,
                samsungMainmenuAccessText = data.samsungMainmenuAccessText,
                samsungModulationType = data.samsungModulationType,
                selectedAppsList = data.selectedAppsList,
                showToiletryPriceColumn = data.showToiletryPriceColumn,
                showVegNonVegImg = data.showVegNonVegImg,
                showtimesVisible = data.showtimesVisible,
                softwareDownloadIp = data.softwareDownloadIp,
                softwareDownloadPort = data.softwareDownloadPort,
                stbCastingImageUrl = data.stbCastingImageUrl,
                stbZoneId = data.stbZoneId,
                streamingChannelBroadcastType = data.streamingChannelBroadcastType,
                streamingIp = data.streamingIp,
                streamingPort = data.streamingPort,
                streamingType = data.streamingType,
                streamingTypeId = data.streamingTypeId,
                temperatureUnit = data.temperatureUnit,
                timeZoneCode = data.timeZoneCode,
                tvBroadcastType = data.tvBroadcastType,
                tvSerialControl = data.tvSerialControl,
                tvVodTuningType = data.tvVodTuningType,
                type = data.type,
                vodMgrIp = data.vodMgrIp,
                vodMgrPort = data.vodMgrPort,
                vodVisible = data.vodVisible,
                welcomeScreenVisible = data.welcomeScreenVisible,
                stbCastingPageUrl = data.stbCastingPageUrl,
                isEnablePatchWall = data.isEnablePatchWall,
                isNetflixCheckin = data.isNetflixCheckin,
                isPlayBoxTv = data.isPlayBoxTv,
                enableClearCredentialsPatchwall = data.enableClearCredentialsPatchwall,
                mdmServerUrl = data.mdmServerUrl,
                mdmServerUsername = data.mdmServerUsername,
                mdmServerPassword = data.mdmServerPassword
            )
        }
    }

    suspend fun updateWeatherData(data: WeatherResponse) {
        weatherDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                accountId = data.accountId,
                dewPoint = data.dewPoint.replaceDegreeSymbol(),
                durationMin = data.durationMin,
                high = data.high.replaceDegreeSymbol().replace("high ", ""),
                highForLingual = data.highForLingual,
                humidity = data.humidity,
                id = data.id,
                location = data.location,
                low = data.low.replaceDegreeSymbol().replace("low ", ""),
                lowForLingual = data.lowForLingual,
                sunrise = data.sunrise,
                sunset = data.sunset,
                tempCondition = data.tempCondition.replaceDegreeSymbol(),
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
        if (preferenceHandler.hsVersion != data.version) {
            if (data.servicesList.isNotEmpty()) {
                updateHSParams(data)
            } else {
                preferenceHandler.updateDatastoreVariables(isHSEmpty = true)
                logE("${MovieBeamRepository.HS} list is empty.")
            }
        } else {
            preferenceHandler.updateDatastoreVariables(isHSEmpty = false)
            logE("Same ${MovieBeamRepository.HS} version found.")
        }

    }

    suspend fun updateHSParams(data: HotelServiceResponse) = coroutineScope {
        preferenceHandler.updateDatastoreVariables(hsVersion = data.version, isHSEmpty = false)
        hotelServiceDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }
        startUpdateDataWorker(UpdateDataWorker.ACTION_HS)
    }

    suspend fun updateLAData(data: LocalAttractionResponse) = coroutineScope {
        if (preferenceHandler.laVersion != data.version) {
            if (data.servicesList.isNotEmpty()) {
                updateLAParams(data)
            } else {
                preferenceHandler.updateDatastoreVariables(isLAEmpty = true)
                logE("${MovieBeamRepository.LA} list is empty.")
            }
        } else {
            preferenceHandler.updateDatastoreVariables(isLAEmpty = false)
            logE("Same ${MovieBeamRepository.LA} version found.")
        }
    }

    suspend fun updateLAParams(data: LocalAttractionResponse) = coroutineScope {
        preferenceHandler.updateDatastoreVariables(
            laVersion = data.version,
            isLAEmpty = false
        )
        localAttractionDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                servicesList = data.servicesList,
                type = data.type,
                version = data.version
            )
        }
        startUpdateDataWorker(UpdateDataWorker.ACTION_LA)
    }

    suspend fun updateThemeData(data: ThemeResponse) = coroutineScope {
        if (data.version != preferenceHandler.themeVersion) {
            if (data.themeBackgroundFileName.isNotEmptyOrNull()) {
                updateThemeParams(data)
            } else {
                preferenceHandler.updateDatastoreVariables(isThemeEmpty = true)
                logE("${MovieBeamRepository.THEME} data is empty.")
            }
        } else {
            preferenceHandler.updateDatastoreVariables(isThemeEmpty = false)
            logE("Same ${MovieBeamRepository.THEME} version found.")
        }

    }

    suspend fun updateThemeParams(data: ThemeResponse) = coroutineScope {
        preferenceHandler.updateDatastoreVariables(
            themeVersion = data.version,
            isThemeEmpty = false
        )
        themeDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                accountId = data.accountId,
                spotLightColor = data.spotLightColor,
                fontCss = data.fontCss,
                gradientColor = data.gradientColor,
                themeBackgroundFileName = data.themeBackgroundFileName,
                themeLogoFileName = data.themeLogoFileName,
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
        startUpdateDataWorker(UpdateDataWorker.ACTION_THEME)
    }

    fun updateChannelListData(data: ChannelListResponse) = CoroutineScope(Dispatchers.IO).launch {
        channelListDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                channelLcnList = data.channelLcnList,
                type = data.type,
            )
        }
    }

    fun updateMoviesData(data: MoviesResponse) = CoroutineScope(Dispatchers.IO).launch {
        if (data.version != preferenceHandler.moviesVersion) {
            if (data.freeContentList.isNotEmpty() || data.premiumContentList.isNotEmpty()) {
                preferenceHandler.updateDatastoreVariables(
                    moviesCount = data.freeContentList.size.plus(data.premiumContentList.size),
                    cListVersion = data.version,
                    moviesVersion = data.version,
                    isMoviesEmpty = false
                )
                moviesDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        accountId = data.accountId,
                        adultDayPassPrice = data.adultDayPassPrice,
                        freeGenreList = data.freeGenreList,
                        freeContentList = data.freeContentList,
                        premiumContentList = data.premiumContentList,
                        premiumGenreList = data.premiumGenreList,
                        id = data.id,
                        type = data.type,
                        version = data.version
                    )
                }
            } else {
                preferenceHandler.updateDatastoreVariables(isMoviesEmpty = true)
                logE("${MovieBeamRepository.MOVIES} data is empty.")
            }
        } else {
            preferenceHandler.updateDatastoreVariables(isMoviesEmpty = false)
            logE("Same ${MovieBeamRepository.MOVIES} version found.")
        }
    }

    fun updateTickerData(data: TickerResponse) = CoroutineScope(Dispatchers.IO).launch {
        tickerDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                tvTickerList = data.tvTickerList,
                type = data.type,
                version = data.version
            )
        }
    }

    fun updateShowTimeData(data: ShowTimeResponse) = CoroutineScope(Dispatchers.IO).launch {
        if (data.version != preferenceHandler.showTimeVersion) {
            if (data.shoGenreList.isNotEmpty()) {
                preferenceHandler.updateDatastoreVariables(
                    showsCount = data.shoContentList.size,
                    showTimeVersion = data.version
                )
                showTimeDataStore.updateData { currentPreferences ->
                    currentPreferences.copy(
                        accountId = data.accountId,
                        id = data.id,
                        shoContentList = data.shoContentList,
                        shoGenreList = data.shoGenreList,
                        type = data.type,
                        version = data.version
                    )
                }
            } else {
                preferenceHandler.updateDatastoreVariables(isShowtimeEmpty = true)
                logE("${MovieBeamRepository.SHOW_TIME} data is empty.")
            }
        } else {
            preferenceHandler.updateDatastoreVariables(isShowtimeEmpty = false)
            logE("Same ${MovieBeamRepository.SHOW_TIME} version found.")
        }
    }

    fun updateGuestData(data: CmdDataDto) = CoroutineScope(Dispatchers.IO).launch {
        guestDetailsDatastore.updateData { currentPreferences ->
            currentPreferences.copy(
                sessionId = data.sessionId,
                parentSessionId = data.parentSessionId,
                adultContentDisabled = data.adultContentDisabled,
                message = data.message,
                guestName = data.guestName,
                adultLocked = data.adultLocked,
                passcode = data.passcode
            )
        }
    }

    fun updateGuestMessageData(data: MessageResponse) = CoroutineScope(Dispatchers.IO).launch {
        guestMessageDataStore.updateData { currentPreferences ->
            currentPreferences.copy(
                id = data.id,
                messagesList = data.messagesList,
                type = data.type,
            )
        }
    }

    private fun startUpdateDataWorker(action: String) {
        val inputData = Data.Builder()
            .putString(UpdateDataWorker.ACTION, action)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdateDataWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(action, ExistingWorkPolicy.APPEND, request)
    }

}