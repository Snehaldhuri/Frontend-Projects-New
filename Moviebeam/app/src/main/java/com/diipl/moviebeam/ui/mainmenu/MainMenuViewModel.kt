package com.diipl.moviebeam.ui.mainmenu

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    /* init {
         fetchAllApi(Constants.ACTIVATE, Constants.UA, Constants.MODE)
     }*/

    /*private fun fetchAllApi(cmd: String, ua: String, mode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(Resource.Loading())
            _themeLiveData.postValue(Resource.Loading())
            _accountSetupLiveData.postValue(Resource.Loading())

            val weatherApiResponse = async { movieBeamRepository.getWeatherData(ua) }
            val themeApiResponse = async { movieBeamRepository.getThemeDetails(ua) }
            val accountSetupApiResponse =
                async { movieBeamRepository.getAccountSetupDetails(cmd, ua, mode) }


            val result = awaitAll(
                weatherApiResponse,
                themeApiResponse,
                accountSetupApiResponse
            )

            if (result[0] == null) {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _weatherLiveData.postValue(Resource.Success(result[0] as WeatherResponse))
            }

            if (result[1] == null) {
                _themeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _themeLiveData.postValue(Resource.Success(result[1] as ThemeResponse))
            }

            if (result[2] == null) {
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _accountSetupLiveData.postValue(Resource.Success(result[2] as AccountSetupResponse))
            }
        }
    }*/

    /*------------------------------------------------------------------------------------------*/

    // Get Response From DataStore
    fun getThemeResponseData(dataStore: DataStore<ThemeResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _themeLiveData.postValue(Resource.Loading())

            dataStore.data.catch {
                _themeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))

            }.collect {
                _themeLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun getWeatherResponseData(dataStore: DataStore<WeatherResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _weatherLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun getAccountSetupResponseData(dataStore: DataStore<AccountSetupResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _accountSetupLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _accountSetupLiveData.postValue(Resource.Success(it))
            }
        }
    }

    /*----------------------------------------------------------------------------------------------*/

    // Set Response to DataStore

    /* fun setThemeResponseData(
         dataStore: DataStore<ThemeResponse>,
         data: ThemeResponse
     ) {

         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
                 currentPreferences.copy(
                     accountId = data.accountId,
                     fontCss = data.fontCss,
                     gradientColor = data.gradientColor,
                     id = data.id,
                     spotLightColor = data.spotLightColor,
                     themeBackgroundFileName = data.themeBackgroundFileName,
                     themeLogoFileNameCloud = data.themeLogoFileNameCloud,
                     themeBgFileName = data.themeBgFileName,
                     themeBgFileNameCloud = data.themeBgFileNameCloud,
                     themeCss = data.themeCss,
                     type = data.type,
                     version = data.version,
                     themeBackgroundFileNameCloud = data.themeBackgroundFileNameCloud,
                     themeLogoFileName = data.themeLogoFileName
                 )

             }
     fun getUAFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
         viewModelScope.launch {
             preferenceDataStoreHelper.getPreference(PreferenceDataStoreConstants.UA, "")
                 .collect {
                     _UALiveData.postValue(it)
                 }
         }
     }




     fun setWeatherResponseData(
         dataStore: DataStore<WeatherResponse>,
         data: WeatherResponse
     ) {

         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
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
     }



     fun setAccountSetupResponseData(
         dataStore: DataStore<AccountSetupResponse>,
         data: AccountSetupResponse
     ) {
         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
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
                     welcomeScreenVisible = data.welcomeScreenVisible
                 )

             }
         }
     }

     fun setHotelServicesResponseData(
         dataStore: DataStore<HotelServiceResponse>,
         data: HotelServiceResponse
     ) {

         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
                 currentPreferences.copy(
                     id = data.id,
                     servicesList = data.servicesList,
                     type = data.type,
                     version = data.version
                 )

             }
         }
     }

     fun setLocalAttractionResponseData(
         dataStore: DataStore<LocalAttractionResponse>,
         data: LocalAttractionResponse
     ) {

         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
                 currentPreferences.copy(
                     id = data.id,
                     servicesList = data.servicesList,
                     type = data.type,
                     version = data.version
                 )

             }
         }
     }

     fun setMoviesResponseData(
         dataStore: DataStore<MoviesResponse>,
         data: MoviesResponse
     ) {

         viewModelScope.launch(Dispatchers.IO) {
             dataStore.updateData { currentPreferences ->
                 currentPreferences.copy(
                     accountId = data.accountId,
                     adultDayPassPrice = data.adultDayPassPrice,
                     freeContentList = data.freeContentList,
                     freeGenreList = data.freeGenreList,
                     premiumContentList = data.premiumContentList,
                     premiumGenreList = data.premiumGenreList,
                     id = data.id,
                     type = data.type,
                     version = data.version
                 )

             }
         }
     }*/


    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}