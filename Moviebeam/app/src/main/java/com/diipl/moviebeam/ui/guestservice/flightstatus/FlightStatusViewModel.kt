package com.diipl.moviebeam.ui.guestservice.flightstatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlightStatusViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _flightStatusLiveData = MutableLiveData<Resource<FlightStatusResponse>>()
    val flightStatusLiveData: LiveData<Resource<FlightStatusResponse>> get() = _flightStatusLiveData

    fun getFlightStatus(cmd: String, ua: String, callType: String, apCode: String, mode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _flightStatusLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getFlightStatus(cmd, ua, callType, apCode, mode)
            if (response == null) {
                _flightStatusLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _flightStatusLiveData.postValue(Resource.Success(response))
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