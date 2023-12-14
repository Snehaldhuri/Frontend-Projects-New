package com.diipl.moviebeam.ui.mainmenu

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.utils.SingleEvent
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerView.adapter = MainMenuBtnAdapter(getBtnList())*/

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
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                var temperature = mainMenuViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if(it.contains("&deg C")){
                        temperature = it.replace("&deg C", " \u2103")
                    }else{
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.txtTemperature.text = temperature
                Glide.with(this).load(mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud).into(binding.imgWeatherImage)
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {

                val startColor = mainMenuViewModel.themeLiveData.value?.data?.gradientColor
                val endColor = mainMenuViewModel.themeLiveData.value?.data?.spotLightColor
                binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
                val adapter = MainMenuBtnAdapter()
                adapter.itemList = getBtnList()
                if (startColor != null && endColor!=null) {
                    adapter.setGradientColor(startColor,endColor)
                }
                binding.recyclerView.adapter = adapter

                Glide.with(this).load(mainMenuViewModel.themeLiveData.value?.data?.themeLogoFileName).into(binding.imgHotelLogo)
                loadBg(mainMenuViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.txtDate.text = mainMenuViewModel.dateTimeLiveData.value?.data?.date
                binding.txtTime.text = mainMenuViewModel.dateTimeLiveData.value?.data?.time
//                Log.i("SIze Calculator :", "${binding.time.textSize} - ${binding.time.textSizeUnit}")
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.txtGreeting.text = mainMenuViewModel.accountSetupLiveData.value?.data?.hotelInfo
                binding.txtGreeting.setTextColor(Color.parseColor("#FFC107"))
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

    private fun loadBg(imgUrl: String?){
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

    private fun getBtnList(): List<BtnModel> {
        val list = mutableListOf(
            BtnModel("prgGuide", R.drawable.program_guide_icon, "Program Guide"),
            BtnModel("vod", R.drawable.video_on_demand_icon, "Movies & More"),
            BtnModel("showtimes", R.drawable.showtime_icon, "Showtime"),
            BtnModel("casting", R.drawable.casting_icon, "Casting"),
            BtnModel("apps", R.drawable.app_world_icon, "Apps"),
            BtnModel("guestServices", R.drawable.guestservices_icon, "Guest Services"),
            BtnModel("hotelServices", R.drawable.hotelservices_icon, "Hotel Info"),
            BtnModel("crackleDefault", R.drawable.crackle_white_icon, "Crackle"),
            BtnModel("inRoomDining", R.drawable.crackle_white_icon, "In Room Dining"),
            BtnModel("lam", R.drawable.crackle_white_icon, "Local Attraction"),
            BtnModel("foodDelivery", R.drawable.fooddelivery_icon, "Food Delivery")
        )
        return list
    }

}
