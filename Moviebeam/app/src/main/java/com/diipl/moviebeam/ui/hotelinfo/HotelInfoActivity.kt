package com.diipl.moviebeam.ui.hotelinfo


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.TabListObj
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityHotelInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class HotelInfoActivity : BaseActivity() {

    private val hotelInfoViewModel: HotelInfoViewModel by viewModels()

    private lateinit var binding: ActivityHotelInfoBinding
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private var helpInfoTabIndex = 0

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var dateTimeDataStore: DataStore<DateTimeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    private lateinit var adapter: HotelInfoTabAdapter

    override fun observeViewModel() {
        observe(hotelInfoViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(hotelInfoViewModel.themeLiveData, ::handleThemeResponse)
        observe(hotelInfoViewModel.weatherLiveData, ::handleWeatherResponse)
        observeSnackBarMessages(hotelInfoViewModel.showSnackBar)
        observeToast(hotelInfoViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        gradientStartColor = intent.extras?.getString("gradientStartColor").toString()
        gradientEndColor = intent.extras?.getString("gradientEndColor").toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fetch data from dataStore
        hotelInfoViewModel.getThemeResponseData(themeDataStore)
        hotelInfoViewModel.getWeatherResponseData(weatherDataStore)
        hotelInfoViewModel.getAccountSetupResponseData(accountSetupDataStore)
        hotelInfoViewModel.getHotelServicesResponseData(hotelServicesDataStore)

        // check hotel logo image available from local storage
        //   checkHotelLogoImageAvailableLocally()


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
        LoggingService.sendMessageToWebSocket("In HotelServicesMain activity")

    }

    private fun checkHotelLogoImageAvailableLocally() {
        val hotelLogoImageFile =
            File(getExternalFilesDir(null), Constants.THEME_DIRECTORY + "/" + Constants.HOTEL_LOGO)
        val backgroundImageFile = File(
            getExternalFilesDir(null),
            Constants.THEME_DIRECTORY + "/" + Constants.BACKGROUND_IMAGE
        )

        if (hotelLogoImageFile.exists()) {
            // Load the image from local storage using Glide
            Glide.with(this)
                .load(hotelLogoImageFile)
                .into(binding.layoutHeader.ivHotelLogo)
        }

        if (backgroundImageFile.exists()) {
            // Load the image from local storage using Glide
            loadBgImageFromLocalStorage(backgroundImageFile)
        }
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
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                }
                loadBg(hotelInfoViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { hotelInfoViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                hotelInfoViewModel.hotelServiceLiveData.value?.data?.let {
                    hotelInfoViewModel.setHotelServicesResponseData(hotelServicesDataStore, it)
                }

                val tabMap = mutableMapOf<String, TabListObj>()
                val tabs = mutableListOf<String>()
                val response = hotelInfoViewModel.hotelServiceLiveData.value?.data
                for (service in response?.servicesList!!) {
                    when (service.categoryName) {
                        "All" -> {
                            var helpInfoAdded = false
                            service.serviceList.forEach {
                                if (it.title == "Help & Info") {
                                    tabMap[it.title] = TabListObj(2, it, null)
                                    tabs.add(it.title)
                                    helpInfoAdded = true
                                } else {
                                    tabMap[it.title] = TabListObj(2, it, null)
                                    tabs.add(it.title)
                                }
                            }

                            if (!helpInfoAdded) {
                                tabs.add(Constants.HELP_INFO)
                                tabMap[Constants.HELP_INFO] = TabListObj(3, null, null)
                            }
                        }

                        else -> {
                            tabMap[service.categoryName] = TabListObj(1, null, service.serviceList)
                            tabs.add(service.categoryName)
                        }
                    }
                }

                adapter = HotelInfoTabAdapter(itemList = tabs,
                    onItemFocused = { it, view ->
                        val transaction = supportFragmentManager.beginTransaction()
                        when (tabMap[it]?.serviceType) {
                            1 -> {
                                binding.tvHeaderTitle.text = tabMap[it]?.serviceList?.get(0)?.title
                                val carousel = CarouselListFragment({ title ->
                                    binding.tvHeaderTitle.text = title
                                }, { title ->
                                    if (tabMap[it]?.serviceList?.get(0)?.title == title) {
                                        view.requestFocus()
                                    }
                                })
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
                                val list = tabMap[it]?.service?.serviceImageList
                                var imgUrl = "null"
                                if (list!!.isNotEmpty()) {
                                    imgUrl = list[0]
                                }
                                bundle.putString("imgUrl", imgUrl)
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
                    onHelpInfoTabClick = { it, pos, view ->
                        val fragment = HelpInfoFragment {
                            handleBackClick()
                        }
                        val mBundle = Bundle()
                        mBundle.putString("gradientStartColor", gradientStartColor)
                        mBundle.putString("gradientEndColor", gradientEndColor)
                        binding.fragmentContainerHelpInfo.toVisible()
                        fragment.arguments = mBundle
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container_help_info, fragment)
//                            .addToBackStack("Help Info")
                            .commit()
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
                binding.tvHeaderTitle.text = tabs[0]
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { hotelInfoViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    hotelInfoViewModel.weatherLiveData.value?.data?.tempCondition
                binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                    hotelInfoViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud ?: ""
                )
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { hotelInfoViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { hotelInfoViewModel.showToastMessage(it) }

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

    private fun loadBgImageFromLocalStorage(filename: File) {
        Glide.with(this)
            .load(filename)
            .into(object : CustomTarget<Drawable>() {

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
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

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                Log.d("TAG1212", "onKeyDownMain: Back working")
                handleBackClick()
            }

            KeyEvent.KEYCODE_ESCAPE -> {
                Log.d("TAG1212", "onKeyDownMain: Esc working")
                handleBackClick()
            }
        }
        return false
    }

    private fun handleBackClick() {
        if (binding.fragmentContainerHelpInfo.isVisible) {
            binding.fragmentContainerHelpInfo.toInvisible()
            binding.rvHotelInfoHeader.toVisible()
            binding.rvHotelInfoHeader.findViewHolderForAdapterPosition(helpInfoTabIndex)?.itemView?.requestFocus()
            binding.fragmentContainerCarousel.toVisible()
            binding.tvHeaderTitle.toVisible()
            binding.btnBack.toVisible()
            binding.layoutHeader.tvTitle.text = Constants.HOTEL_INFORMATION
            activityStack.add(this::class.java.simpleName)
        } else {
            finish()
        }
    }

}
