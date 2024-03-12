package com.diipl.moviebeam.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ViewWeatherTimeDateRowBinding
import com.diipl.moviebeam.ui.mainmenu.MainMenuViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewWeatherTimeDateRowBinding.inflate(inflater, container, false)

        mainMenuViewModel.getThemeResponseData(themeDataStore)
        mainMenuViewModel.getWeatherResponseData(weatherDataStore)
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
        mainMenuViewModel.getMoviesInfoResponseData(moviesDataStore)
        mainMenuViewModel.getShowtimeResponseData(showtimeDataStore)

        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.moviesLiveData, ::handleMoviesServiceResponse)
        observe(mainMenuViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)

        return binding.root
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
                status.data?.let { response ->
                    Constants.ACCOUNT_ID = response.accountId
                    Constants.STB_ROOM_NO = response.roomNo
                }
            }

            else -> {}
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    Constants.GRADIENT_COLOR_END = it.spotLightColor
                    Constants.GRADIENT_COLOR_START = it.gradientColor
                }
            }

            else -> {}
        }
    }

    private fun handleMoviesServiceResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                    Constants.MOVIES_COUNT =
                        response.freeContentList.size.plus(response.premiumContentList.size)
                    Constants.C_LIST_VERSION = response.version
                }
            }
            else -> {}
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                    Constants.SHOWS_COUNT = response.shoContentList.size
                }
            }
            else -> {}
        }
    }

}