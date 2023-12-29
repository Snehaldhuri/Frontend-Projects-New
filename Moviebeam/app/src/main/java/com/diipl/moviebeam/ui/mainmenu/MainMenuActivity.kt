package com.diipl.moviebeam.ui.mainmenu

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.localattraction.LocalAttractionActivity
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {
    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var binding: ActivityMainMenuBinding
    private var gradientStartColor = ""
    private var gradientEndColor = ""

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var hotelServicesDataStore: DataStore<HotelServiceResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // call below function to get data from datastore

        /* mainMenuViewModel.getThemeResponseData(themeDataStore)
         mainMenuViewModel.getWeatherResponseData(weatherDataStore)
         mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
         */

    }

    override fun observeViewModel() {
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.hotelServiceLiveData, ::handleHotelServiceResponse)
        observe(mainMenuViewModel.localAttractionLiveData, ::handleLAServiceResponse)
        observe(mainMenuViewModel.moviesLiveData, ::handleMoviesResponse)

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

                mainMenuViewModel.weatherLiveData.value?.data?.let {
                    mainMenuViewModel.setWeatherResponseData(weatherDataStore, it)
                }
                var temperature = mainMenuViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.tvTemperature.text = temperature
                mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.ivWeather.loadImagesWithGlideExt(it)
                }
                binding.pbLoader.toInvisible()

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

                val response = mainMenuViewModel.themeLiveData.value?.data
                response?.let {
                    mainMenuViewModel.setThemeResponseData(themeDataStore, it)
                }

                binding.rvMenuButton.setBackgroundColor(resources.getColor(R.color.menu_list_bg))
                response?.themeLogoFileName?.let {
                    getImageBitmap(it, Constants.HOTEL_LOGO)
                    binding.ivHotelLogo.loadImagesWithGlideExt(it)
                }
                response?.gradientColor?.let {
                    gradientStartColor = it
                }
                response?.spotLightColor?.let {
                    gradientEndColor = it
                }
                response?.themeBackgroundFileName?.let {
                    getImageBitmap(it, Constants.BACKGROUND_IMAGE)
                    loadBg(it)
                }
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
                binding.pbLoader.toInvisible()
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

                mainMenuViewModel.fetchDateTime(Constants.UA)

                mainMenuViewModel.accountSetupLiveData.value?.data?.let {
                    mainMenuViewModel.setAccountSetupResponseData(accountSetupDataStore, it)
                }

                binding.tvGreeting.text =
                    mainMenuViewModel.accountSetupLiveData.value?.data?.hotelInfo
                val btnListFromApi: List<String>? =
                    mainMenuViewModel.accountSetupLiveData.value?.data?.buttonsList?.map {
                        it.buttonName
                    }
                val btnModelList: List<BtnModel> = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                    btnListFromApi?.contains(it.btnId) == true
                }
                binding.rvMenuButton.layoutManager = GridLayoutManager(this, 4)
                val adapter = MainMenuBtnAdapter { btn ->
                    val bundle = Bundle()
                    bundle.putString("title", btn.title)
                    var intent: Intent? = null
                    when (btn.btnId) {
                        Constants.HOTEL_SERVICES_ID -> {
                            intent = Intent(this, HotelInfoActivity::class.java)
                        }

                        Constants.LOCAL_ATTRACTION_ID -> {
                            intent = Intent(this, LocalAttractionActivity::class.java)
                        }

                        Constants.VOD_ID -> {
                            intent = Intent(this, MoviesActivity::class.java)
                        }

                        Constants.GUEST_SERVICES_ID -> {
                            intent = Intent(this, GuestServiceActivity::class.java)
                        }


                        Constants.SHOWTIMES_ID -> {
                            intent = Intent(this, ShowtimeActivity::class.java)
                        }

                        Constants.APPS_ID -> {
                            startActivity(Intent(this, AppWorldActivity::class.java))
                        }

                        else -> {

                        }
                    }
                    intent?.let {
                        it.putExtras(bundle)
                        startActivity(it)
                    }
                }
                adapter.itemList = btnModelList
                if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)
                }
                binding.rvMenuButton.adapter = adapter
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleHotelServiceResponse(status: Resource<HotelServiceResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                mainMenuViewModel.hotelServiceLiveData.value?.data?.let {
                    mainMenuViewModel.setHotelServicesResponseData(hotelServicesDataStore, it)
                }

                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                mainMenuViewModel.localAttractionLiveData.value?.data?.let {
                    mainMenuViewModel.setLocalAttractionResponseData(localAttractionDataStore, it)
                }

                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleMoviesResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                mainMenuViewModel.moviesLiveData.value?.data?.let {
                    mainMenuViewModel.setMoviesResponseData(moviesDataStore, it)
                }

                binding.pbLoader.toInvisible()
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
        Glide.with(this).load(imgUrl).into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable, transition: Transition<in Drawable?>?
                ) {
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }


    private fun getImageBitmap(imageUrl: String, filename: String) {
        Glide.with(this).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // The 'resource' parameter contains the Bitmap loaded from the imageUrl
                    // Now you can use the bitmap as needed, for example, save it locally
                    saveImageLocally(resource, filename)

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    // Save the image locally
    fun saveImageLocally(bitmap: Bitmap, filename: String) {
        val directory = File(getExternalFilesDir(null), Constants.THEME_DIRECTORY)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, filename)

        try {
            if (!file.exists()) {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
