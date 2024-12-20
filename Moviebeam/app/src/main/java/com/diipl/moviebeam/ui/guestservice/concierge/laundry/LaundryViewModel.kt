package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaundryViewModel @Inject constructor(private val movieBeamRepository: MovieBeamRepository) :
    ViewModel() {


    private val _laundryMasterLiveData = MutableLiveData<Resource<LaundryResponce>>()
    val laundryMasterLiveData: LiveData<Resource<LaundryResponce>> get() = _laundryMasterLiveData


    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private fun fetchLaundryDetails(serviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _laundryMasterLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getLaundryDetails(serviceId)
            if (response == null) {
                _laundryMasterLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _laundryMasterLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}