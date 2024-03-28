package com.diipl.moviebeam.ui.serial_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.IS_SERIAL_NO_TAKEN_KEY
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.SERIAL_NO
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SerialViewModel @Inject constructor() : ViewModel() {

    private var _serialNoTakenLiveData = MutableLiveData<Boolean>()
    val serialNoTakenLiveData: LiveData<Boolean> get() = _serialNoTakenLiveData

    private var _stbStatusLiveData = MutableLiveData<Boolean>()
    val stbStatusLiveData: LiveData<Boolean> get() = _stbStatusLiveData

    private var _stbAllocationStatusLiveData = MutableLiveData<Boolean>()
    val stbAllocationStatusLiveData: LiveData<Boolean> get() = _stbAllocationStatusLiveData

    fun setDataInDataStore(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        isSerialNoTaken: Boolean,
        serialNo: String,
        ua: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.putPreference(SERIAL_NO, serialNo)
            preferenceDataStoreHelper.putPreference(IS_SERIAL_NO_TAKEN_KEY, isSerialNoTaken)
            preferenceDataStoreHelper.putPreference(PreferenceDataStoreConstants.UA, ua)
        }
    }

    fun getDataFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            _serialNoTakenLiveData.postValue(
                preferenceDataStoreHelper.getFirstPreference(
                    IS_SERIAL_NO_TAKEN_KEY,
                    false
                )
            )
        }
    }

    fun getStbStatusFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            _stbStatusLiveData.postValue(
                preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.IS_STB_REGISTERED,
                    false
                )
            )
        }
    }

    fun getStbAllocationStatusFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            _stbAllocationStatusLiveData.postValue(
                preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.IS_STB_ALLOCATED,
                    false
                )
            )
        }
    }

}