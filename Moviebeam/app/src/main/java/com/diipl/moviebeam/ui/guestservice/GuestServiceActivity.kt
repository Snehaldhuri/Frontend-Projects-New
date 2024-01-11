package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.btn.GsBtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityGuestServiceBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.concierge.ConciergeAdapter
import com.diipl.moviebeam.ui.guestservice.concierge.MakeMyRoomFragment
import com.diipl.moviebeam.ui.guestservice.feedback.FeedbackFragment
import com.diipl.moviebeam.ui.guestservice.flightstatus.FlightStatusFragment
import com.diipl.moviebeam.ui.guestservice.news.NewsFragment
import com.diipl.moviebeam.ui.guestservice.weather.WeatherFragment
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GuestServiceActivity : BaseActivity() {
    private val guestServiceViewModel: GuestServiceViewModel by viewModels()
    private lateinit var binding: ActivityGuestServiceBinding

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    private var gradientStartColor = "#010101"
    private var gradientEndColor = "#EFEFEF"
    private var gradient: GradientDrawable? = null

    override fun initViewBinding() {
        fetchDataFromDatastore()
        binding = ActivityGuestServiceBinding.inflate(layoutInflater)
        fetchDetails()
        setContentView(binding.root)
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        binding.btnBack.setOnFocusChangeListener(::handleBackClick)
        binding.btnBack.setOnClickListener { finish() }
        binding.rvTabLayout.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun observeViewModel() {
        observe(guestServiceViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(guestServiceViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(guestServiceViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeSnackBarMessages(guestServiceViewModel.showSnackBar)
        observeToast(guestServiceViewModel.showToast)
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    replaceDegreeSymbol(guestServiceViewModel.weatherLiveData.value?.data?.tempCondition)
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

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.tvDate.text =
                    guestServiceViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.layoutWeatherTime.tvTime.text =
                    guestServiceViewModel.dateTimeLiveData.value?.data?.time
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
                val adapter = GuestServiceTabAdapter { view, service ->
                    binding.tvServiceTitle.text = service.categoryName
                    when (service.btnId) {
                        Constants.CONCIERGE_ID -> {
                            binding.fvTabContent.toInvisible()
                            val conciergeListFromApi: List<Int>? =
                                guestServiceViewModel.accountSetupLiveData
                                    .value?.data?.conciergeList?.map { concierge -> concierge.serviceId }
                            val conciergeModelList: List<ConciergeBtnModel> =
                                Constants.CONCIERGE_BUTTON_LIST.filter { concierge ->
                                    conciergeListFromApi?.contains(concierge.serviceId) == true
                                }
                            binding.rvTabContent.toVisible()
                            binding.rvTabContent.layoutManager = GridLayoutManager(this, 4)
                            val conciergeAdapter = ConciergeAdapter { conciergeService ->
                                binding.tvServiceTitle.text = conciergeService.categoryName
                                when (conciergeService.serviceId) {
                                    1 -> {
                                        val transaction = supportFragmentManager.beginTransaction()
                                        val fragment = MakeMyRoomFragment()
                                        val dateTimeResponse =
                                            guestServiceViewModel.dateTimeLiveData.value?.data
                                        dateTimeResponse?.let { date ->
                                            fragment.setDate(
                                                date.hour,
                                                date.date.substring(0, 3),
                                                date.day,
                                                date.month,
                                                date.year
                                            )
                                        }
                                        transaction.replace(R.id.fv_tab_content, fragment)
                                        binding.rvTabContent.toInvisible()
                                        transaction.commit()
                                    }
                                }
                            }

                            conciergeAdapter.setButtonList(conciergeModelList)
                            conciergeAdapter.setGradientColor(gradientStartColor, gradientEndColor)
                            binding.rvTabContent.adapter = conciergeAdapter

                        }

                        Constants.FLIGHT_STATUS_ID -> {
                            binding.rvTabContent.toInvisible()
                            val transaction1 = supportFragmentManager.beginTransaction()

                            val fragment = FlightStatusFragment {
                                view.requestFocus()
                            }
                            guestServiceViewModel.accountSetupLiveData.value?.data?.airportCode?.let { airports ->
                                fragment.setAirportList(airports)
                            }
                            fragment.setGradientColor(gradientStartColor, gradientEndColor)
                            transaction1.replace(R.id.fv_tab_content, fragment)
                            transaction1.commit()
                        }

                        Constants.WEATHER_ID -> {
                            binding.rvTabContent.toInvisible()
                            val transaction = supportFragmentManager.beginTransaction()
                            val fragment = WeatherFragment()
                            transaction.replace(R.id.fv_tab_content, fragment)
                            transaction.commit()

                        }

                        Constants.NEWS_ID -> {
                            binding.rvTabContent.toInvisible()
                            val transaction = supportFragmentManager.beginTransaction()
                            val fragment = NewsFragment {
                                view.requestFocus()
                            }
                            fragment.setGradientColor(gradientStartColor, gradientEndColor)
                            transaction.replace(R.id.fv_tab_content, fragment)
                            transaction.commit()
                        }

                        Constants.GUEST_FEEDBACK_ID -> {
                            binding.rvTabContent.toInvisible()
                            val transaction = supportFragmentManager.beginTransaction()
                            val fragment = FeedbackFragment{
                                view.requestFocus()
                            }
                            transaction.replace(R.id.fv_tab_content, fragment)
                            transaction.commit()
                        }
                    }
                }
                adapter.setButtonList(ArrayList(gsBtnModelList.map { it.copy() }))
                adapter.setGradientColor(gradientStartColor, gradientEndColor)

                binding.rvTabLayout.adapter = adapter
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { guestServiceViewModel.showToastMessage(getString(it)) }
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
