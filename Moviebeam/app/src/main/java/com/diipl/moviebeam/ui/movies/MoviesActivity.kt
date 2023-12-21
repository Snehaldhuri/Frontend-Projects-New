package com.diipl.moviebeam.ui.movies

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityMoviesBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.localattraction.LaCardAdapter
import com.diipl.moviebeam.ui.localattraction.LocalAttractionAdapter
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class MoviesActivity : BaseActivity()  {

    private lateinit var binding: ActivityMoviesBinding
    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

    private val list = mutableListOf(
        BtnModel(Constants.MOVIE_RENTALS_ID, R.drawable.movie_rentals_img, Constants.MOVIE_RENTALS_ID),
        BtnModel(Constants.FREE_VOD_ID, R.drawable.video_on_demand_icon, Constants.FREE_VOD_ID),
        BtnModel(Constants.ADULT_DAY_PASS_ID, R.drawable.adult_day_pass, Constants.ADULT_DAY_PASS_ID),
        BtnModel(Constants.ADULT_ID, R.drawable.adult, Constants.ADULT_ID),
    )
    private val MoviesViewModel: MoviesViewModel by viewModels()

    override fun observeViewModel() {
        observe(MoviesViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(MoviesViewModel.themeLiveData, ::handleThemeResponse)
        observe(MoviesViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(MoviesViewModel.moviesLiveData, ::handleMoviesServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityMoviesBinding.inflate(layoutInflater)
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

        val cardRecyclerView: RecyclerView = binding.menuRecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.menuRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
    private fun handleMoviesServiceResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val response = MoviesViewModel.moviesLiveData.value?.data
                Glide.with(this)
                    .load(MoviesViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.layoutHeader.imgHotelLogo)
                loadBg(MoviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                val adapter = MoviesBtnAdapter(list){

                    val cardAdapter = MoviesCardAdapter {
//                        Log.i("Movies5", "handleWeatherResponse: $it")
                    }
//                    cardAdapter.setList(it)
//                    cardAdapter.set(
//                        getGradient(
//                            gradientStartColor,
//                            gradientEndColor
//                        )
//                    )
                    binding.recyclerView.adapter = cardAdapter
                }

                adapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.menuRecyclerView.adapter = adapter

                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                var temperature = MoviesViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.layoutHeader.headerWeatherTime.weather.txtTemperature.text = temperature
                Glide.with(this)
                    .load(MoviesViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud)
                    .into(binding.layoutHeader.headerWeatherTime.weather.imgWeatherImage)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                MoviesViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                MoviesViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                Glide.with(this)
                    .load(MoviesViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.layoutHeader.imgHotelLogo)
                loadBg(MoviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.headerWeatherTime.txtDate.text = MoviesViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.headerWeatherTime.txtTime.text = MoviesViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
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