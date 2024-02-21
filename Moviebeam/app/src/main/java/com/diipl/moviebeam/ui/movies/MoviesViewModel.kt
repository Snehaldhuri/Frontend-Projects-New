package com.diipl.moviebeam.ui.movies

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.room.models.ShowTimeModel
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository,
    private val roomRepository: RoomRepository
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

    private var _isGuestCheckedInLiveData = MutableLiveData<Boolean>()
    val isGuestCheckedInLiveData: LiveData<Boolean> get() = _isGuestCheckedInLiveData

    fun validateSession(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.getPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN,
                false
            ).collect {
                _isGuestCheckedInLiveData.postValue(it)
            }
        }
    }

    private var _rentalMovieResponse = MutableLiveData<Resource<RentalMovieResponse>>()
    val rentalMovieResponse: LiveData<Resource<RentalMovieResponse>> get() = _rentalMovieResponse

    fun getRentalMovieResponse(request: RentalMovieRequest) {
        viewModelScope.launch {
            val result = movieBeamRepository.getMovieAccess(request)
            if (result == null) {
                _rentalMovieResponse.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Movies Services Api"))
            } else {
                _rentalMovieResponse.postValue(Resource.Success(result))
            }
        }
    }


    fun getAllWatchedMovies(): LiveData<List<RentalMovieModel>> {
        return roomRepository.getWatchedMovies()
    }

    fun insertMovieDetails(data: RentalMovieResponse, movie: ContentDto) {
        viewModelScope.launch {
            val model = RentalMovieModel()
            model.rentalID = if (data.rentalID.isEmpty()) 0 else data.rentalID.toInt()
            model.finishTimeStamp =
                if (data.rentalID.isEmpty()) 0 else (model.startTimeStamp + (24 * 60 * 60 * 1000))
            model.sessionID = Constants.SESSION_ID
            model.movieData = movie

            roomRepository.insertRentalMovies(model)
        }
    }

    fun insertShowDetails(show: Detail) {
        viewModelScope.launch {
            val model = ShowTimeModel()
            model.sessionID = Constants.SESSION_ID
            model.seriesData = show

            roomRepository.insertShowDetails(model)
        }
    }


    private var _movieData = MutableLiveData<RentalMovieModel>()
    val movieData: LiveData<RentalMovieModel> get() = _movieData
    private var _seriesData = MutableLiveData<ShowTimeModel>()
    val seriesData: LiveData<ShowTimeModel> get() = _seriesData

    fun getRentalMovie(releaseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieData.postValue(roomRepository.getRentalMovie(releaseId))
        }
    }

    fun getShowData(releaseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _seriesData.postValue(roomRepository.getShowData(releaseId))
        }
    }

    fun updateMovieDetails(rentalMovieModel: RentalMovieModel) {
        viewModelScope.launch {
            rentalMovieModel.lastTimeStamp = System.currentTimeMillis()
            roomRepository.updateRentalMovies(rentalMovieModel)
        }
    }


    fun updateRentalMovieLog(request: RentalMovieRequest) {
        viewModelScope.launch {
            movieBeamRepository.getMovieAccess(request)
        }
    }

    fun updateShowDetails(showTimeModel: ShowTimeModel) {
        viewModelScope.launch {
            showTimeModel.lastTimeStamp = System.currentTimeMillis()
            roomRepository.updateShowDetails(showTimeModel)
        }
    }

    fun deleteMovieDetails(movieData: RentalMovieModel) {
        viewModelScope.launch {
            roomRepository.deleteMovieDetails(movieData)
        }
    }

    fun deleteShowDetails(seriesData: ShowTimeModel) {
        viewModelScope.launch {
            roomRepository.deleteShowDetails(seriesData)
        }
    }


}