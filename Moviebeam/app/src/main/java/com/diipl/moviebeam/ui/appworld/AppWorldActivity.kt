package com.diipl.moviebeam.ui.appworld

import android.content.pm.ApplicationInfo
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppWorldActivity : BaseActivity() {

    private lateinit var binding: ActivityAppWorldBinding
    private val appWorldViewModel: AppWorldViewModel by viewModels()

    private var gradient: GradientDrawable? = null

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun observeViewModel() {
        observe(appWorldViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(appWorldViewModel.dateTimeLiveData, ::handleDateTimeResponse)
    }

    override fun initViewBinding() {
        fetchDataFromDatastore()
        binding = ActivityAppWorldBinding.inflate(layoutInflater)
        fetchDetails()
        binding.rvApps.layoutManager = GridLayoutManager(this, 4)
        getInstalledApps()
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnBack.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        setContentView(binding.root)
    }

    private fun getInstalledApps() {
        // get list of all the apps installed
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val adapter = AppAdapter {
            packageManager.getLaunchIntentForPackage(it.packageName)?.let { intent ->
                startActivity(intent)
            }
        }
        val installedApps = filterSystemApps(allApps)
        val selectedApps = installedApps.filter {
            Constants.SELECTED_APPS.contains(packageManager.getApplicationLabel(it))
        }
        adapter.setAppList(selectedApps)
        binding.rvApps.adapter = adapter
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.tvDate.text =
                    appWorldViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.layoutWeatherTime.tvTime.text =
                    appWorldViewModel.dateTimeLiveData.value?.data?.time
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    replaceDegreeSymbol(appWorldViewModel.weatherLiveData.value?.data?.tempCondition)
                appWorldViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                        it
                    )
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun getGradient(startColor: String?, endColor: String?): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun filterSystemApps(apps: List<ApplicationInfo>): List<ApplicationInfo> {
        return apps.filter {
            !isSystemApp(it)
        }
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun fetchDataFromDatastore() {
        appWorldViewModel.getWeatherResponseData(weatherDataStore)
    }

    private fun fetchDetails() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        gradient = getGradient(
            intent.extras?.getString("gradientStartColor"),
            intent.extras?.getString("gradientEndColor")
        )
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExt(it)
        }
        loadBg(intent.extras?.getString("themeBackgroundFileName"))
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