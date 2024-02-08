package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.Constants.ALL_SERVICES
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.btn.GsBtnModel
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityGuestServiceBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.concierge.ConciergeFragment
import com.diipl.moviebeam.ui.guestservice.concierge.GolfFragment
import com.diipl.moviebeam.ui.guestservice.concierge.LaundryTimeFragment
import com.diipl.moviebeam.ui.guestservice.concierge.MakeMyRoomFragment
import com.diipl.moviebeam.ui.guestservice.concierge.SpaFragment
import com.diipl.moviebeam.ui.guestservice.concierge.ToiletryRequestFragment
import com.diipl.moviebeam.ui.guestservice.concierge.VelvetParkingFragment
import com.diipl.moviebeam.ui.guestservice.concierge.laundry.LaundryFragment
import com.diipl.moviebeam.ui.guestservice.feedback.FeedbackFragment
import com.diipl.moviebeam.ui.guestservice.flightstatus.FlightStatusFragment
import com.diipl.moviebeam.ui.guestservice.inroomdininggs.InRoomDiningGsFragment
import com.diipl.moviebeam.ui.guestservice.localAttraction.LocalAttractionGsFragment
import com.diipl.moviebeam.ui.guestservice.news.NewsFragment
import com.diipl.moviebeam.ui.guestservice.weather.WeatherFragment
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class GuestServiceActivity : BaseActivity() {

    private val guestServiceViewModel: GuestServiceViewModel by viewModels()
    private lateinit var binding: ActivityGuestServiceBinding
    private var conciergeIndex = 0

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private var gradient: GradientDrawable? = null
    private var btnId: String = ""
    private var adapterView: View? = null
    private var focusView: View? = null

    override fun initViewBinding() {
        fetchDataFromDatastore()
        binding = ActivityGuestServiceBinding.inflate(layoutInflater)
        fetchDetails()
        setContentView(binding.root)
        binding.btnBack.setOnFocusChangeListener(::handleBackClick)
        binding.btnBack.setOnClickListener { finish() }
        btnId = intent.getStringExtra("btnId").toString()
        if (btnId == ALL_SERVICES) {
            binding.rvTabLayout.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvTabLayout.toVisible()
            binding.tvServiceTitle.toVisible()
        } else {
            binding.rvTabLayout.toGone()
            binding.tvServiceTitle.toGone()
            bindAdapterView(binding.root, btnId)
        }

    }

    override fun observeViewModel() {
        observe(guestServiceViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(guestServiceViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeSnackBarMessages(guestServiceViewModel.showSnackBar)
        observeToast(guestServiceViewModel.showToast)
    }
//    data class LaundryData(
//        val laundryDataList: List<LaundryCategory>
//    )
//
//    data class LaundryCategory(
//        val categoryName: String,
//        val id: Int,
//        val langWiseList: Map<String, LangWiseCategory>,
//        val subCategoryList: List<LaundrySubCategory>
//    )
//
//    data class LangWiseCategory(
//        val categoryName: String
//    )
//
//    data class LaundrySubCategory(
//        val title: String,
//        val dispPrice: String,
//        val price: Double,
//        val id: Int,
//        val categoryName: String,
//        val subTitle: String,
//        val langWiseList: Map<String, LangWiseSubCategory>
//    )
//
//    data class LangWiseSubCategory(
//        val title: String,
//        val subTitle: String
//    )


    private fun readJson(): LaundryDataResponse? {
        val gson = Gson()
        val inputStream = this.assets.open("LaundryData.json")
        val br = BufferedReader(InputStreamReader(inputStream))
        return gson.fromJson(br, LaundryDataResponse::class.java)
    }


    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    guestServiceViewModel.weatherLiveData.value?.data?.tempCondition
                guestServiceViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                        it
                    )
                }
            }

            else -> {
                status.errorCode?.let { guestServiceViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val gsBtnListFromApi: List<String>? = guestServiceViewModel.accountSetupLiveData
                    .value?.data?.gsButtonsList?.map { it.buttonName }
                val gsBtnModelList: List<GsBtnModel> = Constants.GUEST_SERVICE_BUTTON_LIST.filter {
                    gsBtnListFromApi?.contains(it.btnId) == true
                }
                val sortedGsBtnModelList: List<GsBtnModel> = gsBtnModelList.sortedBy {
                    gsBtnListFromApi?.indexOf(it.btnId) ?: Int.MAX_VALUE
                }
                val adapter = GuestServiceTabAdapter(onMenuItemClicked =  { view, service ->
                    binding.tvServiceTitle.text = service.categoryName
                    adapterView = view
                    bindAdapterView(view, service.btnId)
                }, onRightClicked = {
                    focusView?.let {
                        findViewById<View>(it.id).requestFocus()
                    }
                })
                binding.rvTabContent.toInvisible()
                if (btnId == ALL_SERVICES) {
                    val transaction = supportFragmentManager.beginTransaction()
                    val fragment = WeatherFragment()
                    transaction.replace(R.id.fv_tab_content, fragment)
                    transaction.commit()

                    adapter.setButtonList(ArrayList(sortedGsBtnModelList.map { it.copy() }))
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)

                    binding.rvTabLayout.adapter = adapter
                }
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { guestServiceViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(TRANSIT_FRAGMENT_OPEN)
        transaction.replace(binding.fvTabContent.id, fragment)
        transaction.commitNow()
    }

    private fun requestFocus() {
        binding.rvTabLayout.post {
            adapterView?.let {
                binding.rvTabLayout.findContainingItemView(it)?.requestFocus()
            }
        }
    }


    private fun bindAdapterView(view: View, btnId: String) {
        requestFocus()
        when (btnId) {

            Constants.CONCIERGE_ID -> {
                conciergeIndex = 1
                val concierge = ConciergeFragment { cView, conciergeService ->
                    conciergeIndex = 1
                    focusView = cView
                    requestFocus()
                    binding.tvServiceTitle.text = conciergeService.categoryName
                    when (conciergeService.serviceId) {
                        1 -> {
                            val fragment = MakeMyRoomFragment {
                                view.requestFocus()
                            }

                            changeFragment(fragment)

                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )

                        }

                        2 -> {
                            val fragment = VelvetParkingFragment {

                            }
                            changeFragment(fragment)
                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )
                        }

                        4 -> {
                            binding.layoutHeader.tvTitle.text =
                                getString(R.string.toiletry_requests)
                            val fragment = ToiletryRequestFragment {

                            }
                            val mBundle = Bundle()
                            mBundle.putString("gradientStartColor", gradientStartColor)
                            mBundle.putString("gradientEndColor", gradientEndColor)
                            fragment.arguments = mBundle
                            changeFragment(fragment)
                        }

                        5 -> {
                            val fragment = SpaFragment {

                            }

                            changeFragment(fragment)
                        }

                        6 -> {

                            val fragment = GolfFragment {
                                view.requestFocus()
                                view.performClick()
                            }
                            changeFragment(fragment)
                        }

                        7 -> {
                            val fragment = LaundryTimeFragment {
//                                view.requestFocus()
//                                view.performClick()
                            }
                            changeFragment(fragment)

                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )

                        }

                        3 -> {
                            val fragment = LaundryFragment()

                            changeFragment(fragment)

                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )
                            val laundryData =
                                readJson() // Assuming you have this function to read JSON data
                            if (laundryData != null) {
                                fragment.setLaundryData(laundryData)
                            }

                        }
                    }
                }
                changeFragment(concierge)

                val conciergeListFromApi: List<Int>? =
                    guestServiceViewModel.accountSetupLiveData.value?.data?.conciergeList?.map { concierge -> concierge.serviceId }
                val conciergeModelList: List<ConciergeBtnModel> =
                    Constants.CONCIERGE_BUTTON_LIST.filter { concierge ->
                        conciergeListFromApi?.contains(concierge.serviceId) == true
                    }

                concierge.setAdapter(
                    conciergeModelList,
                    gradientStartColor,
                    gradientEndColor,
                    focusView
                )
            }

            Constants.FLIGHT_STATUS_ID -> {
                conciergeIndex = 0
                focusView = null

                val fragment = FlightStatusFragment {
                    view.requestFocus()
                }
                changeFragment(fragment)

                guestServiceViewModel.accountSetupLiveData.value?.data?.airportCode?.let { airports ->
                    fragment.setAirportList(airports)
                }
                fragment.setGradientColor(gradientStartColor, gradientEndColor)

            }

            Constants.WEATHER_ID -> {
                conciergeIndex = 0
                focusView = null
                changeFragment(WeatherFragment())
            }

            Constants.NEWS_ID -> {
                conciergeIndex = 0
                focusView = null
                val fragment = NewsFragment {
                    view.requestFocus()
                }
                changeFragment(fragment)
                fragment.setGradientColor(gradientStartColor, gradientEndColor)

            }

            Constants.GUEST_FEEDBACK_ID -> {
                conciergeIndex = 0
                focusView = null
                val fragment = FeedbackFragment {
                    view.requestFocus()
                }
                changeFragment(fragment)
                fragment.setGradientColor(gradientStartColor, gradientEndColor)

            }

            Constants.LA_ID -> {
                conciergeIndex = 0

                val fragment = LocalAttractionGsFragment{v ->
                    focusView = v
                    requestFocus()
                }
                changeFragment(fragment)
                fragment.setGradientColor(gradientStartColor, gradientEndColor)

            }

            Constants.IN_ROOM_ID -> {
                conciergeIndex = 0
                focusView = null
                changeFragment(InRoomDiningGsFragment())
            }
        }

    }


    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 120
//                    resource.setTint(Color.argb(0.2f, 0f, 0f, 0f))
                    binding.root.background = resource
//                    binding.root.setBackgroundColor(Color.argb(0.6f, 0f, 0f, 0f))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun getGradient(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun handleBackClick(view: View, focus: Boolean) {
        if (focus) {
            view.background = gradient
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

    private fun fetchDataFromDatastore() {
        guestServiceViewModel.getWeatherResponseData(weatherDataStore)
        guestServiceViewModel.getAccountSetupResponseData(accountSetupDataStore)
    }

    private fun fetchDetails() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        intent.extras?.getString("gradientStartColor")?.let {
            gradientStartColor = it
        }
        intent.extras?.getString("gradientEndColor")?.let {
            gradientEndColor = it
        }
        gradient = getGradient()
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        loadBg(intent.extras?.getString("themeBackgroundFileName"))
    }

    private fun handleBackRemoteClick() {
        if (conciergeIndex == 1) {
            bindAdapterView(binding.root, Constants.CONCIERGE_ID)
            /*if (binding.fvTabContent.isVisible) {
                binding.fvTabContent.toInvisible()
                binding.rvTabContent.toVisible()
            } else{
                finish()
            }*/
            conciergeIndex = 0
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                handleBackRemoteClick()
            }
        }
        return false
    }
}
