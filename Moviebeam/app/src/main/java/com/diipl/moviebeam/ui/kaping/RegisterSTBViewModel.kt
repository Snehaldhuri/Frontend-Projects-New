package com.diipl.moviebeam.ui.kaping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterSTBViewModel @Inject constructor(private val movieBeamRepository: MovieBeamRepository) :
    ViewModel() {

    private val _stbMasterLiveData = MutableLiveData<Resource<StbMasterResponse>>()
    val stbMasterLiveData: LiveData<Resource<StbMasterResponse>> get() = _stbMasterLiveData

    private var _serialNoLiveData = MutableLiveData<String>()
    val serialNoLiveData: LiveData<String> get() = _serialNoLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()

    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun getSerialNoFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            _serialNoLiveData.postValue(
                preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.SERIAL_NO,
                    ""
                )
            )
        }
    }

    fun updateStbStatus(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        isStbRegistered: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.IS_STB_REGISTERED,
                isStbRegistered
            )
        }
    }

    fun processSTBMaster(
        ua: String,
        srNo: String,
        macAddress: String,
        wifiMacAddress: String,
        stbType: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _stbMasterLiveData.postValue(Resource.Loading())
            val response =
                movieBeamRepository.processStbMaster(ua, srNo, macAddress, wifiMacAddress, stbType)
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
