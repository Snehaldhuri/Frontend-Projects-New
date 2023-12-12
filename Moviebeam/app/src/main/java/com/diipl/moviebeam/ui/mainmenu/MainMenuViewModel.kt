package com.diipl.moviebeam.ui.mainmenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.service.RetrofitClient
import com.diipl.moviebeam.utils.ApiResponseUtil
import com.diipl.moviebeam.utils.SingleEvent
import kotlinx.coroutines.launch

class MainMenuViewModel : ViewModel() {

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
        viewModelScope.launch {
            _weatherLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createLgRestService().getWeather(ua)
            val response = ApiResponseUtil().getResponseAsObject(resp, WeatherResponse::class)
            if (response == null) {
                _weatherLiveData.value = Resource.DataError(R.string.server_error)
                Log.i("Success Response", "Failed")
            } else {
                _weatherLiveData.value = Resource.Success(response)
                Log.i("Success Response", response.toString())
            }
        }
    }

    fun fetchThemeDetails(ua: String) {
        viewModelScope.launch {
            _themeLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createLgRestService().getTheme(ua)
            val response = ApiResponseUtil().getResponseAsObject(resp, ThemeResponse::class)
            if (response == null) {
                _themeLiveData.value = Resource.DataError(R.string.server_error)
                Log.i("Success Response", "Failed")
            } else {
                _themeLiveData.value = Resource.Success(response)
                Log.i("Success Response", response.toString())
            }
        }
    }

    fun fetchDateTime(ua: String) {
        viewModelScope.launch {
            _dateTimeLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createLgRestService().getDateTime(ua)
            val response = ApiResponseUtil().getResponseAsObject(resp, DateTimeResponse::class)
            if (response == null) {
                _dateTimeLiveData.value = Resource.DataError(R.string.server_error)
                Log.i("Success Response", "Failed")
            } else {
                _dateTimeLiveData.value = Resource.Success(response)
                Log.i("Success Response", response.toString())
            }
        }
    }

    fun fetchAccountSetupDetails(cmd: String, ua: String, mode: String) {
        viewModelScope.launch {
            _accountSetupLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createAccountSetupService().getAccountSetupDetails(cmd, ua, mode)
            val response = ApiResponseUtil().getResponseAsObject(resp, AccountSetupResponse::class)
            if (response == null) {
                _accountSetupLiveData.value = Resource.DataError(R.string.server_error)
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