package com.diipl.moviebeam.ui.guestservice.weather

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.saveImageCloud
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    init {
        fetchWeatherData(Constants.UA)
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


    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}