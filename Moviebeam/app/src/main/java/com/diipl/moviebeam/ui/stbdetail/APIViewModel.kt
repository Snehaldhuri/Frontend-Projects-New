package com.diipl.moviebeam.ui.stbdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class APIViewModel @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val movieBeamRepository: MovieBeamRepository,
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper,
) : ViewModel() {

    val isLastApiCallFinished: LiveData<Boolean> = movieBeamRepository.isLastApiCallFinished

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> get() = _networkStatus
    val epgStatus : LiveData<Int> get() = movieBeamRepository.epgStatus

    private fun loge(msg: String) = this@APIViewModel.logE(msg)
    private fun logd(msg: String) = this@APIViewModel.logD(msg)

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    fun getNetworkStatus() = viewModelScope.launch(Dispatchers.IO) {
        preferenceDataStoreHelper.getPreference(PreferenceDataStoreConstants.NETWORK_STATUS, false).collect {
            _networkStatus.postValue(it)
        }
    }

    fun fetchAllAPI() {
        viewModelScope.launch {
            logd("API Call Started")
            while (isActive) {
                if (networkUtils.isNetworkAvailable()) {
                    logd("Network is Available")
                    movieBeamRepository.fetchAllApi()
                    break
                } else {
                    logd("Network is Unavailable. Retrying...")
                    delay(5000)
                }
            }
        }
    }

    fun fetchDataNotPresent() {
        viewModelScope.launch {
            logd("API Call Started")
            while (isActive) {
                if (networkUtils.isNetworkAvailable()) {
                    logd("Network is Available")
                    movieBeamRepository.fetchDataNotPresent()
                    break
                } else {
                    logd("Network is Unavailable. Retrying...")
                    delay(5000)
                }
            }
        }
    }

    fun updateGuestSession(isCheckedIn: Boolean, guestDetails: CmdDataDto?, messageResponse: MessageResponse = MessageResponse()) =
        movieBeamRepository.updateGuestSession(isCheckedIn, guestDetails, messageResponse)

    suspend fun fetchAccountData(isKapingCmd: Boolean) = movieBeamRepository.fetchAccountData(isKapingCmd)
    suspend fun fetchThemeDetails() = movieBeamRepository.fetchThemeDetails()
    suspend fun fetchHotelService() = movieBeamRepository.fetchHotelService()
    suspend fun fetchLocalAttractions() = movieBeamRepository.fetchLocalAttractions()
    suspend fun fetchMoviesMore() = movieBeamRepository.fetchMoviesMore()
    suspend fun fetchShowtime() = movieBeamRepository.fetchShowtime()
    suspend fun fetchChannelList() = movieBeamRepository.fetchChannelList()
    suspend fun fetchGuestMessage() = movieBeamRepository.fetchGuestMessage()
    suspend fun fetchEpgData() = movieBeamRepository.fetchEpgData()


}