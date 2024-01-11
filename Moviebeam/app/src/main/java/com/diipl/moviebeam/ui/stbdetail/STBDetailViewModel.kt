package com.diipl.moviebeam.ui.stbdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class STBDetailViewModel @Inject constructor(private val movieBeamRepository: MovieBeamRepository) :
    ViewModel() {

    private val _stbMasterLiveData = MutableLiveData<Resource<StbMasterResponse>>()
    val  stbMasterLiveData: LiveData<Resource<StbMasterResponse>> get() = _stbMasterLiveData

    private val _weatherLiveData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherLiveData: LiveData<Resource<WeatherResponse>> get() = _weatherLiveData

    private val _themeLiveData = MutableLiveData<Resource<ThemeResponse>>()
    val themeLiveData: LiveData<Resource<ThemeResponse>> get() = _themeLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    private val _hotelServiceLiveData = MutableLiveData<Resource<HotelServiceResponse>>()
    val hotelServiceLiveData: LiveData<Resource<HotelServiceResponse>> get() = _hotelServiceLiveData

    private val _localAttractionLiveData = MutableLiveData<Resource<LocalAttractionResponse>>()
    val localAttractionLiveData: LiveData<Resource<LocalAttractionResponse>> get() = _localAttractionLiveData

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

    private var _serialNoLiveData = MutableLiveData<String>()
    val  serialNoLiveData: LiveData<String> get() = _serialNoLiveData

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    init
    {
    }

    fun getDataFromDataStore(preferenceDataStoreHelper: PreferenceDataStoreHelper){
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(PreferenceDataStoreConstants.SERIAL_NO_KEY,"").collect {
                _serialNoLiveData.postValue(it)
            }
        }
    }

    private fun fetchstbMasterDetails(
        ua: String,
        srno: String,
        macadd: String,
        type: String,
        wifimacadd: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _stbMasterLiveData.postValue(Resource.Loading())
            val response = movieBeamRepository.getStbMasterDetails(
                ua,
                srno,
                macadd,
                type,
                wifimacadd
            )
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