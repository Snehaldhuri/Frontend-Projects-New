package com.diipl.moviebeam.ui.guestservice.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.FragmentWeatherBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : BaseFragment() {

    private var _binding: FragmentWeatherBinding? = null
    val binding get() = _binding!!
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun observeViewModel() {
        observe(weatherViewModel.weatherLiveData, ::handleWeatherResponse)
    }

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
                val weatherDetails = weatherViewModel.weatherLiveData.value?.data
                weatherDetails?.copy(tempCondition = replaceDegreeSymbol(weatherDetails.tempCondition))
                    ?.let {
                        weatherViewModel.setWeatherResponseData(
                            weatherDataStore,
                            it
                        )
                    }
                weatherDetails?.tempConditionUrlCloud?.let {
                    binding.ivWeather.loadImagesWithGlideExt(it)
                }
                binding.tvTemperature.text = replaceDegreeSymbol(weatherDetails?.tempCondition)
                binding.tvCity.text = weatherDetails?.location
                binding.tvTempHigh.text =
                    replaceDegreeSymbol(weatherDetails?.high).replace("high ", "")
                binding.tvTempLow.text =
                    replaceDegreeSymbol(weatherDetails?.low).replace("low ", "")
                binding.tvSunrise.text = weatherDetails?.sunrise
                binding.tvHumidity.text = weatherDetails?.humidity
                binding.tvDevPoint.text = replaceDegreeSymbol(weatherDetails?.dewPoint)
                binding.tvSunset.text = weatherDetails?.sunset
                binding.tvVisibility.text = weatherDetails?.visibility
                binding.tvWind.text = weatherDetails?.windSpeed
                weatherDetails?.weatherProviderImageCloud?.let {
                    binding.ivWeatherProvider.loadImagesWithGlideExt(it)
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { weatherViewModel.showToastMessage(getString(it)) }
            }
        }
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

}