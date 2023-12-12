package com.diipl.moviebeam.ui.hotelinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.service.RetrofitClient
import com.diipl.moviebeam.utils.ApiResponseUtil
import com.diipl.moviebeam.utils.SingleEvent
import kotlinx.coroutines.launch

class HotelInfoViewModel:ViewModel() {

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    private val _hotelServiceLiveData = MutableLiveData<Resource<HotelServiceResponse>>()
    val hotelServiceLiveData: LiveData<Resource<HotelServiceResponse>> get() = _hotelServiceLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    init {
        fetchWeatherData(7147)
        fetchThemeDetails("17205KKXLKF626")
    }

    fun fetchWeatherData(accountId: Int) {
        viewModelScope.launch {
            _hotelServiceLiveData.value = Resource.Loading()
            val resp = RetrofitClient.createLgRestService().getHotelServices(accountId)
            val response = ApiResponseUtil().getResponseAsObject(resp, HotelServiceResponse::class)
            if (response == null) {
                _hotelServiceLiveData.value = Resource.DataError(R.string.server_error)
                Log.i("Success Response", "Failed")
            } else {
                _hotelServiceLiveData.value = Resource.Success(response)
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

}