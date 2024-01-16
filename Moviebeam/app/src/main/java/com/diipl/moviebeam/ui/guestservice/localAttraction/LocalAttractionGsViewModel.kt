package com.diipl.moviebeam.ui.guestservice.localAttraction

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalAttractionGsViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _localAttractionLiveData = MutableLiveData<Resource<LocalAttractionResponse>>()
    val localAttractionLiveData: LiveData<Resource<LocalAttractionResponse>> get() = _localAttractionLiveData

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _dateTimeLiveData = MutableLiveData<Resource<DateTimeResponse>>()
    val dateTimeLiveData: LiveData<Resource<DateTimeResponse>> get() = _dateTimeLiveData

    init {
        fetchDateTime(Constants.UA)
    }

    private fun fetchDateTime(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _dateTimeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getDateTimeData(ua)
            if (response == null) {
                _dateTimeLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _dateTimeLiveData.postValue(Resource.Success(response))
            }
        }
    }

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

    fun getLocalAttractionResponseData(dataStore: DataStore<LocalAttractionResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _localAttractionLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _localAttractionLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _localAttractionLiveData.postValue(Resource.Success(it))
            }
        }
    }


    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}