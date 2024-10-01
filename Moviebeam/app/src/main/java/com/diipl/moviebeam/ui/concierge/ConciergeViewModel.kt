package com.diipl.moviebeam.ui.concierge

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConciergeViewModel @Inject constructor(
    hotelCustomizationDataStore: DataStore<AccountSetupResponse>
) : ViewModel() {

    init {
        getHotelCustomizationResponseData(hotelCustomizationDataStore)
    }

    private val _hotelCustomizationLiveData =
        MutableLiveData<Resource<AccountSetupResponse>>()
    val hotelCustomizationLiveData: LiveData<Resource<AccountSetupResponse>> get() = _hotelCustomizationLiveData

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private fun getHotelCustomizationResponseData(dataStore: DataStore<AccountSetupResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _hotelCustomizationLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _hotelCustomizationLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _hotelCustomizationLiveData.postValue(Resource.Success(it))
            }
        }
    }
    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}