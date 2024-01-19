package com.diipl.moviebeam.ui.stbdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class STBDetailViewModel @Inject constructor(private val movieBeamRepository: MovieBeamRepository) :
    ViewModel() {

    private val _stbMasterLiveData = MutableLiveData<Resource<StbMasterResponse>>()
    val  stbMasterLiveData: LiveData<Resource<StbMasterResponse>> get() = _stbMasterLiveData
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    init
    {
        fetchstbMasterDetails(Constants.UA, "", "", "", "")

    }

    private fun fetchstbMasterDetails(
        ua: String,
        srno: String,
        macadd: String,
        type: String,
        wifimacadd: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _stbMasterLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getStbMasterDetails(
                ua,
                srno,
                macadd,
                type,
                wifimacadd
            )
            if (response == null) {
                _stbMasterLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _stbMasterLiveData.postValue(Resource.Success(response))
            }
        }
    }

        fun showToastMessage(error: String) {
            showToastPrivate.value = SingleEvent(error)
        }
}