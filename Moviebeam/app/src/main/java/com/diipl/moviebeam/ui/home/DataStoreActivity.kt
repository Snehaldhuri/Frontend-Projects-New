package com.diipl.moviebeam.ui.home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityDataStoreBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DataStoreActivity : BaseActivity() {

    private lateinit var binding: ActivityDataStoreBinding

    val dataStoreViewModel: DataStoreViewModel by viewModels()

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var dateTimeDataStore: DataStore<DateTimeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun observeViewModel() {
        observe(dataStoreViewModel.themeDataStore, ::handleThemeDataStoreResponse)
        observe(dataStoreViewModel.accountSetupDataStore, ::handleAccountSetupResponse)
        observe(dataStoreViewModel.weatherDataStore, ::handleWeatherResponse)
        observe(dataStoreViewModel.dateTimeDataStore, ::handleDateTimeResponse)
    }

    override fun initViewBinding() {
        binding = ActivityDataStoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreViewModel.getThemeResponseData(themeDataStore)
        dataStoreViewModel.getAccountSetupResponseData(accountSetupDataStore)
        dataStoreViewModel.getDateTimeResponseData(dateTimeDataStore)
        dataStoreViewModel.getWeatherResponseData(weatherDataStore)

    }

    private fun handleThemeDataStoreResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {

                dataStoreViewModel.themeDataStore.value?.data?.gradientColor?.let { gradient ->

                    dataStoreViewModel.themeDataStore.value?.data?.spotLightColor?.let { spotlight ->

                        fetchGradientColors(binding.tvGradient, gradient, spotlight)

                        fetchGradientColors(binding.tvSpotlight, spotlight, gradient)
                    }
                }
                Log.d("DATASTORE", "handleThemeDataStoreResponse: ${dataStoreViewModel.themeDataStore.value?.data}")
                binding.tvTheme.text = dataStoreViewModel.themeDataStore.value?.data?.gradientColor
                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { dataStoreViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {
                binding.tvGreeting.text = dataStoreViewModel.accountSetupDataStore.value?.data?.hotelInfo
                binding.tvAccountSetup.text = dataStoreViewModel.accountSetupDataStore.value?.data?.address

                Log.d("DATASTORE", "handleAccountSetupResponse: ${dataStoreViewModel.accountSetupDataStore.value?.data}")
                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { dataStoreViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {
                binding.tvWeather.text = dataStoreViewModel.weatherDataStore.value?.data?.location
                Log.d("DATASTORE", "handleWeatherResponse: ${dataStoreViewModel.weatherDataStore.value?.data}")

                binding.progressBar.toGone()
            }

            else -> {
                status.errorCode?.let { dataStoreViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {

                binding.tvDateTime.text = dataStoreViewModel.dateTimeDataStore.value?.data?.time
                Log.d("DATASTORE", "handleDateTimeResponse: ${dataStoreViewModel.dateTimeDataStore.value?.data}")
                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { dataStoreViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun fetchGradientColors(textView: TextView, startColor: String, endColor: String) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        textView.background = gradientDrawable
    }
}