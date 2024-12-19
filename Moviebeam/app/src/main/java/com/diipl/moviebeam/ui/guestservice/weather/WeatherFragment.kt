package com.diipl.moviebeam.ui.guestservice.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.FragmentWeatherBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment() {

    private var _binding: FragmentWeatherBinding? = null
    val binding get() = _binding!!
    private val weatherViewModel: WeatherViewModel by activityViewModels()

    override fun observeViewModel() = observe(weatherViewModel.weatherLiveData, ::handleWeatherResponse)

    override fun initViewBinding() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                status.data?.let { weatherDetails ->

                    weatherDetails.tempConditionUrlCloud.let {
                        binding.ivWeather.loadImagesWithGlideExt(it)
                    }
                    binding.tvTemperature.text = weatherDetails.tempCondition
                    binding.tvCity.text = weatherDetails.location
                    binding.tvTempHigh.text = weatherDetails.high
                    binding.tvTempLow.text = weatherDetails.low
                    binding.tvSunrise.text = weatherDetails.sunrise
                    binding.tvHumidity.text = weatherDetails.humidity
                    binding.tvDevPoint.text = weatherDetails.dewPoint
                    binding.tvSunset.text = weatherDetails.sunset
                    binding.tvVisibility.text = weatherDetails.visibility
                    binding.tvWind.text = weatherDetails.windSpeed
                    weatherDetails.weatherProviderImageCloud.let {
                        binding.ivWeatherProvider.loadImagesWithGlideExt(it)
                    }
                    binding.pbLoader.toInvisible()
                }
            }

            else -> {
                status.errorCode?.let { weatherViewModel.showToastMessage(getString(it)) }
            }
        }
    }

}