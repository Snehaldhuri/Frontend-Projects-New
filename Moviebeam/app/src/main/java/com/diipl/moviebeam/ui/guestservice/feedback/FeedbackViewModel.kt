package com.diipl.moviebeam.ui.guestservice.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.feedback.FeedbackResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _feedbackLiveData = MutableLiveData<Resource<FeedbackResponse>>()
    val feedbackLiveData: LiveData<Resource<FeedbackResponse>> get() = _feedbackLiveData

    fun sendGuestFeedback(feedback: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _feedbackLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.sendGuestFeedback(feedback)
            if (response == null) {
                _feedbackLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _feedbackLiveData.postValue(Resource.Success(response))
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