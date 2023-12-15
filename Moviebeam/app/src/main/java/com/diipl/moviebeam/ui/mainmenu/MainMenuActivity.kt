package com.diipl.moviebeam.ui.mainmenu

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {
    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var binding: ActivityMainMenuBinding
    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun observeViewModel() {
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeSnackBarMessages(mainMenuViewModel.showSnackBar)
        observeToast(mainMenuViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                var temperature = mainMenuViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.tvTemperature.text = temperature
                Glide.with(this)
                    .load(mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud)
                    .into(binding.ivWeather)
                binding.pbLoader.toInvisible()
                binding.txtTemperature.text = temperature
                Glide.with(this)
                    .load(mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud)
                    .into(binding.imgWeatherImage)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                val startColor = mainMenuViewModel.themeLiveData.value?.data?.gradientColor
                val endColor = mainMenuViewModel.themeLiveData.value?.data?.spotLightColor
                binding.rvMenuButton.layoutManager = GridLayoutManager(this, 4)
                val adapter = MainMenuBtnAdapter()
                adapter.itemList = getBtnList()
                if (startColor != null && endColor != null) {
                    adapter.setGradientColor(startColor, endColor)
                mainMenuViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                binding.rvMenuButton.setBackgroundColor(resources.getColor(R.color.menu_list_bg))
                binding.rvMenuButton.adapter = adapter

                Glide.with(this)
                    .load(mainMenuViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.ivHotelLogo)
                mainMenuViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                Glide.with(this)
                    .load(mainMenuViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.imgHotelLogo)
                loadBg(mainMenuViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.tvDate.text = mainMenuViewModel.dateTimeLiveData.value?.data?.date
                binding.tvTime.text = mainMenuViewModel.dateTimeLiveData.value?.data?.time
//                Log.i("SIze Calculator :", "${binding.time.textSize} - ${binding.time.textSizeUnit}")
                binding.pbLoader.toInvisible()
                binding.txtDate.text = mainMenuViewModel.dateTimeLiveData.value?.data?.date
                binding.txtTime.text = mainMenuViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.tvGreeting.text = mainMenuViewModel.accountSetupLiveData.value?.data?.hotelInfo
             //   binding.tvGreeting.setTextColor(Color.parseColor("#FFC107"))
                binding.pbLoader.toInvisible()
                binding.txtGreeting.text =
                    mainMenuViewModel.accountSetupLiveData.value?.data?.hotelInfo
                binding.txtGreeting.setTextColor(Color.parseColor("#FFC107"))
                val btnListFromApi: List<String>? =
                    mainMenuViewModel.accountSetupLiveData.value?.data?.buttonsList?.map {
                        it.buttonName
                    }
                val btnModelList: List<BtnModel> = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                    btnListFromApi?.contains(it.btnId) == true
                }
                binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
                val adapter = MainMenuBtnAdapter { btnId ->
                    when (btnId) {
                        Constants.HOTEL_SERVICES_ID -> {
                            startActivity(Intent(this, HotelInfoActivity::class.java))
                        }
                        else -> {

                        }
                    }
                }
                adapter.itemList = btnModelList
                adapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.recyclerView.adapter = adapter

                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

}
