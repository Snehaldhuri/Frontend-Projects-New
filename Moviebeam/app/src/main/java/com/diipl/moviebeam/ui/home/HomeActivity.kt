package com.diipl.moviebeam.ui.home

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityHomeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {


    private lateinit var binding: ActivityHomeBinding

    val homeViewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnFetchWeatherData.setOnClickListener {
            fetchWeatherData()
        }
    }

    private fun fetchWeatherData() {
        homeViewModel.fetchWeatherData("17205KKXLKF626")
    }


    override fun observeViewModel() {
        observe(homeViewModel.weatherLiveData, ::handleWeatherResponse)
    }

    override fun initViewBinding() {

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {binding.progressBar.toVisible()}
            is Resource.Success -> {
                var temperature = homeViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if(it.contains("&deg C")){
                        temperature = it.replace("&deg C", " \u2103")
                    }else{
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.tvWeather.text = temperature
                val weatherImage = homeViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud
                weatherImage?.let {
                    binding.ivWeather.loadImagesWithGlideExt(it)
                    Log.d("Weather Image", "handleWeatherResponse: $it")
                }
                /*Glide.with(this).load(mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud).into(binding.weatherImage)
                binding.pbLoader.toInvisible()*/
                binding.progressBar.toGone()
            }
            is Resource.DataError -> {
                status.errorCode?.let { Log.d("Error", "handleWeatherResponse: $it") }
            }
        }
    }
}