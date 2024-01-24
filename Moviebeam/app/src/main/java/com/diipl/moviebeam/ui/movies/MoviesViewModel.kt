package com.diipl.moviebeam.ui.movies

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
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
class MoviesViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

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

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }


}