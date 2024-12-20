package com.diipl.moviebeam.ui.guestservice.news

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val movieBeamRepository: MovieBeamRepository,
    private val preferenceHandler: PreferenceHandler
) : ViewModel() {

    private val _newsHeaderLiveData = MutableLiveData<Resource<NewsHeaderResponse>>()
    val newsHeaderLiveData: LiveData<Resource<NewsHeaderResponse>> get() = _newsHeaderLiveData

    private val _newsLiveData = MutableLiveData<Resource<NewsResponse>>()
    val newsLiveData: LiveData<Resource<NewsResponse>> get() = _newsLiveData

    init {
        fetchNewsHeader()
    }

    private fun fetchNewsHeader() {
        viewModelScope.launch(Dispatchers.IO) {
            _newsHeaderLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getNewsHeader(1)
            if (response == null) {
                _newsHeaderLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _newsHeaderLiveData.postValue(Resource.Success(response))
            }
        }
    }

    fun fetchNewsDetails(newsId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _newsLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getNewsDetails(newsId)
            if (response == null) {
                _newsLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _newsLiveData.postValue(Resource.Success(response))
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