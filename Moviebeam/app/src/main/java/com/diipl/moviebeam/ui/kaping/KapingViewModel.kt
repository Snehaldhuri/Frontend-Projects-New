package com.diipl.moviebeam.ui.kaping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.kaping.kapingResponce
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KapingViewModel @Inject constructor(private val movieBeamRepository: MovieBeamRepository) :
    ViewModel() {

    private val _kapingLiveData = MutableLiveData<Resource<kapingResponce>>()
    val kapingLiveData: LiveData<Resource<kapingResponce>> get() = _kapingLiveData

    private val showSnakcBarPrivate = MutableLiveData<SingleEvent<Any>>()

    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnakcBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    init {
        fetchKaping(
            "", "", "", "", "", "", "", "", "", "", "", "", "", "" +
                    "", "", "", "", ""
        )
    }


    fun fetchKaping(
        Q: String,
        UA: String,
        DRID: String,
        SW: String,
        CLISTVER: String,
        DV: String,
        TNS: String,
        EVENT: String,
        SID: String,
        RBTY: String,
        MODE: String,
        LAVER: String,
        HSVER: String,
        THMVER: String,
        CMDRES: String,
        CALLBACKFLG: String,
        INRMVER: String,
        LAUVER: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _kapingLiveData.postValue(Resource.Loading())

            val responce = movieBeamRepository.kapingResponce(
                Q,
                UA,
                DRID,
                SW,
                CLISTVER,
                DV,
                TNS,
                EVENT,
                SID,
                RBTY,
                MODE,
                LAVER,
                HSVER,
                THMVER,
                CMDRES,
                CALLBACKFLG,
                INRMVER,
                LAUVER
            )

            if (responce == null) {
                _kapingLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            } else {
                _kapingLiveData.postValue(Resource.Success(responce))
            }
        }
    }

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }


}
