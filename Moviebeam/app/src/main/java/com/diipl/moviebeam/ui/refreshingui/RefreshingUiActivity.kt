package com.diipl.moviebeam.ui.refreshingui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityRefreshingUiBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RefreshingUiActivity : BaseActivity() {

    private var kapingResponse: KapingResponse? = null

    private lateinit var binding: ActivityRefreshingUiBinding
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private val refreshingUiViewModel: RefreshingUiViewModel by viewModels()

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>

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

    override fun observeViewModel() {
        observe(refreshingUiViewModel.themeLiveData, ::handleThemeResponse)
        observe(refreshingUiViewModel.hotelServiceLiveData, ::handleHotelServicesResponse)
        observe(refreshingUiViewModel.localAttractionLiveData, ::handleLAServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityRefreshingUiBinding.inflate(layoutInflater)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            kapingResponse = intent.getParcelableExtra("response", KapingResponse::class.java)
        } else {
            kapingResponse = intent.getParcelableExtra("response")
        }
        binding.root.postDelayed( {
            this.handleKaping(kapingResponse)
        }, 5000)
    }

    private fun handleKaping(kapingResponse: KapingResponse?) {
        when (kapingResponse?.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> {
                handleAccountActivateCmd()
            }

            KapingConstants.KAP_CMD_CHECK_IN -> {
                handleCheckInCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> {
                handleCheckOutCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                handleThemeChangeCmd()
            }

            KapingConstants.KAP_CMD_REBOOT -> {
//                reboot()
            }

            KapingConstants.KAP_CMD_HS_CHANGE -> {
                handleHsChangeCmd()
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                handleLAChangeCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                handleFetchSyncListCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                handleFetchShowtimeCmd()
            }
        }
        val i = Intent(this, MainMenuActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        refreshingUiViewModel.updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            false,
            kapingResponse.cmdData?.cmdData
        )
    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        refreshingUiViewModel.updateGuestSession(
            preferenceDataStoreHelper,
            guestDetailsDatastore,
            true,
            kapingResponse.cmdData?.cmdData
        )
    }

    private fun handleAccountActivateCmd() {
        refreshingUiViewModel.fetchAccountSetupDetails(
            Constants.ACTIVATE,
            Constants.UA,
            Constants.MODE
        )
    }

    private fun handleThemeChangeCmd() {
        refreshingUiViewModel.fetchThemeDetails(Constants.UA)
    }

    private fun handleHsChangeCmd() {
        refreshingUiViewModel.fetchHotelServiceInfo(Constants.UA)
    }

    private fun handleLAChangeCmd() {
        refreshingUiViewModel.fetchLocalAttractionInfo(Constants.UA)
    }

    private fun handleFetchSyncListCmd() {
        refreshingUiViewModel.fetchSyncList(Constants.UA)
    }

    private fun handleFetchShowtimeCmd() {
        refreshingUiViewModel.fetchShowtimeData(Constants.UA)
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.accountSetupLiveData.value?.data?.let {
                    refreshingUiViewModel.setAccountSetupResponseData(accountSetupDataStore, it)
                    Constants.ACCOUNT_ID = it.accountId
                    Constants.STB_ROOM_NO = it.roomNo
                    Log.d("DataStoreResponse", "handleAccountSetupResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {

                refreshingUiViewModel.themeLiveData.value?.data?.let {
                    refreshingUiViewModel.setThemeResponseData(themeDataStore, it)
                    Log.d("DataStoreResponse", "handleThemeResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleHotelServicesResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.hotelServiceLiveData.value?.data?.let {
                    refreshingUiViewModel.setHotelServicesResponseData(hotelServicesDataStore, it)
                    Log.d("DataStoreResponse", "handleHotelServicesResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Success -> {
                refreshingUiViewModel.localAttractionLiveData.value?.data?.let {
                    refreshingUiViewModel.setLocalAttractionResponseData(
                        localAttractionDataStore,
                        it
                    )
                    Log.d("DataStoreResponse", "handleLAServiceResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                refreshingUiViewModel.moviesLiveData.value?.data?.let {
                    refreshingUiViewModel.updateSyncList(moviesDataStore, it)
                    Constants.C_LIST_VERSION = it.version
                    Log.d("DataStoreResponse", "handleMoviesResponse: $it")
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                refreshingUiViewModel.showtimeLiveData.value?.data?.let {
                    refreshingUiViewModel.updateShowtimeData(showTimeDataStore, it)
                }
            }

            else -> {
                status.errorCode?.let { refreshingUiViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { refreshingUiViewModel.showToastMessage(it) }

            }
        }
    }

    private val REQUEST_PERMISSION_PHONE_STATE = 1

    private fun reboot() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_PERMISSION_PHONE_STATE
            )
        } else {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            pm.reboot("System update")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_PHONE_STATE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                pm.reboot("System update")
            }

            else -> {}
        }
    }

}