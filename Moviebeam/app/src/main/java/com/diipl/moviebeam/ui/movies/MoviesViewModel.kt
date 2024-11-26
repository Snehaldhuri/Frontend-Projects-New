package com.diipl.moviebeam.ui.movies

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassRequest
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.DayPassResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.movies.RentalReversalRequest
import com.diipl.moviebeam.data.dto.movies.RentalReversalResponse
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_CONTENT_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.room.models.ShowTimeModel
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.getRentalDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

private const val TAG = "MoviesViewModel"

@HiltViewModel
class MoviesViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val movieBeamRepository: MovieBeamRepository,
    private val preferenceHandler: PreferenceHandler,
    private val accountSetupData: DataStore<AccountSetupResponse>,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _moviesLiveData = MutableLiveData<Resource<MoviesResponse>>()
    val moviesLiveData: LiveData<Resource<MoviesResponse>> get() = _moviesLiveData

    private val _accountSetupLiveData = MutableLiveData<Resource<AccountSetupResponse>>()
    val accountSetupLiveData: LiveData<Resource<AccountSetupResponse>> get() = _accountSetupLiveData

    init {
        preferenceHandler.loadAllData()
    }

    // Get Response From DataStore
    fun getMoviesInfoResponseData(dataStore: DataStore<MoviesResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            _moviesLiveData.postValue(Resource.Loading())
            dataStore.data.catch {
                _moviesLiveData.postValue(Resource.DataError(msg = Constants.SERVER_ERROR))
            }.collect {
                _moviesLiveData.postValue(Resource.Success(it))
            }
        }
    }

    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    private var _isGuestCheckedInLiveData = MutableLiveData<Boolean>()
    val isGuestCheckedInLiveData: LiveData<Boolean> get() = _isGuestCheckedInLiveData

    fun validateSession(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStoreHelper.getPreference(
                PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY,
                false
            ).collect {
                _isGuestCheckedInLiveData.postValue(it)
            }
        }
    }

    private var _rentalMovieResponse = MutableLiveData<Resource<RentalMovieResponse>>()
    val rentalMovieResponse: LiveData<Resource<RentalMovieResponse>> get() = _rentalMovieResponse

    fun getRentalMovieResponse(request: RentalMovieRequest) {
        viewModelScope.launch {
            _rentalMovieResponse.postValue(Resource.Loading())
            val result = movieBeamRepository.getMovieAccess(request)
            if (result == null) {
                _rentalMovieResponse.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Movies Services Api"))
            } else {
                _rentalMovieResponse.postValue(Resource.Success(result))
            }
        }
    }

    fun getNewRentalMovieResponse(request: RentalMovieRequest) {
        viewModelScope.launch {
            try {
                _rentalMovieResponse.postValue(Resource.Loading())
                val response = movieBeamRepository.getNewMovieAccess(
                    q = "RENTAL",
                    UA = request.UA,
                    RID = request.releaseID,
                    PID = request.productId,
                    price = request.price,
                    timeStamp = request.timeStamp,
                    seek = request.seek,
                    sessionID = GuestDetails.SESSION_ID,
                    a = request.a,
                    ra = request.ra,
                    cType = request.cType,
                    seekType = request.seekType,
                    rentalID = request.rentalID,
                    contentTypeID = request.contentTypeID,
                    productType = request.productType,
                    vodMID = request.vodMID,
                    AID = 0L,
                    mode = "JSON"
                )
                if (response == null) {
                    _rentalMovieResponse.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Movies Services Api"))
                } else {
                    _rentalMovieResponse.postValue(Resource.Success(response))
                    Log.d(TAG, "getNewRentalMovieResponse: $response")
                }

            } catch (e: Exception) {
                Log.e("TAG", "Exception: ${e.message}")
                _rentalMovieResponse.postValue(Resource.DataError("An error occurred: ${e.message}"))
            }
        }
    }

    fun requestVODMgr(
        request: RentalMovieRequest
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val epoch = getEpochTimeInSeconds()
                val fileName = "${request.releaseID}_0.m2t"
                val dataResponse = accountSetupData.data.first()

                val vodMgrIp = dataResponse.vodMgrIp
                val vodMgrPort = dataResponse.vodMgrPort

                Log.d(TAG, "requestVODMgr: $vodMgrIp:$vodMgrPort")
                val baseUrl = "http://$vodMgrIp:$vodMgrPort/"
                //http://10.1.8.3:8083/   (reference)
                //url will create something like (http://10.1.8.3:8083/VODManager/ContentRequestServlet?VODMID=1&TRANSID=1732008000&A=2&M=UNICAST&UA=17311KKAJK7608&RID=42171&PID=15022307&PRIORITY=5&FILENAME=42171_0.m2t&CTYPE=NDVR&SEEK=0&JSON=true)
                try {
                    val response = movieBeamRepository.getVodData(
//                        vodMgrIp = dataResponse.vodMgrIp,
//                        vodMgrPort = dataResponse.vodMgrPort,
                        vodMid = 1,
                        transId = epoch.toLong(),
                        streamingType = "2",
                        mode = "UNICAST",
                        ua = request.UA,
                        rentalId = request.releaseID,
                        productId = request.productId,
                        priority = 5,
                        fileName = fileName,
                        contentType = request.cType,
                        seek = request.seek,
                        json = true
                    )
                    if (response == null) {
                        Log.d("TAG", "Fetch failed: $response")
                    } else {
                        Log.e("TAG", "Fetch successful: $response")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "Exception during fetch: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Exception in requestVODMgr: ${e.message}")
            }
        }
    }

    fun getEpochTimeInSeconds(): Int {
        val currentDate = Date()

        val epochTimeInSeconds = (currentDate.time / 1000).toInt()

        Log.d("TAG", "Epoch Time in seconds: $epochTimeInSeconds")

        return epochTimeInSeconds
    }

    fun getAllWatchedMovies(): LiveData<List<RentalMovieModel>> {
        return roomRepository.getWatchedMovies()
    }

    fun insertMovieDetails(data: RentalMovieResponse, movie: ContentDto) {
        viewModelScope.launch {
            val model = RentalMovieModel()
            model.rentalID = if (data.rentalID.isEmpty()) 0 else data.rentalID.toInt()
            model.finishTimeStamp = (model.startTimeStamp + (24 * 60 * 60 * 1000))
            model.sessionID = GuestDetails.SESSION_ID
            model.movieData = movie

            roomRepository.insertRentalMovies(model)
        }
    }

    fun insertShowDetails(show: Detail) {
        viewModelScope.launch {
            val model = ShowTimeModel()
            model.sessionID = GuestDetails.SESSION_ID
            model.seriesData = show

            roomRepository.insertShowDetails(model)
        }
    }

    private var _movieData = MutableLiveData<RentalMovieModel>()
    val movieData: LiveData<RentalMovieModel> get() = _movieData
    private var _seriesData = MutableLiveData<ShowTimeModel>()
    val seriesData: LiveData<ShowTimeModel> get() = _seriesData

    fun getRentalMovie(releaseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieData.postValue(roomRepository.getRentalMovie(releaseId))
        }
    }

    fun getShowData(releaseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _seriesData.postValue(roomRepository.getShowData(releaseId))
        }
    }

    fun updateMovieDetails(rentalMovieModel: RentalMovieModel) {
        viewModelScope.launch {
            rentalMovieModel.lastTimeStamp = System.currentTimeMillis()
            roomRepository.updateRentalMovies(rentalMovieModel)
        }
    }

    fun updateRentalMovieLog(request: RentalMovieRequest) {
        viewModelScope.launch {
            movieBeamRepository.getMovieAccess(request)
        }
    }

    fun updateShowDetails(showTimeModel: ShowTimeModel) {
        viewModelScope.launch {
            showTimeModel.lastTimeStamp = System.currentTimeMillis()
            roomRepository.updateShowDetails(showTimeModel)
        }
    }

    fun deleteMovieDetails(movieData: RentalMovieModel) {
        viewModelScope.launch {
            roomRepository.deleteMovieDetails(movieData)
        }
    }

    fun deleteShowDetails(seriesData: ShowTimeModel) {
        viewModelScope.launch {
            roomRepository.deleteShowDetails(seriesData)
        }
    }

    private val _rentalReversal = MutableLiveData<Resource<RentalReversalResponse>>()
    val rentalReversal: LiveData<Resource<RentalReversalResponse>> get() = _rentalReversal
    fun setRentalReversal(rentalMovieModel: RentalMovieModel) {
        viewModelScope.launch {
            preferenceHandler.loadAllData()
            delay(100)
            if (rentalMovieModel.rentalID != 0) {
                val request = RentalReversalRequest()
                request.reversalDetails = rentalMovieModel.getRentalDetails(preferenceHandler.UA)
                request.UA = preferenceHandler.UA
                val result = movieBeamRepository.setRentalReversal(request)
                if (result == null) {
                    _rentalReversal.postValue(Resource.DataError(msg = Constants.SERVER_ERROR + " in Rental Reversal Services Api"))
                } else {
                    _rentalReversal.postValue(Resource.Success(result))
                }
            } else
                _rentalReversal.postValue(Resource.Success(RentalReversalResponse()))
        }
    }

    private val _adultStatus = MutableLiveData<Boolean>()
    val adultStatus: LiveData<Boolean> get() = _adultStatus
    private val _adultDayPassStatus = MutableLiveData<Boolean>()
    val adultDayPassStatus: LiveData<Boolean> get() = _adultDayPassStatus
    fun getAdultStatus(preferenceDataStoreHelper: PreferenceDataStoreHelper) {
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(ADULT_CONTENT_STATUS, false).collect {
                _adultStatus.postValue(it)
            }
        }
        viewModelScope.launch {
            preferenceDataStoreHelper.getPreference(ADULT_DAY_PASS_STATUS, false).collect {
                _adultDayPassStatus.postValue(it)
            }
        }
    }

    private val _purchaseResponse = MutableLiveData<Resource<DayPassResponse>>()
    val purchaseResponse: LiveData<Resource<DayPassResponse>> get() = _purchaseResponse
    fun buyPassRequest(request: AdultDayPassRequest) {
        viewModelScope.launch {
            _purchaseResponse.postValue(Resource.Loading())
            val result = movieBeamRepository.buyPassRequest(request)
            if (result == null) {
                _purchaseResponse.postValue(Resource.DataError(Constants.SERVER_ERROR))
            } else {
                _purchaseResponse.postValue(Resource.Success(result))
            }
        }
    }

    private fun initializeDatastoreParams() {
        viewModelScope.launch {
            ua = getUa()
        }
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

}
