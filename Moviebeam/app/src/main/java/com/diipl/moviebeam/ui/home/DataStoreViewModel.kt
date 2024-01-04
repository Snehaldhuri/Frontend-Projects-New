package com.diipl.moviebeam.ui.home

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(): ViewModel() {


    private val _themeDataStore = MutableLiveData<Resource<ThemeResponse>>()
    val themeDataStore: LiveData<Resource<ThemeResponse>> get() = _themeDataStore

    private val _accountSetupDataStore = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupDataStore: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupDataStore

    private val _weatherDataStore = MutableLiveData<Resource<WeatherResponse>>()
    val weatherDataStore: LiveData<Resource<WeatherResponse>> get() = _weatherDataStore

    private val _dateTimeDataStore = MutableLiveData<Resource<DateTimeResponse>>()
    val dateTimeDataStore: LiveData<Resource<DateTimeResponse>> get() = _dateTimeDataStore

    fun getThemeResponseData(dataStore: DataStore<ThemeResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collect {
                _themeDataStore.postValue(Resource.Success(it))
            }
        }
    }

    fun getAccountSetupResponseData(dataStore: DataStore<AccountSetupResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collect {
                _accountSetupDataStore.postValue(Resource.Success(it))
            }
        }
    }

    fun getWeatherResponseData(dataStore: DataStore<WeatherResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.catch {
                _weatherDataStore.postValue(Resource.DataError(it.toString()))
            }.collect {
                _weatherDataStore.postValue(Resource.Success(it))
            }
        }
    }

    fun getDateTimeResponseData(dataStore: DataStore<DateTimeResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collect {
                _dateTimeDataStore.postValue(Resource.Success(it))
            }
        }
    }

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}