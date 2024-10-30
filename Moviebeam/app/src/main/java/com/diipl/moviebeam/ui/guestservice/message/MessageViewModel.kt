package com.diipl.moviebeam.ui.guestservice.message

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val movieBeamRepository: MovieBeamRepository,
    private val preferenceHandler: PreferenceHandler
) : ViewModel() {

    private val _guestMessageLiveData = MutableLiveData<Resource<MessageResponse>>()
    val guestMessageLiveData: LiveData<Resource<MessageResponse>> get() = _guestMessageLiveData

    init {
        initializeDatastoreParams()
    }

    private fun fetchGuestMessages(ua: String, guestSessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _guestMessageLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getGuestMessages(ua, guestSessionId)
            if (response == null) {
                _guestMessageLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _guestMessageLiveData.postValue(Resource.Success(response))
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

    private fun initializeDatastoreParams() {
        viewModelScope.launch {
            preferenceHandler.loadAllData()
            delay(100)
            fetchGuestMessages(preferenceHandler.UA, GuestDetails.SESSION_ID)
        }
    }

}