package com.diipl.moviebeam.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ViewWeatherTimeDateRowBinding
import com.diipl.moviebeam.ui.mainmenu.MainMenuViewModel
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewWeatherTimeDateRowBinding.inflate(inflater, container, false)

        mainMenuViewModel.getWeatherResponseData(weatherDataStore)
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)

        return binding.root
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {}
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


}