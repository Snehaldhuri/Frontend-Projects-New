package com.diipl.moviebeam.ui.programguide

import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
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
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

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