package com.diipl.moviebeam.ui.hotelinfo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.ui.base.UpdateDataStore
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelInfoViewModel @Inject constructor(
    private val updateDataStore: UpdateDataStore,
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _hotelServiceLiveData = MutableLiveData<Resource<HotelServiceResponse>>()
    val hotelServiceLiveData: LiveData<Resource<HotelServiceResponse>> get() = _hotelServiceLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    // Get Response from DataStore
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

    fun fetchApis(context: Context) {
        viewModelScope.launch {
            delay(1000)
            if (isNetworkAvailable(context)) {
                fetchAllApi(Constants.ACTIVATE, Constants.UA, Constants.MODE, Constants.ACCOUNT_ID)
            } else {
                delay(5000)
                fetchApis(context)
            }
        }
    }

    private fun fetchAllApi(cmd: String, ua: String, mode: String, accountId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val weatherApiResponse = async { movieBeamRepository.getWeatherData(ua) }
            val themeApiResponse = async { movieBeamRepository.getThemeDetails(ua) }
            val accountSetupApiResponse = async { movieBeamRepository.getAccountSetupDetails(cmd, ua, mode) }
            val hotelServicesResponse = async { movieBeamRepository.getHotelServiceInfo(accountId) }

            val result = awaitAll(
                weatherApiResponse,
                themeApiResponse,
                accountSetupApiResponse,
                hotelServicesResponse
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
                _hotelServiceLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Hotel Services Api"))
            } else {
                _hotelServiceLiveData.postValue(Resource.Success(result[3] as HotelServiceResponse))
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

    fun getHotelServicesResponseData(dataStore: DataStore<HotelServiceResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _hotelServiceLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _hotelServiceLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _hotelServiceLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun setHotelServicesResponseData(
        data: HotelServiceResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
           updateDataStore.updateHSData(data)
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}
