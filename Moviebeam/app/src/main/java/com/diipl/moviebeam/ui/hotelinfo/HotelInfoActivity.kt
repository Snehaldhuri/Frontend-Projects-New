package com.diipl.moviebeam.ui.hotelinfo


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.hotelservice.TabListObj
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityHotelInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.handleFocusChange
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

private const val TAG = "HotelInfoActivity"

@AndroidEntryPoint
class HotelInfoActivity : BaseActivity(), HotelInfoTabAdapter.OnFocusChangeListener {

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
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    private lateinit var adapter: HotelInfoTabAdapter
    private var focusedView: View? = null

    override fun observeViewModel() {
        observe(hotelInfoViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(hotelInfoViewModel.themeLiveData, ::handleThemeResponse)
        observeSnackBarMessages(hotelInfoViewModel.showSnackBar)
        observeToast(hotelInfoViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        gradientStartColor = intent.extras?.getString("gradientStartColor").toString()
        gradientEndColor = intent.extras?.getString("gradientEndColor").toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // fetch data from dataStore
            hotelInfoViewModel.getThemeResponseData(themeDataStore)
            hotelInfoViewModel.getAccountSetupResponseData(accountSetupDataStore)
            hotelInfoViewModel.getHotelServicesResponseData(hotelServicesDataStore)

            // check hotel logo image available from local storage
            //   checkHotelLogoImageAvailableLocally()


            binding.btnBack.handleFocusChange()

            binding.btnBack.setOnClickListener {
                finish()
            }
            binding.rvHotelInfoHeader.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            LoggingService.sendMessageToWebSocket(
                "In HotelServicesMain activity",
                getCurrentPanelNumber()
            )
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In HotelServicesMain activity onCreate: ${e.message}",
                getCurrentPanelNumber()
            )
        }
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
                status.data?.let {
//                    hotelInfoViewModel.setHotelServicesResponseData(it)
                    val tabMap = mutableMapOf<String, TabListObj>()
                    val tabs = mutableListOf<String>()
                    val response = it
                    for (service in response.servicesList) {
                        when (service.categoryName) {
                            "All" -> {
                                var helpInfoAdded = false
                                service.serviceList.forEach { s ->
                                    if (s.title == "Help & Info") {
                                        tabMap[s.title] = TabListObj(2, s, null)
                                        tabs.add(s.title)
                                        helpInfoAdded = true
                                    } else {
                                        tabMap[s.title] = TabListObj(2, s, null)
                                        tabs.add(s.title)
                                    }
                                }

                                if (!helpInfoAdded && service.contentTypeId == 15) {
                                    tabs.add(Constants.HELP_INFO)
                                    tabMap[Constants.HELP_INFO] = TabListObj(3, null, null)
                                }
                            }

                            else -> {
                                tabMap[service.categoryName] =
                                    TabListObj(1, null, service.serviceList)
                                tabs.add(service.categoryName)
                            }
                        }
                    }

                    adapter = HotelInfoTabAdapter(
                        itemList = tabs,
                        onItemFocused = { it, view ->
                            focusedView = view
                            val transaction = supportFragmentManager.beginTransaction()
                            when (tabMap[it]?.serviceType) {
                                1 -> {
                                    binding.tvHeaderTitle.text =
                                        tabMap[it]?.serviceList?.get(0)?.title
                                    val carousel = CarouselListFragment(onItemFocused = { title ->
                                        binding.tvHeaderTitle.text = title
                                    }, onLeftKeyPressed = { title ->
                                        binding.rvHotelInfoHeader.post {
                                            binding.rvHotelInfoHeader.findContainingItemView(
                                                focusedView!!
                                            )?.requestFocus()
                                        }
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
                                    var list = tabMap[it]?.service?.serviceImageListNewCloud
                                    if (list?.isEmpty() == true)
                                        list = tabMap[it]?.service?.serviceImageListCloud
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
                            helpInfoTabIndex = pos
                            binding.gsDown.visibility = View.GONE
                            binding.gsUp.visibility = View.GONE
                            binding.fragmentContainerHelpInfo.toVisible()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_help_info, fragment)
                                .commit()
                            binding.fragmentContainerCarousel.toInvisible()
                            binding.rvHotelInfoHeader.toInvisible()
                            binding.tvHeaderTitle.toInvisible()
                            binding.btnBack.toInvisible()
                            binding.layoutHeader.tvTitle.text = Constants.HELP_INFO
                            binding.tvHeaderTitle.text = ""
                        },
                        onFocusChangeListener = this
                    )
                    if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
                        adapter.setGradientColor(gradientStartColor, gradientEndColor)
                    }
                    binding.rvHotelInfoHeader.adapter = adapter
                    binding.tvHeaderTitle.text = tabs[0]
                    binding.pbLoader.toInvisible()
                }

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
        try {
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
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In HotelServicesMain activity loadBg: ${e.message}",
                getCurrentPanelNumber()
            )
        }

    }

    private fun loadBgImageFromLocalStorage(filename: File) {
        try {
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
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In HotelServicesMain activity loadBgImageFromLocalStorage: ${e.message}",
                getCurrentPanelNumber()
            )
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                handleBackClick()
            }

            KeyEvent.KEYCODE_ESCAPE -> {
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

    override fun onItemFocused(position: Int, itemList: List<String>) {
        if (position == 0) {
            binding.gsUp.visibility = View.GONE
        } else {
            binding.gsUp.visibility = View.VISIBLE
        }
        if (position == itemList.size - 1) {
            binding.gsDown.visibility = View.GONE
        } else {
            binding.gsDown.visibility = View.VISIBLE
        }
    }
}
