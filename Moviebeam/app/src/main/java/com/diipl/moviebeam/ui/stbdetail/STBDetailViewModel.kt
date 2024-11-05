package com.diipl.moviebeam.ui.stbdetail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.datastore.UpdateDataStore
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
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.NETWORK_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class STBDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val updateDataStore: UpdateDataStore,
    private val networkUtils: NetworkUtils,
    private val movieBeamRepository: MovieBeamRepository,
    private val preferenceHandler: PreferenceHandler,
) : ViewModel() {

    //Variables from datastore
    private var preferenceDataStoreHelper: PreferenceDataStoreHelper =
        PreferenceDataStoreHelper(context)
    private var accountId: String = ""
    private var ua: String = ""

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

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus

    init {
        initializeDatastoreParams()
    }

    fun getNetworkStatus(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.getPreference(NETWORK_STATUS, false).collect {
                _networkStatus.postValue(it)
            }
        }
    }

    fun fetchHotelService(accountId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val hotelServicesResponse =
                async { movieBeamRepository.getHotelServiceInfo(accountId) }
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

    private fun fetchAllApi(cmd: String, ua: String, mode: String) {
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
                logE(Constants.SERVER_ERROR + " in Weather Api")
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Weather Api"))
            } else {
                _weatherLiveData.postValue(Resource.Success(result[0] as WeatherResponse))
            }

            if (result[1] == null) {
                logE(Constants.SERVER_ERROR + " in Theme Api")
                _themeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Theme Api"))
            } else {
                _themeLiveData.postValue(Resource.Success(result[1] as ThemeResponse))
            }

            if (result[2] == null) {
                logE(Constants.SERVER_ERROR + " in Account Setup Api")
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Account Setup Api"))
            } else {
                _accountSetupLiveData.postValue(Resource.Success(result[2] as AccountSetupResponse))
            }

            if (result[3] == null) {
                logE(Constants.SERVER_ERROR + " in Local Attraction Api")
                _localAttractionLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Local Attraction Api"))
            } else {
                _localAttractionLiveData.postValue(Resource.Success(result[3] as LocalAttractionResponse))
            }

            if (result[4] == null) {
                logE(Constants.SERVER_ERROR + " in Channel List Api")
                _channelListLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Channel List Api"))
            } else {
                _channelListLiveData.postValue(Resource.Success(result[4] as ChannelListResponse))
            }

            if (result[5] == null) {
                logE(Constants.SERVER_ERROR + " in Movies Api")
                _moviesLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Movies Api"))
            } else {
                _moviesLiveData.postValue(Resource.Success(result[5] as MoviesResponse))
            }

            if (result[6] == null) {
                logE(Constants.SERVER_ERROR + " in Ticker Api")
                _tickerLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Ticker Api"))
            } else {
                _tickerLiveData.postValue(Resource.Success(result[6] as TickerResponse))
            }

            if (result[7] == null) {
                logE(Constants.SERVER_ERROR + " in ShowTime Api")
                _showtimeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in ShowTime Api"))
            } else {
                _showtimeLiveData.postValue(Resource.Success(result[7] as ShowTimeResponse))
            }
        }
    }

    fun setThemeResponseData(
        data: ThemeResponse,
    ) {
        Constants.isWorkDone = 0
        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateThemeData(data)
        }
    }

    fun setWeatherResponseData(
        data: WeatherResponse,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateWeatherData(data)
        }
    }

    fun setAccountSetupResponseData(data: AccountSetupResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateAccountData(data)
        }
    }

    fun setHotelServicesResponseData(
        data: HotelServiceResponse,
    ) {
        var run = true
        Constants.isWorkDone = 2
        viewModelScope.launch(Dispatchers.IO) {
            while (run) {
                if (Constants.isWorkDone == 2) {
                    run = false
                    updateDataStore.updateHSData(data)
                }
                delay(2000)
            }
        }
    }

    fun setLocalAttractionResponseData(
        data: LocalAttractionResponse,
    ) {
        var run = true
        Constants.isWorkDone = 1
        viewModelScope.launch(Dispatchers.IO) {
            while (run) {
                if (Constants.isWorkDone == 1) {
                    run = false
                    updateDataStore.updateLAData(data)
                }
                delay(2000)
            }
        }
    }

    fun setChannelListResponseData(
        data: ChannelListResponse,
    ) {
        updateDataStore.updateChannelListData(data)
    }

    fun setMoviesResponseData(
        data: MoviesResponse,
    ) {
        updateDataStore.updateMoviesData(data)
    }

    fun setTickerResponseData(
        data: TickerResponse,
    ) {
        updateDataStore.updateTickerData(data)
    }

    fun setShowTimeResponseData(
        data: ShowTimeResponse,
    ) {
        updateDataStore.updateShowTimeData(data)
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    fun fetchApis() {
        viewModelScope.launch {
            this@STBDetailViewModel.logD("Api Call Started")
            delay(5000)
            if (networkUtils.isNetworkAvailable()) {
                this@STBDetailViewModel.logD("Network is Available")
                fetchAllApi(
                    Constants.ACTIVATE,
                    ua,
                    Constants.MODE
                )
            } else {
                delay(5000)
                fetchApis()
            }
        }
    }

    private fun initializeDatastoreParams() = viewModelScope.launch {
        preferenceHandler.loadAllData()
        delay(200)
        accountId = preferenceHandler.accountID
        ua = preferenceHandler.UA
    }


}