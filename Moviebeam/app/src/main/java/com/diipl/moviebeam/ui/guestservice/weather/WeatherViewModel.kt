package com.diipl.moviebeam.ui.guestservice.weather

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.datastore.UpdateDataStore
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val updateDataStore: UpdateDataStore,
    private val preferenceHandler: PreferenceHandler,
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    init {
        initializeDatastoreParams()
    }

    private fun fetchWeatherData(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getWeatherData(ua)
            if (response == null) {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _weatherLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun setWeatherResponseData(
        data: WeatherResponse
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDataStore.updateWeatherData(data)
        }
    }

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    private fun initializeDatastoreParams() {
        viewModelScope.launch {
            preferenceHandler.loadAllData()
            delay(100)
            fetchWeatherData(preferenceHandler.UA)
        }
    }


}