package com.diipl.moviebeam.ui.guestservice.message

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository,
    private val guestMessageDataStore: DataStore<MessageResponse>,
) : ViewModel() {

    private val _guestMessageLiveData = MutableLiveData<Resource<MessageResponse>>()
    val guestMessageLiveData: LiveData<Resource<MessageResponse>> get() = _guestMessageLiveData

    init {
        fetchGuestMessages()
    }

    private fun fetchGuestMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            _guestMessageLiveData.postValue(Resource.Loading())
            val isComplete = movieBeamRepository.fetchGuestMessage()
            if (isComplete) {
                _guestMessageLiveData.postValue(Resource.Success(guestMessageDataStore.data.first()))
            } else {
                _guestMessageLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
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