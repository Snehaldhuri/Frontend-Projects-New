package com.diipl.moviebeam.ui.stbdetail

import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.android.tv.settings.aidl.regular.IDeviceNameConfigureCallback
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.ticker.TvTickerDTO
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.isWorkDone
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.launchNewActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.openSettingsPattern
import com.diipl.moviebeam.utils.scheduleClearCredentialsTask
import com.diipl.moviebeam.utils.scheduleEpgApiCall
import com.diipl.moviebeam.utils.scheduleMsgEndTask
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "STBDetailsActivity"

@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val stbDetailViewModel: STBDetailViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding
    private var serialNumber: String = ""
    private var UA = ""
    private var isEPGServerApiCalled = false

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var preferences: SharedPreference

    @Inject
    lateinit var hardwareAPI: HardwareAPI

    //Variables from datastore
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            applicationContext
        )
    }
    private var stbRoomNo = ""
    private var networkJob: Job? = null

    private val epgHandler by lazy { EPGHandler(this, roomRepository) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stbDetailViewModel.getNetworkStatus(preferenceDataStoreHelper)

        stbRoomNo = preferenceHandler.roomNo
        handleSerialNumberResponse()

        if (!IS_API_CALLED) {
            IS_API_CALLED = true
        } else {
            finish()
        }

        binding.root.openSettingsPattern()

    }

    //observe class
    override fun observeViewModel() {
        observe(stbDetailViewModel.networkStatus, ::handleNetworkResponse)
//        observe(stbDetailViewModel.serialNoLiveData, ::handleSerialNumberResponse)
        observe(stbDetailViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(stbDetailViewModel.themeLiveData, ::handleThemeResponse)
        observe(stbDetailViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(stbDetailViewModel.localAttractionLiveData, ::handleLAServiceResponse)
        observe(stbDetailViewModel.channelListLiveData, ::handleChannelListResponse)
        observe(stbDetailViewModel.moviesLiveData, ::handleMoviesResponse)
        observe(stbDetailViewModel.tickerLiveData, ::handleTickerResponse)
        observe(stbDetailViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
        observe(stbDetailViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(stbDetailViewModel.epgLiveData, ::handleEpgResponse)

        observeSnackBarMessages(stbDetailViewModel.showSnackBar)
        observeToast(stbDetailViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityStbdetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun handleNetworkResponse(isConnected: Boolean) {
        if (!isConnected) {
            launchMain()
        } else {
            Log.e(TAG, "handleNetworkResponse: launch Main cancelled")
            networkJob?.cancel()
        }
    }

    private fun launchMain() {
        networkJob?.cancel()
        Log.e(TAG, "launchMain: networkJob ")
        networkJob = lifecycleScope.launch {
            delay(1000 * 15)
            Log.e(TAG, "launchMain: networkJob start MainMenu")
            launchNewActivity(MainMenuActivity::class.java, true)
        }
    }


    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Success -> {
                stbDetailViewModel.weatherLiveData.value?.data?.let {
                    stbDetailViewModel.setWeatherResponseData(
                        it.copy(tempCondition = replaceDegreeSymbol(it.tempCondition))
                    )
                }
                logD("Weather Api call success")
            }

            else -> {
                logE("Weather Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                status.data?.let {
                    stbDetailViewModel.setThemeResponseData(it)
                    preferenceHandler.updateDatastoreVariables(
                        gradientStartColor = it.gradientColor,
                        gradientEndColor = it.spotLightColor
                    )
                    ThemeDetails.GRADIENT_COLOR_START = it.gradientColor
                    ThemeDetails.GRADIENT_COLOR_END = it.spotLightColor
                    ThemeDetails.GRADIENT = null
                    ThemeDetails.GRADIENT = getGradientColor()
                    ThemeDetails.BG_IMAGE = it.themeBackgroundFileName
                    ThemeDetails.LOGO_IMAGE = it.themeLogoFileName
                }
                logD("Theme Api call success")
            }

            else -> {
                logE("Theme Api call fail")
                isWorkDone = 1
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.setAccountSetupResponseData(it)
                    if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_STB) {
                        hardwareAPI.myService?.setDeviceName(
                            "MBAP_${it.accountId}_${it.roomNo}",
                            object : IDeviceNameConfigureCallback {
                                @Throws(RemoteException::class)
                                override fun onDeviceNameConfigureCallback(s: String) {
                                    Log.e("TAG", "onDeviceNameConfigureCallback: setDeviceName $s")
                                }

                                override fun asBinder(): IBinder? {
                                    return null
                                }
                            }
                        )
                    }
                    preferenceHandler.updateAccountData(it)

                    scheduleClearCredentialsTask(it.checkOutTime)
                    stbDetailViewModel.fetchHotelService(it.accountId)
                }
                logD("Account Setup Api call success")
            }

            else -> {
                logE("Account Setup Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                status.data?.let {
                    stbDetailViewModel.setHotelServicesResponseData(it)
                }
                logD("Hotel Services Api call success")
            }

            else -> {
                logE("Hotel Services Api call fail")
                isWorkDone = 3
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
                    stbDetailViewModel.setLocalAttractionResponseData(it)
                }
                logD("Local Attractions Api call success")
            }

            else -> {
                logE("Local Attractions Api call fail")
                isWorkDone = 2
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleChannelListResponse(status: Resource<ChannelListResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    stbDetailViewModel.setChannelListResponseData(it)
                    preferenceHandler.updateDatastoreVariables(channelCount = it.channelLcnList.size)
                }
                logD("Channel List Api call success")
            }

            else -> {
                logE("Channel List Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
                redirectToMainMenuPage()
            }
        }
    }

    private fun handleEpgResponse(status: Resource<EPGResponse>) {
        when (status) {
            is Resource.Success -> {

                status.data?.let {
                    stbDetailViewModel.channelListLiveData.value?.data?.channelLcnList?.let { list ->
                        epgHandler.parseEPG(it, list)
                    }
                }

                fetchEPGFromServer()

            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let {
                    stbDetailViewModel.showToastMessage(it)
                    launchMain()
                }
            }
        }
    }

    private fun fetchEPGFromServer() {
        epgHandler.epgStatus.observe(this){
            Log.e(TAG, "fetchEPGFromServer: $it")
            when(it){
                EPGHandler.STATUS_FAIL -> {
                    if (!isEPGServerApiCalled) {
                        logD("Fetching EPG Data from server")
                        stbDetailViewModel.fetchEPGDataFromServer(UA)
                        isEPGServerApiCalled = true
                    } else {
                        redirectToMainMenuPage()
                    }
                }
                EPGHandler.STATUS_OK -> redirectToMainMenuPage()
            }
        }

    }

    private fun redirectToMainMenuPage() {
        scheduleEpgApiCall()
        lifecycleScope.launch {
            while (true) {
                Log.e(TAG, "redirectToMainMenuPage: $isWorkDone")
                if (isWorkDone == 3) {
                    launchMain()
                    isWorkDone = 0
                }
                delay(1000 * 10)
            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                stbDetailViewModel.moviesLiveData.value?.data?.let {
                    preferenceHandler.updateDatastoreVariables(
                        moviesCount = it.freeContentList.size.plus(it.premiumContentList.size),
                        cListVersion = it.version
                    )
                    stbDetailViewModel.setMoviesResponseData(it)
                }
                logD("Movies Api call success")
            }

            else -> {
                logE("Movies Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleTickerResponse(status: Resource<TickerResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    val sdf =
                        SimpleDateFormat(Constants.TICKER_MESSAGE_DATE_FORMAT, Locale.ENGLISH)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.tvTickerList?.removeIf { msg ->
                            (msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true) || sdf.parse(
                                msg.etStr
                            ).before(Date())
                        }
                    } else {
                        val iterator = it.tvTickerList?.iterator()
                        while (iterator!!.hasNext()) {
                            val msg: TvTickerDTO = iterator.next()
                            if ((msg.all == 0 && msg.assignedRooms?.contains(stbRoomNo) != true) || sdf.parse(
                                    msg.etStr
                                ).before(Date())
                            ) {
                                iterator.remove()
                            }
                        }
                    }
                    stbDetailViewModel.setTickerResponseData(it)
                    it.tvTickerList?.forEach { msg ->
                        applicationContext.scheduleMsgEndTask(msg)
                    }
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
                status.data?.let {
                    preferenceHandler.updateDatastoreVariables(showsCount = it.shoContentList.size)
                    stbDetailViewModel.setShowTimeResponseData(it)
                }
                stbDetailViewModel.accountSetupLiveData.value?.data?.let {
                    stbDetailViewModel.fetchEpgData(it.epgCdnUrl + it.accountId + Constants.EPG_CLOUD_URL_SUFFIX)
                }
                logD("Showtime Api call success")
            }

            else -> {
                logE("Showtime Api call fail")
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String = "") {
        /*   if (!isNetworkConnected) {
               launchMain()
               return
           }*/
        isWorkDone = 0

        serialNumber = preferenceHandler.serialNo
        UA = "21$serialNumber"
        stbDetailViewModel.fetchApis()

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_SHORT)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_SHORT)
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

    companion object {
        private var IS_API_CALLED = false
    }

}