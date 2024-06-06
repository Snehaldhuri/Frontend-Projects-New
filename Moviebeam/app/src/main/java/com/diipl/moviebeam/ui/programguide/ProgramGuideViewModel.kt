package com.diipl.moviebeam.ui.programguide

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgramGuideViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val channelListDataStore: DataStore<ChannelListResponse>
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _channelListLiveData = MutableLiveData<Resource<ChannelListResponse>>()
    val channelListLiveData: LiveData<Resource<ChannelListResponse>> get() = _channelListLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData


    //------------------------------------------datastore-------------------------------------------
    fun getWeatherResponseData(dataStore: DataStore<WeatherResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _weatherLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _weatherLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun getChannelListResponseData(dataStore: DataStore<ChannelListResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _channelListLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _channelListLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _channelListLiveData.postValue(Resource.Success(it))
            }
        }
    }

    fun getAccountSetupResponseData(dataStore: DataStore<AccountSetupResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _accountSetupLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _accountSetupLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _accountSetupLiveData.postValue(Resource.Success(it))
            }
        }
    }


//    fun getChannels() = viewModelScope.launch(Dispatchers.IO){
//        channelListDataStore.data.collect{
//            _channelList.postValue(Resource.Success(it))
//        }
//    }

    fun getAllChannels(key: String?): LiveData<MutableList<ChannelEpgDTO>> {
        return roomRepository.getAllChannels(key)
    }

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

}
