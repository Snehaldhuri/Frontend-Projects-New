package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
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
import com.diipl.moviebeam.ui.guestservice.localAttraction.LaCardAdapterGs
import com.diipl.moviebeam.ui.guestservice.localAttraction.LocalAttractionGsFragment
import com.diipl.moviebeam.ui.guestservice.news.NewsFragment
import com.diipl.moviebeam.ui.guestservice.weather.WeatherFragment
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toDelayVisible
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class GuestServiceActivity : BaseActivity() ,GuestServiceTabAdapter.OnFocusChangeListener{

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
        binding = ActivityGuestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun observeViewModel() {
        observe(guestServiceViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(guestServiceViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeSnackBarMessages(guestServiceViewModel.showSnackBar)
        observeToast(guestServiceViewModel.showToast)

        guestServiceViewModel.getWeatherResponseData(weatherDataStore)
        guestServiceViewModel.getAccountSetupResponseData(accountSetupDataStore)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            fetchDetails()

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

            binding.btnBack.toDelayVisible()
            binding.btnBack.setOnFocusChangeListener(::handleBackClick)
            binding.btnBack.setOnClickListener { finish() }
            LoggingService.sendMessageToWebSocket("In GuestServicesMain activity","08")

        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("In GuestServicesMain activity onCreate:${e.message}","08")
        }
    }


    private fun readLaundryJson(): LaundryDataResponse? {
        return try {
        val gson = Gson()
        val inputStream = this.assets.open("LaundryData.json")
        val br = BufferedReader(InputStreamReader(inputStream))
        gson.fromJson(br, LaundryDataResponse::class.java)
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("In GuestServicesMain activity readLaundryJson:${e.message}","08")
            null
        }
    }
    private fun readToiletryJson(): ToiletryResponse {
        return try {
            val gson = Gson()
            val inputStream = this.assets.open("ToiletryData.json")
            val br = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            for (str in br.readLines()) {
                stringBuilder.append(str)
            }
            val data = JSONObject(stringBuilder.toString())
            gson.fromJson(data.toString(), ToiletryResponse::class.java)
        }
        catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("In GuestServicesMain activity readToiletryJson:${e.message}","08")
            ToiletryResponse()
        }
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
            is Resource.Loading -> {
                binding.loaderView.toVisible()
            }
            is Resource.Success -> {
                try {
                    val gsBtnListFromApi: List<String>? = guestServiceViewModel.accountSetupLiveData
                        .value?.data?.gsButtonsList?.map { it.buttonName }
                    val gsBtnModelList: List<GsBtnModel> =
                        Constants.GUEST_SERVICE_BUTTON_LIST.filter {
                            gsBtnListFromApi?.contains(it.btnId) == true
                        }
                    val sortedGsBtnModelList: List<GsBtnModel> = gsBtnModelList.sortedBy {
                        gsBtnListFromApi?.indexOf(it.btnId) ?: Int.MAX_VALUE
                    }
                    val adapter = GuestServiceTabAdapter(onMenuItemClicked = { view, service ->
                        binding.tvServiceTitle.text = service.categoryName
                        adapterView = view
                        bindAdapterView(view, service.btnId)
                    }, onRightClicked = {

                    },
                        onFocusChangeListener = this // Provide the onFocusChangeListener here
                    )
                    if(btnId != Constants.LA_ID) {
                        val transaction = supportFragmentManager.beginTransaction()
                        val fragment = WeatherFragment()
                        transaction.replace(R.id.fv_tab_content, fragment)
                        transaction.commit()
                    }
                    adapter.setButtonList(ArrayList(sortedGsBtnModelList.map { it.copy() }))
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)

                    binding.rvTabLayout.adapter = adapter

                    binding.loaderView.toInvisible()
                } catch (e: Exception) {
                    LoggingService.sendMessageToWebSocket("handleAccountSetupResponse Exception in GuestServicesMain activity: ${e.message}","08")
                }
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
                                view.performClick()
                            }

                            changeFragment(fragment)

                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )

                        }

                        2 -> {
                            val fragment = VelvetParkingFragment {
                                view.requestFocus()
                                view.performClick()
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
                                view.requestFocus()
                                view.performClick()
                            }
                            val mBundle = Bundle()
                            mBundle.putString("gradientStartColor", gradientStartColor)
                            mBundle.putString("gradientEndColor", gradientEndColor)
                            fragment.arguments = mBundle
                            val toiletryData = readToiletryJson()
                            fragment.setToiletryData(toiletryData)
                            Log.d("snehald","$toiletryData")
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


                            fragment.setGradientColor(
                                gradientStartColor,
                                gradientEndColor
                            )
                            val laundryData = readLaundryJson()
                            if (laundryData != null) {
                                fragment.setLaundryData(laundryData)
                            }

                            changeFragment(fragment)

                            changeFragment(fragment)
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

                guestServiceViewModel.accountSetupLiveData.value?.data?.airportCode?.let { airports ->
                    fragment.setAirportList(airports)
                }
                fragment.setGradientColor(gradientStartColor, gradientEndColor)
                changeFragment(fragment)

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

    override fun onItemFocused(position:Int, itemList: List<GsBtnModel>) {
        if (position == 0) {
            binding.gsUp.visibility = View.GONE
        }
        else{
            binding.gsUp.visibility = View.VISIBLE
        }
        if(position == itemList.size - 1){
            binding.gsDown.visibility = View.GONE
        }
        else {
            binding.gsDown.visibility = View.VISIBLE
        }
    }
}
