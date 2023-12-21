package com.diipl.moviebeam.ui.localattraction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalAttractionViewModel @Inject constructor(
    private val movieBeamRepository: MovieBeamRepository
) : ViewModel() {

    private val _localAttractionLiveData = MutableLiveData<Resource<LocalAttractionResponse>>()
    val localAttractionLiveData: LiveData<Resource<LocalAttractionResponse>> get() = _localAttractionLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData


    init {
        fetchLocalAttractionInfo("17205KKXLKF626")
        fetchThemeDetails("17205KKXLKF626")
    }

    private fun fetchLocalAttractionInfo(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _localAttractionLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getLocalAttractionInfo(ua)
            if (response == null) {
                _localAttractionLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _localAttractionLiveData.postValue(Resource.Success(response))
            }
        }
    }

    private fun fetchThemeDetails(ua: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _themeLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getThemeDetails(ua)
            if (response == null) {
                _themeLiveData.postValue(Resource.DataError(code = R.string.server_error))
            } else {
                _themeLiveData.postValue(Resource.Success(response))
            }
        }
    }

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}