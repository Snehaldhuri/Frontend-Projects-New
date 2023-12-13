package com.diipl.moviebeam.ui.mainmenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.RetrofitClient
import com.diipl.moviebeam.utils.ApiResponseUtil
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _dateTimeLiveData = MutableLiveData<Resource<DateTimeResponse>>()
    val dateTimeLiveData: LiveData<Resource<DateTimeResponse>> get() = _dateTimeLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    init {
        fetchWeatherData("17205KKXLKF626")
        fetchThemeDetails("17205KKXLKF626")
        fetchDateTime("17205KKXLKF626")
        fetchAccountSetupDetails("ACTIVATE", "17205KKXLKF626", "JSON")
    }

    fun fetchWeatherData(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getWeatherData(ua)
            if (response == null) {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _weatherLiveData.postValue( Resource.Success(response))
            }
        }
    }

    private fun fetchThemeDetails(ua: String) {
        viewModelScope.launch {
            _themeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getThemeDetails(ua)
            if (response == null) {
                _themeLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _themeLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchDateTime(ua: String) {
        viewModelScope.launch {
            _dateTimeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getDateTimeData(ua)
            if (response == null) {
                _dateTimeLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _dateTimeLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchAccountSetupDetails(cmd: String, ua: String, mode: String) {
        viewModelScope.launch {
            _accountSetupLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createAccountSetupService().getAccountSetupDetails(cmd, ua, mode)
            val response = ApiResponseUtil().getResponseAsObject(resp, AccountSetupResponse::class)
            if (response == null) {
                _accountSetupLiveData.value = Resource.DataError(code = R.string.server_error)
                Log.i("Success Response", "Failed")
            } else {
                _accountSetupLiveData.value = Resource.Success(response)
                Log.i("Success Response", response.toString())
            }
        }
    }

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}