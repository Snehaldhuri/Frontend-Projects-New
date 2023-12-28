package com.diipl.moviebeam.ui.showtime

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
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
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityShowtimeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.movies.MovieDetailFragment
import com.diipl.moviebeam.ui.movies.MoviesBtnAdapter
import com.diipl.moviebeam.ui.movies.ParentAdapter
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowtimeActivity  : BaseActivity() {

    private lateinit var binding: ActivityShowtimeBinding

    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

    private val list: List<BtnModel> = Constants.MOVIES_PAGE_MENU_BUTTON_LIST

    private val ShowtimeViewModel: ShowtimeViewModel by viewModels()
    private val movieDetailFragment: MovieDetailFragment = MovieDetailFragment()

    override fun observeViewModel() {
        observe(ShowtimeViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(ShowtimeViewModel.themeLiveData, ::handleThemeResponse)
        observe(ShowtimeViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(ShowtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityShowtimeBinding.inflate(layoutInflater)
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
            if (binding.fcvMovieDetail.isVisible) {
                binding.fcvMovieDetail.toInvisible()
                binding.parentRecyclerView.toVisible()
            } else {
                finish()
            }
        }

        val parentRecyclerView: RecyclerView = binding.parentRecyclerView
        parentRecyclerView.setHasFixedSize(true)
        binding.parentRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val cardRecyclerView: RecyclerView = binding.menuRecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(this)


    }
    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val response = ShowtimeViewModel.showtimeLiveData.value?.data
                ShowtimeViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.imgHotelLogo.loadImagesWithGlideExt(it)
                }
                loadBg(ShowtimeViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
//                val genreMap: HashMap<String, MutableList<ShowTimeContent>> = HashMap()
//                response?.shoContentList?.forEach {
//                        if (genreMap[it.genre1] != null) {
//                            genreMap[it.genre1]?.add(it)
//                        } else {
//                            val movieList = mutableListOf<ShowTimeContent>()
//                            movieList.add(it)
//                            genreMap[it.genre1] = movieList
//                        }
//                }
//                val adapter = ShowtimeMenuAdapter(list) { btnId ->
//                    binding.fcvMovieDetail.toInvisible()
//                    binding.parentRecyclerView.toVisible()
//                    when (btnId) {
//
//                        Constants.MOVIE_RENTALS_ID -> {
//                            val parentAdapter = ParentAdapter(onItemClicked = ::onMovieClick)
//
//                            parentAdapter.setMovieList(genreMap)
//                            binding.parentRecyclerView.adapter = parentAdapter
//                        }
//
//                        Constants.FREE_MOVIES_ID -> {
//                            val freeGenreMap: HashMap<String, MutableList<ShowTimeContent>> = HashMap()
//                            response?.shoContentList?.forEach {
//                                if (freeGenreMap[it.genre1] != null) {
//                                    freeGenreMap[it.genre1]?.add(it)
//                                } else {
//                                    val movieList = mutableListOf<ShowTimeContent>()
//                                    movieList.add(it)
//                                    freeGenreMap[it.genre1] = movieList
//                                }
//                            }
//                            val parentAdapter = ParentAdapter(onItemClicked = ::onMovieClick)
//                            parentAdapter.setMovieList(freeGenreMap)
//                            binding.parentRecyclerView.adapter = parentAdapter
//                        }
//
//                        Constants.ADULT_DAY_PASS_ID -> {
//                            val adultGenreMap: HashMap<String, MutableList<ShowTimeContent>> = HashMap()
//                            response?.shoContentList?.forEach {
//                                    if (adultGenreMap[it.genre1] != null) {
//                                        adultGenreMap[it.genre1]?.add(it)
//                                    } else {
//                                        val movieList = mutableListOf<ShowTimeContent>()
//                                        movieList.add(it)
//                                        adultGenreMap[it.genre1] = movieList
//                                    }
//                            }
//                            val parentAdapter = ParentAdapter(onItemClicked = ::onMovieClick)
//                            parentAdapter.setMovieList(adultGenreMap)
//                            binding.parentRecyclerView.adapter = parentAdapter
//                        }
//
//                        Constants.ADULT_ID -> {
//                            val adultGenreMap: HashMap<String, MutableList<ShowTimeContent>> = HashMap()
//                            response?.shoContentList?.forEach {
//                                if (it.genre1 == "Adult") {
//                                    if (adultGenreMap[it.genre1] != null) {
//                                        adultGenreMap[it.genre1]?.add(it)
//                                    } else {
//                                        val movieList = mutableListOf<ShowTimeContent>()
//                                        movieList.add(it)
//                                        adultGenreMap[it.genre1] = movieList
//                                    }
//                                }
//                            }
//                            val parentAdapter = ParentAdapter(onItemClicked = ::onMovieClick)
//                            parentAdapter.setMovieList(adultGenreMap)
//                            binding.parentRecyclerView.adapter = parentAdapter
//                        }
//
//                    }
//                }
//                // TODO Movies Details Logic
//                val transition = supportFragmentManager.beginTransaction()
//                transition.replace(R.id.fcv_movie_detail, movieDetailFragment)
//                transition.commit()
//                binding.fcvMovieDetail.toInvisible()
//                val parentAdapter = ParentAdapter {
//                    movieDetailFragment.setMovieDetails(it)
//                    binding.parentRecyclerView.toInvisible()
//                    binding.fcvMovieDetail.toVisible()
//                }
////                parentAdapter.setMovieList(genreMap)
//                binding.parentRecyclerView.adapter = parentAdapter
//                adapter.setGradientColor(gradientStartColor, gradientEndColor)
//                binding.menuRecyclerView.adapter = adapter
//                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
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

    private fun onMovieClick(movie: ShowTimeContent) {
//        movieDetailFragment.setMovieDetails(movie)
        binding.parentRecyclerView.toInvisible()
        binding.fcvMovieDetail.toVisible()
    }
    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                var temperature = ShowtimeViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.layoutHeader.headerWeatherTime.weather.txtTemperature.text = temperature
                ShowtimeViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.headerWeatherTime.weather.imgWeatherImage.loadImagesWithGlideExt(
                        it
                    )
                }
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                ShowtimeViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                ShowtimeViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                movieDetailFragment.setGradient(getGradient(gradientStartColor, gradientEndColor))
                ShowtimeViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.imgHotelLogo.loadImagesWithGlideExt(it)
                }
                loadBg(ShowtimeViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.headerWeatherTime.txtDate.text =
                    ShowtimeViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.headerWeatherTime.txtTime.text =
                    ShowtimeViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }
}