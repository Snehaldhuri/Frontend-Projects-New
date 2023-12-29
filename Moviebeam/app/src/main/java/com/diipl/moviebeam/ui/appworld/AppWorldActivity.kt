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
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppWorldActivity : BaseActivity() {

    private lateinit var binding: ActivityAppWorldBinding
    private val appWorldViewModel: AppWorldViewModel by viewModels()

    @Inject
    lateinit var dateTimeDataStore: DataStore<DateTimeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    override fun observeViewModel() {
        observe(appWorldViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(appWorldViewModel.themeLiveData, ::handleThemeResponse)
    }

    override fun initViewBinding() {
        fetchDetailsFromDatasource()
        binding = ActivityAppWorldBinding.inflate(layoutInflater)
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        binding.rvApps.layoutManager = GridLayoutManager(this, 4)
        getInstalledApps()
        binding.btnBack.setOnClickListener {
            finish()
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
        adapter.setAppList(installedApps)
        binding.rvApps.adapter = adapter
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.btnBack.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        view.background = getGradient(
                            appWorldViewModel.themeLiveData.value?.data?.gradientColor,
                            appWorldViewModel.themeLiveData.value?.data?.gradientColor
                        )
                    } else {
                        view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                    }
                }
                appWorldViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExt(it)
                }
                loadBg(appWorldViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.pbLoader.toInvisible()
                binding.btnBack.clearFocus()
                binding.btnBack.requestFocus()
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
//        when (status) {
//            is Resource.Loading -> binding.pbLoader.toVisible()
//            is Resource.Success -> {
//                var temperature = appWorldViewModel.weatherLiveData.value?.data?.tempCondition
//                temperature?.let {
//                    temperature = if (it.contains("&deg C")) {
//                        it.replace("&deg C", Constants.SYMBOL_DEGREE_CELSIUS)
//                    } else {
//                        it.replace("&deg F", Constants.SYMBOL_DEGREE_FAHRENHEIT)
//                    }
//                }
//                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
//                    temperature
//                binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
//                    appWorldViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud ?: ""
//                )
//                binding.pbLoader.toInvisible()
//            }
//
//            else -> {
//                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
//            }
//        }
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
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )

        gradientDrawable.cornerRadius = 20f

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

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

    private fun fetchDetailsFromDatasource(){
        appWorldViewModel.getThemeResponseData(themeDataStore)
        appWorldViewModel.getWeatherResponseData(weatherDataStore)
    }

}