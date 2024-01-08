package com.diipl.moviebeam.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityHomeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity() {


    private lateinit var binding: ActivityHomeBinding

    val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var dateTimeDataStore: DataStore<DateTimeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.btnGoToDataStoreActivity.setOnClickListener {

            startActivity(Intent(this,DataStoreActivity::class.java))
        }
    }

    override fun observeViewModel() {
        observe(homeViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(homeViewModel.themeLiveData, ::handleThemeResponse)
        observe(homeViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(homeViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observeToast(homeViewModel.showToast)
    }

    override fun initViewBinding() {

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {

                homeViewModel.themeLiveData.value?.data?.let {
                    homeViewModel.setThemeResponseData(themeDataStore, it)
                }

                binding.tvTheme.text = getString(R.string.theme_success)
                Log.d("DATASTORE", "handleThemeDataStoreResponse: ${homeViewModel.themeLiveData.value?.data}")

                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { homeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {

                homeViewModel.accountSetupLiveData.value?.data?.let {
                    homeViewModel.setAccountSetupResponseData(accountSetupDataStore,it)
                }

                binding.tvAccountSetup.text =getString(R.string.account_setup_success)
                Log.d("DATASTORE", "handleAccountSetupResponse: ${homeViewModel.accountSetupLiveData.value?.data}")

                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { homeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> {
                binding.progressBar.toVisible()
            }

            is Resource.Success -> {

                homeViewModel.weatherLiveData.value?.data?.let {
                    homeViewModel.setWeatherResponseData(weatherDataStore, it)
                }
                binding.tvWeather.text = getString(R.string.weather_success)
                Log.d("DATASTORE", "handleWeatherResponse: ${homeViewModel.weatherLiveData.value?.data}")

                binding.progressBar.toGone()
            }

            is Resource.DataError -> {
                status.errorCode?.let { homeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.progressBar.toVisible()
            is Resource.Success -> {

                homeViewModel.dateTimeLiveData.value?.data?.let {
                    homeViewModel.setDateTimeResponseData(dateTimeDataStore, it)
                }
                binding.tvDateTime.text = getString(R.string.date_time_success)
                Log.d("DATASTORE", "handleDateTimeResponse: ${homeViewModel.dateTimeLiveData.value?.data}")

                binding.progressBar.toInvisible()
            }

            else -> {
                status.errorCode?.let { homeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }
}