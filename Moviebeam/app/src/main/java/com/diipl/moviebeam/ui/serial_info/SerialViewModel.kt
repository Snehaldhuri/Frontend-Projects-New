package com.diipl.moviebeam.ui.serial_info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.IS_SERIAL_NO_TAKEN_KEY
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.SERIAL_NO
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
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
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(SERIAL_NO, serialNo)
        }
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(IS_SERIAL_NO_TAKEN_KEY, isSerialNoTaken)
            preferenceDataStoreHelper.putPreference(PreferenceDataStoreConstants.UA, ua)
        }
    }

    fun getDataFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(SERIAL_NO, "").collect {
            }
        }
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(IS_SERIAL_NO_TAKEN_KEY, false).collect {
                _serialNoTakenLiveData.postValue(it)
            }
        }
    }

    fun getStbStatusFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(PreferenceDataStoreConstants.IS_STB_REGISTERED, false)
                .collect {
                    _stbStatusLiveData.postValue(it)
                }
        }
    }

    fun getStbAllocationStatusFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            _stbAllocationStatusLiveData.postValue(preferenceDataStoreHelper.getFirstPreference(
                PreferenceDataStoreConstants.IS_STB_ALLOCATED,
                false
            ))
        }
    }

}