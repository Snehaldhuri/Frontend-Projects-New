package com.diipl.moviebeam.ui.serial_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.IS_SERIAL_NO_TAKEN_KEY
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.SERIAL_NO
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

class SerialViewModel @Inject constructor() : ViewModel() {

    private var _serialNoTakenLiveData = MutableLiveData<Boolean>()
    val serialNoTakenLiveData: LiveData<Boolean> get() = _serialNoTakenLiveData

    fun setDataInDataStore(
        preferenceDataStoreHelper: PreferenceDataStoreHelper,
        isSerialNoTaken: Boolean,
        serialNo: String
    ) {
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(SERIAL_NO, serialNo)
        }
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(IS_SERIAL_NO_TAKEN_KEY, isSerialNoTaken)
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

}