package com.diipl.moviebeam.ui.hotelinfo


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.TabListObj
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityHotelInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotelInfoActivity : BaseActivity() {
    private val hotelInfoViewModel: HotelInfoViewModel by viewModels()
    private lateinit var binding: ActivityHotelInfoBinding
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private var isHelpinfoScreen = false
    private var helpinfoPosition = 0

    override fun observeViewModel() {
        observe(hotelInfoViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(hotelInfoViewModel.themeLiveData, ::handleThemeResponse)
        observe(hotelInfoViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(hotelInfoViewModel.weatherLiveData, ::handleWeatherResponse)
        observeSnackBarMessages(hotelInfoViewModel.showSnackBar)
        observeToast(hotelInfoViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.btnBack.background = getGradient(gradientStartColor, gradientEndColor)
            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.rvHotelInfoHeader.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                hotelInfoViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                hotelInfoViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                hotelInfoViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExt(it)
                }
                loadBg(hotelInfoViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val tabMap = mutableMapOf<String, TabListObj>()
                val tabs = mutableListOf<String>()
                val response = hotelInfoViewModel.hotelServiceLiveData.value?.data
                for (service in response?.servicesList!!) {
                    when (service.categoryName) {
                        "All" -> {
                            service.serviceList.forEach {
                                tabMap[it.title] = TabListObj(2, it, null)
                                tabs.add(it.title)
                            }
                        }

                        else -> {
                            tabMap[service.categoryName] = TabListObj(1, null, service.serviceList)
                            tabs.add(service.categoryName)
                        }
                    }
                }
                tabs.add(Constants.HELP_INFO)
                tabMap[Constants.HELP_INFO] = TabListObj(3, null, null)

                val adapter = HotelInfoTabAdapter(itemList = tabs,
                    onItemFocused = {

                        val transaction = supportFragmentManager.beginTransaction()
                        when (tabMap[it]?.serviceType) {
                            1 -> {
                                binding.tvHeaderTitle.text = tabMap[it]?.serviceList?.get(0)?.title
                                val carousel = CarouselListFragment { title ->
                                    binding.tvHeaderTitle.text = title
                                }
                                carousel.bindData(tabMap[it]?.serviceList)
                                transaction.replace(R.id.fragment_container_carousel, carousel)
                            }

                            2 -> {
                                binding.tvHeaderTitle.text = it
                                val bundle = Bundle()
                                bundle.putString("title", it)
                                tabMap[it]?.service?.description?.let { desc ->
                                    bundle.putString("desc", desc)
                                }
                                tabMap[it]?.service?.serviceImageList?.get(0)?.let { url ->
                                    bundle.putString("imgUrl", url)
                                }
                                val fragment = HotelServiceInfoFragment()
                                fragment.arguments = bundle
                                transaction.replace(R.id.fragment_container_carousel, fragment)
                            }

                            3 -> {
                                binding.tvHeaderTitle.text = it
                                val bundle = Bundle()
                                bundle.putString("title", it)
                                bundle.putString(
                                    "desc",
                                    hotelInfoViewModel.accountSetupLiveData.value?.data?.address
                                )
                                val fragment = HotelServiceInfoFragment()
                                fragment.arguments = bundle
                                transaction.replace(R.id.fragment_container_carousel, fragment)
                            }

                            else -> {

                                binding.tvHeaderTitle.text = it
                                val bundle = Bundle()
                                bundle.putString("title", it)
                                bundle.putString(
                                    "desc",
                                    hotelInfoViewModel.accountSetupLiveData.value?.data?.address
                                )
                                val fragment = HotelServiceInfoFragment()
                                fragment.arguments = bundle
                                transaction.replace(R.id.fragment_container_carousel, fragment)
                            }
                        }
                        transaction.commit()
                    },
                    onHelpInfoTabClick = { it, pos ->
                        val fragment = HelpInfoFragment() {
                            if (it) {
                                isHelpinfoScreen = false
                                binding.fragmentContainerCarousel.toVisible()
                                binding.rvHotelInfoHeader.toVisible()
                                binding.tvHeaderTitle.toVisible()
                                binding.btnBack.toVisible()
                                binding.layoutHeader.tvTitle.text = Constants.HOTEL_INFORMATION
                                binding.rvHotelInfoHeader.layoutManager?.scrollToPosition(pos)
                            }
                        }
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container_help_info, fragment)
                            .addToBackStack("Help Info").commit()
                        binding.fragmentContainerCarousel.toInvisible()
                        binding.rvHotelInfoHeader.toInvisible()
                        binding.tvHeaderTitle.toInvisible()
                        binding.btnBack.toInvisible()
                        binding.layoutHeader.tvTitle.text = Constants.HELP_INFO
                        binding.tvHeaderTitle.text = ""
                    })
                if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)
                }
                binding.rvHotelInfoHeader.adapter = adapter
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                var temperature = hotelInfoViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    temperature
                binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                    hotelInfoViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud ?: ""
                )
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.tvDate.text =
                    hotelInfoViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.layoutWeatherTime.tvTime.text =
                    hotelInfoViewModel.dateTimeLiveData.value?.data?.time
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
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
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun getGradient(startColor: String, endColor: String): GradientDrawable {
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

}