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
    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

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
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                hotelInfoViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                hotelInfoViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                Glide.with(this)
                    .load(hotelInfoViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.header.imgHotelLogo)
                loadBg(hotelInfoViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
//                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
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

                val adapter = HotelInfoTabAdapter(tabs) {
                    val transaction = supportFragmentManager.beginTransaction()
                    when (tabMap[it]?.serviceType) {
                        1 -> {
                            binding.tvServiceTitle.text = tabMap[it]?.serviceList?.get(0)?.title
                            val carousel = CarouselListFragment { title ->
                                binding.tvServiceTitle.text = title
                            }
                            carousel.bindData(tabMap[it]?.serviceList)
                            transaction.replace(R.id.fragment_container_carousel, carousel)
                        }

                        else -> {
                            binding.tvServiceTitle.text = it
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
                    }
                    transaction.commit()
                }
                adapter.setGradientDrawable(getGradient(gradientStartColor, gradientEndColor))
                binding.recyclerView.adapter = adapter
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                var temperature = hotelInfoViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.header.headerWeatherTime.weather.txtTemperature.text = temperature
                binding.header.headerWeatherTime.weather.imgWeatherImage.loadImagesWithGlideExt(
                    hotelInfoViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud ?: ""
                )
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.header.headerWeatherTime.txtDate.text =
                    hotelInfoViewModel.dateTimeLiveData.value?.data?.date
                binding.header.headerWeatherTime.txtTime.text =
                    hotelInfoViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
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