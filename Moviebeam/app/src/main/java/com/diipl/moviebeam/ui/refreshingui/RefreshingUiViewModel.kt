package com.diipl.moviebeam.ui.refreshingui

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RefreshingUiViewModel"

@HiltViewModel
class RefreshingUiViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _hotelServiceLiveData = MutableLiveData<Resource<HotelServiceResponse>>()
    val hotelServiceLiveData: LiveData<Resource<HotelServiceResponse>> get() = _hotelServiceLiveData

    private val _localAttractionLiveData = MutableLiveData<Resource<LocalAttractionResponse>>()
    val localAttractionLiveData: LiveData<Resource<LocalAttractionResponse>> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

    private val _showtimeLiveData = MutableLiveData<Resource<ShowTimeResponse>>()
    val showtimeLiveData: LiveData<Resource<ShowTimeResponse>> get() = _showtimeLiveData

    private val _channelListLiveData = MutableLiveData<Resource<ChannelListResponse>>()
    val channelListLiveData: LiveData<Resource<ChannelListResponse>> get() = _channelListLiveData

    private val _epgLiveData = MutableLiveData<Resource<EPGResponse>>()
    val epgLiveData: LiveData<Resource<EPGResponse>> get() = _epgLiveData

    fun fetchAccountSetupDetails(cmd: String, ua: String, mode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _accountSetupLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getAccountSetupDetails(cmd, ua, mode)
            if (response == null) {
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _accountSetupLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchThemeDetails(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _themeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getThemeDetails(ua)
            if (response == null) {
                _themeLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _themeLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchLocalAttractionInfo(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _localAttractionLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getLocalAttractionInfo(ua)
            if (response == null) {
                _localAttractionLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _localAttractionLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchHotelServiceInfo(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _hotelServiceLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getHotelServiceInfo(ua)
            if (response == null) {
                _hotelServiceLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _hotelServiceLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchSyncList(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _moviesLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getMoviesInfo(ua)
            if (response == null) {
                _moviesLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _moviesLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchShowtimeData(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _showtimeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getShowtimeInfo(ua)
            if (response == null) {
                _showtimeLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _showtimeLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun handleFetchChannelListCmd(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _channelListLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getChannelList(ua)
            if (response == null) {
                _channelListLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _channelListLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchEPGData(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _epgLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getEPGFromCloud(url)
            if (response == null) {
                _epgLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _epgLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchEPGDataFromServer(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _epgLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getEPGDataFromServer(ua)
            if (response == null) {
                _epgLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _epgLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun updateGuestSession(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        guestDetailsDatastore: DataStore<CmdDataDto>,
        isCheckedIn: Boolean,
        guestDetails: CmdDataDto?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN,
                isCheckedIn
            )
            updateGuestDetails(guestDetailsDatastore, guestDetails)
        }
    }

    private fun updateGuestDetails(
        dataStore: DataStore<CmdDataDto>,
        data: CmdDataDto?
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    sessionId = data?.sessionId,
                    parentSessionId = data?.parentSessionId,
                    adultContentDisabled = data?.adultContentDisabled,
                    message = data?.message,
                    guestFirstName = data?.guestFirstName,
                    guestLastName = data?.guestLastName,
                    adultLocked = data?.adultLocked,
                    passcode = data?.passcode
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

    fun updateSyncList(
        dataStore: DataStore<MoviesResponse>,
        data: MoviesResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    accountId = data.accountId,
                    adultDayPassPrice = data.adultDayPassPrice,
                    id = data.id,
                    type = data.type,
                    version = data.version,
                    freeContentList = data.freeContentList,
                    freeGenreList = data.freeGenreList,
                    premiumContentList = data.premiumContentList,
                    premiumGenreList = data.premiumGenreList
                )
            }
        }
    }

    fun updateShowtimeData(
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

    fun updateChannelList(
        dataStore: DataStore<ChannelListResponse>,
        data: ChannelListResponse
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    channelLcnList = data.channelLcnList,
                    type = data.type
                )
            }
        }
    }

    fun getChannelList(dataStore: DataStore<ChannelListResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _channelListLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _channelListLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _channelListLiveData.postValue(Resource.Success(it))
            }
        }
    }

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}