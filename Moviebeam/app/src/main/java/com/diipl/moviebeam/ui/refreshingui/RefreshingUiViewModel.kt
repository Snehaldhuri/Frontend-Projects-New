package com.diipl.moviebeam.ui.refreshingui

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.datastore.UpdateDataStore
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RefreshingUiViewModel"

@HiltViewModel
class RefreshingUiViewModel @Inject constructor(
    private val updateDataStore: UpdateDataStore,
    private val movieBeamRepository: MovieBeamRepository
//    private val workManager: WorkManager
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

    private val _guestMessageLiveData = MutableLiveData<Resource<MessageResponse>>()
    val guestMessageLiveData: LiveData<Resource<MessageResponse>> get() = _guestMessageLiveData




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

    fun fetchHotelServiceInfo(accountId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _hotelServiceLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getHotelServiceInfo(accountId)
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

    fun fetchGuestMessage(ua: String, guestSessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _guestMessageLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getGuestMessages(ua, guestSessionId)
            if (response == null) {
                _guestMessageLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _guestMessageLiveData.postValue(Resource.Success(response))
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
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY,
                isCheckedIn
            )
            guestDetails?.sessionId?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SESSION_ID_KEY,
                    it
                )
            }
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
        data: AccountSetupResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateAccountData(data)
        }
    }

    fun setThemeResponseData(
        data: ThemeResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            /*dataStore.updateData { currentPreferences ->
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

            }*/
            updateDataStore.updateThemeData(data)
        }
    }

    fun setHotelServicesResponseData(
        data: HotelServiceResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateHSData(data)
        }
    }

    fun setLocalAttractionResponseData(
        data: LocalAttractionResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateLAData(data)
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
        viewModelScope.launch(Dispatchers.IO) {
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

    fun updateGuestMessage(
        dataStore: DataStore<MessageResponse>,
        data: MessageResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateData { currentPreferences ->
                currentPreferences.copy(
                    id = data.id,
                    messagesList = data.messagesList,
                    type = data.type,
                )
            }
        }
    }

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    private val _mainMsgStatus = MutableLiveData<Boolean>()
    val mainMsgStatus: LiveData<Boolean> = _mainMsgStatus

    fun setMainMsgStatus(status: Boolean) {
        _mainMsgStatus.value = status
    }

}