package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
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

    init {
       // fetchlaundryDetails(Constants.UA,"3")
        fetchlaundryDetails("14508KKMH0K299","3")
    }                       //14508KKMH0K299

    private fun fetchlaundryDetails(ua: String, s: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _laundryMasterLiveData.postValue(Resource.Loading())
            val responce = movieBeamRepository.getLaundryDetails(ua, s)
           // val responce = movieBeamRepository.getLaundryDetails2(ua, s)
            Log.e("fetchlaundryresponce", "fetchlaundryDetails:${responce}", )
         //   Log.e("fetchlaundryresponce1", "fetchlaundryDetails:${responce}", )

            if (responce == null) {
                _laundryMasterLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _laundryMasterLiveData.postValue(Resource.Success(responce))
            }
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}