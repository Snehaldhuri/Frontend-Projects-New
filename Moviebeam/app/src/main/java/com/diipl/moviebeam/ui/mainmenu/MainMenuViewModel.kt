package com.diipl.moviebeam.ui.mainmenu

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.NETWORK_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
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

    private val _tickerLiveData = MutableLiveData<Resource<TickerResponse>>()
    val tickerLiveData: LiveData<Resource<TickerResponse>> get() = _tickerLiveData

    private val _guestDetailsLiveData = MutableLiveData<Resource<CmdDataDto>>()
    val guestDetailsLiveData: LiveData<Resource<CmdDataDto>> get() = _guestDetailsLiveData

    private var _isGuestCheckedInLiveData = MutableLiveData<Boolean>()
    val isGuestCheckedInLiveData: LiveData<Boolean> get() = _isGuestCheckedInLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

    private val _showtimeLiveData = MutableLiveData<Resource<ShowTimeResponse>>()
    val showtimeLiveData: LiveData<Resource<ShowTimeResponse>> get() = _showtimeLiveData

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    fun getNetworkStatus(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.getPreference(NETWORK_STATUS, false).collect {
                _networkStatus.postValue(it)
            }
        }
    }

    fun getMoviesInfoResponseData(dataStore: DataStore<MoviesResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _moviesLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _moviesLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _moviesLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun getShowtimeResponseData(dataStore: DataStore<ShowTimeResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _showtimeLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _showtimeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _showtimeLiveData.postValue(Resource.Success(it))
            }
        }
    }


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

    fun getTickerResponseData(dataStore: DataStore<TickerResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _tickerLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _tickerLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _tickerLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun validateSession(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.getPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY,
                false
            ).collect {
                _isGuestCheckedInLiveData.postValue(it)
            }
        }
    }

    fun getGuestDetails(dataStore: DataStore<CmdDataDto>) {
        viewModelScope.launch(Dispatchers.IO) {
            _guestDetailsLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _guestDetailsLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _guestDetailsLiveData.postValue(Resource.Success(it))
            }
        }
    }


    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}