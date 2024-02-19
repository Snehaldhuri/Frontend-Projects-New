package com.diipl.moviebeam.ui.stbdetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val stbDetailViewModel: STBDetailViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding
    private var serialNumber: String = ""
    private var UA = ""

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var showTimeDataStore: DataStore<ShowTimeResponse>

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Constants.IS_API_CALLED) {
            preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
            stbDetailViewModel.getDataFromDataStore(preferenceDataStoreHelper)
            Constants.IS_API_CALLED = true
        } else {
            finish()
        }
    }

    //observe class
    override fun observeViewModel() {
        observe(stbDetailViewModel.serialNoLiveData, ::handleSerialNumberResponse)
        observe(stbDetailViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(stbDetailViewModel.themeLiveData, ::handleThemeResponse)
        observe(stbDetailViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(stbDetailViewModel.localAttractionLiveData, ::handleLAServiceResponse)
        observe(stbDetailViewModel.moviesLiveData, ::handleMoviesResponse)
        observe(stbDetailViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
        observe(stbDetailViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)

        observeSnackBarMessages(stbDetailViewModel.showSnackBar)
        observeToast(stbDetailViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityStbdetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.weatherLiveData.value?.data?.let {
                    stbDetailViewModel.setWeatherResponseData(
                        weatherDataStore,
                        it.copy(tempCondition = replaceDegreeSymbol(it.tempCondition))
                    )
                    Log.d("DataStoreResponse", "handleWeatherResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {

                stbDetailViewModel.themeLiveData.value?.data?.let {
                    stbDetailViewModel.setThemeResponseData(themeDataStore, it)
                    Log.d("DataStoreResponse", "handleThemeResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.setAccountSetupResponseData(accountSetupDataStore, it)
                    Constants.ACCOUNT_ID = it.accountId
                    Constants.STB_ROOM_NO = it.roomNo
                    stbDetailViewModel.fetchHotelService()
                    Log.d("DataStoreResponse", "handleAccountSetupResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.hotelServiceLiveData.value?.data?.let {
                    stbDetailViewModel.setHotelServicesResponseData(hotelServicesDataStore, it)
                    Log.d("DataStoreResponse", "handleHotelServiceResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.localAttractionLiveData.value?.data?.let {
                    stbDetailViewModel.setLocalAttractionResponseData(localAttractionDataStore, it)
                    Log.d("DataStoreResponse", "handleLAServiceResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.moviesLiveData.value?.data?.let {
                    stbDetailViewModel.setMoviesResponseData(moviesDataStore, it)
                    Constants.C_LIST_VERSION = it.version
                    Log.d("DataStoreResponse", "handleMoviesResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.showtimeLiveData.value?.data?.let {
                    stbDetailViewModel.setShowTimeResponseData(showTimeDataStore, it)
                }
                val bundle = Bundle()
                bundle.putString("UA", UA)
                val intent = Intent(this, MainMenuActivity::class.java)
                intent.let {
                    it.putExtras(bundle)
                    startActivity(it)
                }
                finish()
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        serialNumber = serialNo
        Constants.SERIAL_NO = serialNo
        UA = "21$serialNumber"
        Constants.UA = UA
        stbDetailViewModel.fetchApis(applicationContext, preferenceDataStoreHelper)

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun replaceDegreeSymbol(temp: String?): String {
        var temperature = ""
        temp?.let {
            temperature = if (it.contains("&deg C")) {
                it.replace("&deg C", Constants.SYMBOL_DEGREE_CELSIUS)
            } else {
                it.replace("&deg F", Constants.SYMBOL_DEGREE_FAHRENHEIT)
            }
        }
        return temperature
    }

    override fun onBackPressed() {}

}