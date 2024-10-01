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
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ViewWeatherTimeDateRowBinding
import com.diipl.moviebeam.di.HardwareAPI
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
            updateDatastoreVariables(
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
                        if(BuildConfig.BUILD_TYPE==Constants.BUILD_TYPE_STB) {
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
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
                            it.accountId
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
                            it.roomNo
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.EPG_CDN_URL_KEY,
                            it.epgCdnUrl + it.accountId + Constants.EPG_CLOUD_URL_SUFFIX
                        )
                        preferenceDataStoreHelper.putPreference(
                            PreferenceDataStoreConstants.CASTING_URL_KEY,
                            it.stbCastingPageUrl
                        )
                        if (it.contentDetailFlag)
                            preferenceDataStoreHelper.putPreference(
                                PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY,
                                it.httpStreamingHotelvideoUrl + it.hotelChannelList[0].fileName
                            )
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
                    updateDatastoreVariables(
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
                    updateDatastoreVariables(
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
                        updateDatastoreVariables(showsCount = showsCount)
                    }

                }
            }

            else -> {}
        }
    }

    private fun updateDatastoreVariables(
        moviesCount: Int? = null,
        showsCount: Int? = null,
        cListVersion: String? = null,
        ipAddress: String? = null,
        netMask: String? = null,
        gateway: String? = null,
        connectivity: String? = null,
        gradientStartColor: String? = null,
        gradientEndColor: String? = null
    ) {
        lifecycleScope.launch {
            moviesCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.MOVIES_COUNT_KEY,
                    it
                )
            }
            showsCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SHOWS_COUNT_KEY,
                    it
                )
            }
            cListVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.C_LIST_VERSION_KEY,
                    it
                )
            }
            ipAddress?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_ADDRESS_KEY,
                    it
                )
            }
            netMask?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_NET_MASK_KEY,
                    it
                )
            }
            gateway?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_GATEWAY_KEY,
                    it
                )
            }
            connectivity?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.CONNECTIVITY_KEY,
                    it
                )
            }
            gradientStartColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
                    it
                )
            }
            gradientEndColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
                    it
                )
            }
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            GuestDetails.IS_GUEST_CHECKED_IN = getSession()
            GuestDetails.SESSION_ID = getSessionId()
        }
    }

    private suspend fun getSession(): Boolean {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY,
            false
        )
    }

    private suspend fun getSessionId(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.SESSION_ID_KEY,
            ""
        )
    }

}