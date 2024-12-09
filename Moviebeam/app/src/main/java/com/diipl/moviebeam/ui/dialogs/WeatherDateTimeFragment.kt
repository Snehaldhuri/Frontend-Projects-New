package com.diipl.moviebeam.ui.dialogs

import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.tv.settings.aidl.regular.IDeviceNameConfigureCallback
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ViewWeatherTimeDateRowBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.ui.mainmenu.MainMenuViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setIPInfo
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherDateTimeFragment : Fragment() {

    private lateinit var binding: ViewWeatherTimeDateRowBinding
    private val mainMenuViewModel: MainMenuViewModel by activityViewModels()

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var showtimeDataStore: DataStore<ShowTimeResponse>
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var preferences: SharedPreference

    @Inject
    lateinit var hardwareAPI: HardwareAPI

    @Inject
    lateinit var preferenceHandler: PreferenceHandler


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ViewWeatherTimeDateRowBinding.inflate(inflater, container, false)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(requireContext())

        initializeDatastoreParams()

        if (preferences.irFrequencyModel == null)
            preferences.irFrequencyModel = IRUtils.SELECTED_IR_MODEL

        if (preferences.btCommandModel == null)
            preferences.btCommandModel = IRUtils.SELECTED_BT_MODEL


        mainMenuViewModel.getThemeResponseData(themeDataStore)
        mainMenuViewModel.getWeatherResponseData(weatherDataStore)
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
        mainMenuViewModel.getMoviesInfoResponseData(moviesDataStore)
        mainMenuViewModel.getShowtimeResponseData(showtimeDataStore)
        mainMenuViewModel.getNetworkStatus(preferenceDataStoreHelper)

        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.moviesLiveData, ::handleMoviesServiceResponse)
        observe(mainMenuViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
        observe(mainMenuViewModel.networkStatus, ::handleNetworkResponse)

        return binding.root
    }

    private fun handleNetworkResponse(b: Boolean) {
        if (b) {
            binding.root.toVisible()
            binding.tvDate.toVisible()
            binding.tvTime.toVisible()
            setIPInfo()
        } else {
            binding.root.toInvisible()
            binding.tvDate.toGone()
            binding.tvTime.toGone()
            preferenceHandler.updateDatastoreVariables(
                ipAddress = "0.0.0.0",
                netMask = "0.0.0.0",
                gateway = "0.0.0.0",
                connectivity = "NO INTERNET"
            )
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    binding.txtTemperature.text = it.tempCondition
                    binding.ivWeather.loadImagesWithGlideExt(it.tempConditionUrlCloud)
                }
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { mainMenuViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    CoroutineScope(Dispatchers.Default).launch {
                        if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_STB) {
                            hardwareAPI.myService?.setDeviceName(
                                "MBAP_${it.accountId}_${it.roomNo}",
                                object : IDeviceNameConfigureCallback {
                                    @Throws(RemoteException::class)
                                    override fun onDeviceNameConfigureCallback(s: String) {
                                        Log.e(
                                            "TAG",
                                            "onDeviceNameConfigureCallback: setDeviceName $s"
                                        )
                                    }

                                    override fun asBinder(): IBinder? {
                                        return null
                                    }
                                }
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
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
            }

            else -> {}
        }
    }

    private fun handleMoviesServiceResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                    preferenceHandler.updateDatastoreVariables(
                        moviesCount = response.freeContentList.size.plus(
                            response.premiumContentList.size
                        ),
                        cListVersion = response.version
                    )
                }
            }

            else -> {}
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                    lifecycleScope.launch {
                        var showsCount = 0
                        val newList = mutableListOf<Int>()
                        val list = response.shoContentList.map { s ->
                            async(Dispatchers.IO) {
                                s.seasonList.size
                            }
                        }
                        val list1 = response.shoGenreList.map { s ->
                            async(Dispatchers.IO) {
                                s.detailList.size
                            }
                        }

                        newList.addAll(list.awaitAll())
                        newList.addAll(list1.awaitAll())
                        newList.forEach {
                            showsCount += it
                        }
                        preferenceHandler.updateDatastoreVariables(showsCount = showsCount)
                    }

                }
            }

            else -> {}
        }
    }

    private fun initializeDatastoreParams() {
        GuestDetails.IS_GUEST_CHECKED_IN = preferenceHandler.isGuestCheckedIn
        GuestDetails.SESSION_ID = preferenceHandler.sessionId
    }


}