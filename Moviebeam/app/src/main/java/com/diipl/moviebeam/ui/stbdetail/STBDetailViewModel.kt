package com.diipl.moviebeam.ui.stbdetail

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.saveImage
import com.diipl.moviebeam.utils.saveImageCloud
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "STBDetailViewModel"

@HiltViewModel
class STBDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val movieBeamRepository: MovieBeamRepository
) :
    ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    private val _hotelServiceLiveData = MutableLiveData<Resource<HotelServiceResponse>>()
    val hotelServiceLiveData: LiveData<Resource<HotelServiceResponse>> get() = _hotelServiceLiveData

    private val _localAttractionLiveData = MutableLiveData<Resource<LocalAttractionResponse>>()
    val localAttractionLiveData: LiveData<Resource<LocalAttractionResponse>> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

    private val _tickerLiveData = MutableLiveData<Resource<TickerResponse>>()
    val tickerLiveData: LiveData<Resource<TickerResponse>> get() = _tickerLiveData

    private val _showtimeLiveData = MutableLiveData<Resource<ShowTimeResponse>>()
    val showtimeLiveData: LiveData<Resource<ShowTimeResponse>> get() = _showtimeLiveData

    private var _serialNoLiveData = MutableLiveData<String>()
    val serialNoLiveData: LiveData<String> get() = _serialNoLiveData

    private var _channelListLiveData = MutableLiveData<Resource<ChannelListResponse>>()
    val channelListLiveData: LiveData<Resource<ChannelListResponse>> get() = _channelListLiveData

    private var _epgLiveData = MutableLiveData<Resource<EPGResponse>>()
    val epgLiveData: LiveData<Resource<EPGResponse>> get() = _epgLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun fetchHotelService() {
        viewModelScope.launch(Dispatchers.IO) {
            val hotelServicesResponse =
                async { movieBeamRepository.getHotelServiceInfo(Constants.ACCOUNT_ID) }
            val result = awaitAll(
                hotelServicesResponse
            )
            if (result[0] == null) {
                _hotelServiceLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Hotel Services Api"))
            } else {
                _hotelServiceLiveData.postValue(Resource.Success(result[0] as HotelServiceResponse))
            }
        }
    }

    fun fetchEpgData(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val epgResponse = movieBeamRepository.getEPGFromCloud(url)
            if (epgResponse == null) {
                _epgLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Epg Api"))
            } else {
                _epgLiveData.postValue(Resource.Success(epgResponse))
            }
        }
    }

    fun fetchEPGDataFromServer(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _epgLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getEPGDataFromServer(ua)
            if (response == null) {
                _epgLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Epg Server Api"))
            } else {
                _epgLiveData.postValue(Resource.Success(response))
            }
        }
    }

    private fun fetchAllApi(cmd: String, ua: String, mode: String, accountId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val weatherApiResponse = async { movieBeamRepository.getWeatherData(ua) }
            val themeApiResponse = async { movieBeamRepository.getThemeDetails(ua) }
            val accountSetupApiResponse =
                async { movieBeamRepository.getAccountSetupDetails(cmd, ua, mode) }
            val localAttractionResponse = async { movieBeamRepository.getLocalAttractionInfo(ua) }
            val channelListResponse = async { movieBeamRepository.getChannelList(ua) }
            val releasesMoviesMoreResponse = async { movieBeamRepository.getMoviesInfo(ua) }
            val tickerResponse = async { movieBeamRepository.getTvTickerMessages(ua) }
            val showTimeResponse = async { movieBeamRepository.getShowtimeInfo(ua) }

            val result = awaitAll(
                weatherApiResponse,
                themeApiResponse,
                accountSetupApiResponse,
                localAttractionResponse,
                channelListResponse,
                releasesMoviesMoreResponse,
                tickerResponse,
                showTimeResponse
            )

            if (result[0] == null) {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Weather Api"))
            } else {
                _weatherLiveData.postValue(Resource.Success(result[0] as WeatherResponse))
            }

            if (result[1] == null) {
                _themeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Theme Api"))
            } else {
                _themeLiveData.postValue(Resource.Success(result[1] as ThemeResponse))
            }

            if (result[2] == null) {
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Account Setup Api"))
            } else {
                _accountSetupLiveData.postValue(Resource.Success(result[2] as AccountSetupResponse))
            }

            if (result[3] == null) {
                _localAttractionLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Local Attraction Api"))
            } else {
                _localAttractionLiveData.postValue(Resource.Success(result[3] as LocalAttractionResponse))
            }

            if (result[4] == null) {
                _channelListLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Channel List Api"))
            } else {
                _channelListLiveData.postValue(Resource.Success(result[4] as ChannelListResponse))
            }

            if (result[5] == null) {
                _moviesLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Movies Api"))
            } else {
                _moviesLiveData.postValue(Resource.Success(result[5] as MoviesResponse))
            }

            if (result[6] == null) {
                _tickerLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Ticker Api"))
            } else {
                _tickerLiveData.postValue(Resource.Success(result[6] as TickerResponse))
            }

            if (result[7] == null) {
                _showtimeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in ShowTime Api"))
            } else {
                _showtimeLiveData.postValue(Resource.Success(result[7] as ShowTimeResponse))
            }
        }
    }

    // Get Response From DataStore
    fun getDataFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(PreferenceDataStoreConstants.SERIAL_NO, "")
                .collect {
                    _serialNoLiveData.postValue(it)
                }
        }
    }

    // Set Response to DataStore

    fun setUAInDataStore(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        ua: String
    ) {
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(PreferenceDataStoreConstants.UA, ua)
        }
    }

    fun setThemeResponseData(
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
                    tempConditionUrlCloud = context.saveImageCloud(data.tempConditionUrlCloud),
                    type = data.type,
                    visibility = data.visibility,
                    weatherProviderImage = data.weatherProviderImage,
                    weatherProviderImageCloud = context.saveImageCloud(data.weatherProviderImageCloud),
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

    fun setChannelListResponseData(
        dataStore: DataStore<ChannelListResponse>,
        data: ChannelListResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    channelLcnList = data.channelLcnList,
                    type = data.type,
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
                    freeGenreList = data.freeGenreList,
                    freeContentList = data.freeContentList,
                    premiumContentList = data.premiumContentList,
                    premiumGenreList = data.premiumGenreList,
                    id = data.id,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    fun setTickerResponseData(
        dataStore: DataStore<TickerResponse>,
        data: TickerResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    tvTickerList = data.tvTickerList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    fun setShowTimeResponseData(
        dataStore: DataStore<ShowTimeResponse>,
        data: ShowTimeResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    id = data.id,
                    shoContentList = data.shoContentList,
                    shoGenreList = data.shoGenreList,
                    type = data.type,
                    version = data.version
                )
            }
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    fun fetchApis(context: Context, preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            delay(5000)
            if (isNetworkAvailable(context)) {
                fetchAllApi(Constants.ACTIVATE, Constants.UA, Constants.MODE, Constants.ACCOUNT_ID)
            } else {
                delay(5000)
                fetchApis(context, preferenceDataStoreHelper)
            }
        }
    }

}