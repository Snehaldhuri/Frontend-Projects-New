package com.diipl.moviebeam.ui.showtime

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Spinner
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
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityShowtimeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowtimeActivity  : BaseActivity() {

    private lateinit var binding: ActivityShowtimeBinding

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR

    private val list: List<BtnModel> = Constants.SHOWTIME_PAGE_MENU_BUTTON_LIST

    private val ShowtimeViewModel: ShowtimeViewModel by viewModels()
    private val ShowtimeDetailFragment: ShowtimeDetailFragment = ShowtimeDetailFragment()

    private var selectedHeaderItemPosition = 0
    override fun observeViewModel() {
        observe(ShowtimeViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(ShowtimeViewModel.themeLiveData, ::handleThemeResponse)
        observe(ShowtimeViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(ShowtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }
    override fun initViewBinding() {
        binding = ActivityShowtimeBinding.inflate(layoutInflater)
        binding.layoutHeader.tvTitle.setText("Showtime")
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
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                }
                loadBg(ShowtimeViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                val showTimeGenreMap: Map<String, List<Detail>> = response?.shoGenreList?.associate { genre ->
                    genre.name to genre.detailList
                } ?: emptyMap()

                val adapter = ShowtimeMenuAdapter(list,
                    onMoviesMenuItemClicked = { btnId ->
                        binding.fcvMovieDetail.toInvisible()
                        binding.parentRecyclerView.toVisible()

                        when (btnId) {
                            Constants.ALL_SHOWS_ID -> {
                                val showtimeParentAdapter = ShowtimeParentAdapter(onItemClicked = ::onShowsClick)
                                showtimeParentAdapter.setShowsList(showTimeGenreMap)
                                binding.parentRecyclerView.adapter = showtimeParentAdapter
                            }

                            Constants.SHO_SPORTS_ID,
                            Constants.SHO_SERIES_ID,
                            Constants.SHO_DOCS_ID -> {
                                val shoSportsGenre = response?.shoGenreList?.find { it.name == getGenreName(btnId) }

                                val showTimeGenreMap: Map<String, List<Detail>> = shoSportsGenre?.let {
                                    mapOf(it.name to it.detailList)
                                } ?: emptyMap()

                                val showtimeParentAdapter = ShowtimeParentAdapter(onItemClicked = ::onShowsClick)
                                showtimeParentAdapter.setShowsList(showTimeGenreMap)
                                binding.parentRecyclerView.adapter = showtimeParentAdapter
                            }
                        }
                    },
                    onRightKeyPressed = {
                        if (binding.fcvMovieDetail.isVisible) {
                            binding.fcvMovieDetail.requestFocus()
                            binding.fcvMovieDetail.postDelayed({
                                val btnSeasonList: Spinner? = binding.fcvMovieDetail.findViewById(R.id.btn_season_list)
                                btnSeasonList?.requestFocus()
                            }, 80)
                        }
                    }
                )



                binding.fcvMovieDetail.toInvisible()

                val showtimeParentAdapter =
                    ShowtimeParentAdapter(onItemClicked = ::onShowsClick)

                showtimeParentAdapter.setShowsList(showTimeGenreMap)
                binding.parentRecyclerView.adapter = showtimeParentAdapter
                adapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.menuRecyclerView.adapter = adapter
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    private fun getGenreName(btnId: String): String {
        return when (btnId) {
            Constants.SHO_SPORTS_ID -> "SHO Sports"
            Constants.SHO_SERIES_ID -> "SHO Series"
            Constants.SHO_DOCS_ID -> "SHO Docs"
            else -> ""
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

    private fun onShowsClick(shows: Detail,position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        if (shows.episodesPresent==true) {

            val bundle = Bundle()
            bundle.putInt("movieReleaseId", shows.releaseId)
            val fragment = ShowtimeSeasonFragment()
            fragment.arguments = bundle
            fragment.setGradient(getGradient(gradientStartColor, gradientEndColor))
            transaction.replace(R.id.fcv_movie_detail, fragment)
        }
        else{
            val bundle = Bundle()
            bundle.putInt("movieReleaseId", shows.releaseId)
            val fragment = ShowtimeDetailFragment()
            fragment.arguments = bundle
            fragment.setGradient(getGradient(gradientStartColor, gradientEndColor))
            transaction.replace(R.id.fcv_movie_detail, fragment)
        }
        binding.parentRecyclerView.toInvisible()
        binding.fcvMovieDetail.toVisible()
        transaction.commit()
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
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text = temperature
                ShowtimeViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
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
                ShowtimeDetailFragment.setGradient(getGradient(gradientStartColor, gradientEndColor))
                ShowtimeViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
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
                binding.layoutHeader.layoutWeatherTime.tvDate.text =
                    ShowtimeViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.layoutWeatherTime.tvTime.text =
                    ShowtimeViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    fun gotoExoPlayerActivity(movieDetails: Detail,isTrailer:Boolean ,isContent:Boolean) {
//        val intent = Intent(this, ExoPlayerActivity::class.java)
//        intent.putExtra(Constants.TRAILER_URL, movieDetails.videoPath)

        val bundle = Bundle()
        bundle.putString(Constants.RELEASE_ID,(movieDetails.releaseId).toString())
        bundle.putBoolean(Constants.IS_TRAILER,isTrailer)
        bundle.putBoolean(Constants.IS_CONTENT,isContent)

        val intent = Intent(this, ExoPlayerActivity::class.java)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}


