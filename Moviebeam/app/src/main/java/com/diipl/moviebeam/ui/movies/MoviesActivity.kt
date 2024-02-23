package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityMoviesBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.ADULT_BUTTON
import com.diipl.moviebeam.utils.Constants.ADULT_DAY_PASS_BUTTON
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MoviesActivity"

@AndroidEntryPoint
class MoviesActivity : BaseActivity() {

    private lateinit var binding: ActivityMoviesBinding

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR

    private val list: MutableList<BtnModel> = Constants.MOVIES_PAGE_MENU_BUTTON_LIST

    private val moviesViewModel: MoviesViewModel by viewModels()
    private val movieDetailFragment = MovieDetailFragment()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>
    private var selectedView: View? = null
    private var itemView: View? = null
    private var isRecentView = false
    private var isAdultDayPassPurchased = false

    override fun observeViewModel() {
//        observe(moviesViewModel.recentWatchMovies, ::handleWatchedMovies)
        observe(moviesViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(moviesViewModel.themeLiveData, ::handleThemeResponse)
        observe(moviesViewModel.moviesLiveData, ::handleMoviesServiceResponse)
        observe(moviesViewModel.adultStatus, ::handleAdultResponse)
        observe(moviesViewModel.adultDayPassStatus, ::handleAdultDayPassResponse)

//        moviesViewModel.getWatchedMovies()
        moviesViewModel.getThemeResponseData(themeDataStore)
        moviesViewModel.getWeatherResponseData(weatherDataStore)
        moviesViewModel.getMoviesInfoResponseData(moviesDataStore)

    }

    private fun handleAdultResponse(enabled: Boolean) {
        Log.e(TAG, "handleAdultResponse: $enabled")
        if (enabled) {
            if (!Constants.MOVIES_PAGE_MENU_BUTTON_LIST.contains(ADULT_DAY_PASS_BUTTON)) Constants.MOVIES_PAGE_MENU_BUTTON_LIST.add(
                2,
                ADULT_DAY_PASS_BUTTON
            )
            if (!Constants.MOVIES_PAGE_MENU_BUTTON_LIST.contains(ADULT_BUTTON)) Constants.MOVIES_PAGE_MENU_BUTTON_LIST.add(
                3,
                ADULT_BUTTON
            )
        } else {
            Constants.MOVIES_PAGE_MENU_BUTTON_LIST.remove(ADULT_DAY_PASS_BUTTON)
            Constants.MOVIES_PAGE_MENU_BUTTON_LIST.remove(ADULT_BUTTON)
        }
    }

    private fun handleAdultDayPassResponse(purchased: Boolean) {
        isAdultDayPassPurchased = purchased
        Log.e(TAG, "handleAdultDayPassResponse: $purchased   $isAdultDayPassPurchased")
    }

    override fun initViewBinding() {
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        val view = binding.root
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(applicationContext)

        moviesViewModel.getAdultStatus(preferenceDataStoreHelper)

        binding.btnBack.setOnFocusChangeListener { v, b ->
            if (b) {
                binding.btnBack.background = getGradient(gradientStartColor, gradientEndColor)
            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnBack.setOnClickListener {
            handleBackClick()
        }

        val parentRecyclerView: RecyclerView = binding.parentRecyclerView
        parentRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        parentRecyclerView.layoutManager = layoutManager


        binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY) {
                v.scrollTo(scrollX, scrollY.minus(100))
            }
        })

        val cardRecyclerView: RecyclerView = binding.menuRecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(this)

        val params = binding.recentRecyclerView.layoutParams
//        params.width = getWidthInPercent(applicationContext, 22)
        params.height = getHeightInPercent(applicationContext, 28)


        moviesViewModel.getAllWatchedMovies().observe(this) { data ->
            if (data.isNotEmpty()) {
                val movieList = mutableListOf<ContentDto>()
                data.forEach { model ->
                    model.movieData?.let { movieList.add(it) }
                }

                if (movieList.isNotEmpty()) {
                    val adapter = ChildAdapter(movieList, onItemClicked = { it, view ->
                        onMovieClick(it, view)
                    }, onLeftKey = {
                        if (it) {
                            requestFocus()
                        }
                    })

                    binding.recentRecyclerView.adapter = adapter

                }
            }
        }


    }

    private fun requestFocus() {
        if (!isRecentView) {
            if (itemView != null) {
                val adapter = binding.parentRecyclerView.adapter as ParentAdapter
                binding.parentRecyclerView.post {
                    binding.parentRecyclerView.findContainingItemView(itemView!!)?.requestFocus()
//                    binding.parentRecyclerView.findViewHolderForAdapterPosition(Constants.MOVIE_PARENT_POSITION)?.itemView?.requestFocus()
//                    adapter.updateFocus()
                    itemView = null
                }
            } else {
                selectedView?.let {
                    binding.menuRecyclerView.post {
                        binding.menuRecyclerView.findContainingItemView(it)?.requestFocus()
                    }
                }
            }
        } else {/*binding.recentRecyclerView.post {
                binding.recentRecyclerView.findViewHolderForAdapterPosition(Constants.MOVIE_SELECTED_POSITION)?.itemView?.requestFocus()
            }*/
            selectedView?.let {
                binding.menuRecyclerView.post {
                    binding.menuRecyclerView.findContainingItemView(it)?.requestFocus()
                }
            }
        }
    }

    private fun handleMoviesServiceResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                lifecycleScope.launch {
                    val response = moviesViewModel.moviesLiveData.value?.data
                    moviesViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                        binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                    }
                    loadBg(moviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                    val genreMap: LinkedHashMap<String, MutableList<ContentDto>> = LinkedHashMap()
                    withContext(Dispatchers.IO) {
                        response?.premiumContentList?.forEach {
                            if (it.genre1 != "Adult") {
                                if (genreMap[it.genre1] != null) {
                                    genreMap[it.genre1]?.add(it)
                                } else {
                                    val movieList = mutableListOf<ContentDto>()
                                    movieList.add(it)
                                    genreMap[it.genre1] = movieList
                                }
                                if (it.genre1 != "New Releases") {
                                    if (genreMap[Constants.ALL_PAY_MOVIES] != null) {
                                        genreMap[Constants.ALL_PAY_MOVIES]?.add(it)
                                    } else {
                                        val movieList = mutableListOf<ContentDto>()
                                        movieList.add(it.copy(genre1 = Constants.ALL_PAY_MOVIES))
                                        genreMap[Constants.ALL_PAY_MOVIES] = movieList
                                    }
                                }
                            }
                        }
                    }

                    val sortedGenreMap = genreMap.toList().sortedBy { it.first }.toMap()

                    val adapter = MoviesBtnAdapter(onMoviesMenuItemClicked = { view, btnId ->
                        isRecentView = false
                        binding.fcvMovieDetail.toGone()
                        binding.recentRecyclerView.toGone()
                        binding.parentRecyclerView.toVisible()
                        selectedView = view
                        when (btnId) {
                            Constants.RECENT_WATCH_MOVIE_ID -> {
                                isRecentView = true
                                binding.recentRecyclerView.toVisible()
                                binding.parentRecyclerView.toGone()
                            }

                            Constants.MOVIE_RENTALS_ID -> {
                                val parentAdapter = ParentAdapter(onItemClicked = { it, v ->
                                    onMovieClick(it, v)
                                }, onLeftKey = {
                                    if (it) {
                                        requestFocus()
                                    }
                                })

                                parentAdapter.setMovieList(sortedGenreMap, null, true)
                                binding.parentRecyclerView.adapter = parentAdapter
                            }

                            Constants.FREE_MOVIES_ID -> {
                                val freeGenreMap: HashMap<String, MutableList<ContentDto>> =
                                    HashMap()
                                response?.freeContentList?.forEach {
                                    if (freeGenreMap[it.genre1] != null) {
                                        freeGenreMap[it.genre1]?.add(it)
                                    } else {
                                        val movieList = mutableListOf<ContentDto>()
                                        movieList.add(it)
                                        freeGenreMap[it.genre1] = movieList
                                    }
                                }
                                val parentAdapter = ParentAdapter(onItemClicked = { it, v ->
                                    onMovieClick(it, v)
                                }, onLeftKey = {
                                    if (it) {
                                        requestFocus()
                                    }
                                })
                                parentAdapter.setMovieList(freeGenreMap, null, true)
                                binding.parentRecyclerView.adapter = parentAdapter
                            }

                            Constants.ADULT_DAY_PASS_ID -> {
                                if (isAdultDayPassPurchased) {
                                    val adultGenreMap: HashMap<String, MutableList<ContentDto>> =
                                        HashMap()
                                    response?.premiumContentList?.forEach {
                                        if (it.genre1 == "Adult") {
                                            if (adultGenreMap[it.genre1] != null) {
                                                adultGenreMap[it.genre1]?.add(it)
                                            } else {
                                                val movieList = mutableListOf<ContentDto>()
                                                movieList.add(it)
                                                adultGenreMap[it.genre1] = movieList
                                            }
                                        }
                                    }

                                    val parentAdapter = ParentAdapter(onItemClicked = { it, v ->
                                        onMovieClick(it, v)
                                    }, onLeftKey = {
                                        if (it) {
                                            requestFocus()
                                        }
                                    })
                                    parentAdapter.setMovieList(adultGenreMap, null, true)
                                    binding.parentRecyclerView.adapter = parentAdapter
                                } else {
                                    startActivity(
                                        Intent(
                                            this@MoviesActivity,
                                            ConfirmRentalActivity::class.java
                                        ).putExtra("price", response?.adultDayPassPrice.toString())
                                    )
                                }
                            }

                            Constants.ADULT_ID -> {
                                val adultGenreMap: HashMap<String, MutableList<ContentDto>> =
                                    HashMap()
                                response?.premiumContentList?.forEach {
                                    if (it.genre1 == "Adult") {
                                        if (adultGenreMap[it.genre1] != null) {
                                            adultGenreMap[it.genre1]?.add(it)
                                        } else {
                                            val movieList = mutableListOf<ContentDto>()
                                            movieList.add(it)
                                            adultGenreMap[it.genre1] = movieList
                                        }
                                    }
                                }
                                val parentAdapter = ParentAdapter(onItemClicked = { it, v ->
                                    onMovieClick(it, v)
                                }, onLeftKey = {
                                    if (it) {
                                        requestFocus()
                                    }
                                })
                                parentAdapter.setMovieList(adultGenreMap, null, true)
                                binding.parentRecyclerView.adapter = parentAdapter
                            }

                        }
                    }, onRightKeyPressed = {
                        if (binding.fcvMovieDetail.isVisible) {
                            binding.fcvMovieDetail.postDelayed({
                                val btnRentNow: Button? =
                                    binding.fcvMovieDetail.findViewById(R.id.btn_rent_now)
                                btnRentNow?.requestFocus()
                            }, 50)
                        }
                    })

                    adapter.submitList(list)

                    // TODO Movies Details Logic
                    val transition = supportFragmentManager.beginTransaction()
                    transition.replace(R.id.fcv_movie_detail, movieDetailFragment)
                    transition.commit()
                    binding.fcvMovieDetail.toInvisible()
                    val parentAdapter = ParentAdapter(onItemClicked = { it, v ->
                        onMovieClick(it, v)
                    }, onLeftKey = {
                        if (it) {
                            requestFocus()
                        }
                    })
                    parentAdapter.setMovieList(genreMap, null, true)
                    binding.parentRecyclerView.adapter = parentAdapter
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)
                    binding.menuRecyclerView.adapter = adapter
                    delay(200)
                    binding.loaderView.toGone()
                }
            }

            else -> {
                status.errorCode?.let { moviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }


    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    moviesViewModel.weatherLiveData.value?.data?.tempCondition
                moviesViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                        it
                    )
                }
            }

            else -> {
                status.errorCode?.let { moviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                moviesViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                moviesViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                movieDetailFragment.setGradient(getGradient(gradientStartColor, gradientEndColor))
                moviesViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                }
                loadBg(moviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
            }

            else -> {
                status.errorCode?.let { moviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl).into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable, transition: Transition<in Drawable?>?
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

    private fun onMovieClick(movie: ContentDto, view: View) {
        itemView = view
        binding.parentRecyclerView.toGone()
        binding.recentRecyclerView.toGone()
        binding.fcvMovieDetail.toVisible()
        movieDetailFragment.setMovieDetails(movie)
    }

    fun gotoExoPlayerActivity(
        movieDetails: ContentDto, isTrailer: Boolean, isContent: Boolean, seekPosition: Long
    ) {

        val bundle = Bundle()

        bundle.putString(Constants.MOVIE_DETAILS, movieDetails.toJson())
        bundle.putBoolean(Constants.IS_TRAILER, isTrailer)
        bundle.putBoolean(Constants.IS_CONTENT, isContent)
        bundle.putLong(Constants.IS_CONTINUE, seekPosition)

        val intent = Intent(this, ExoPlayerActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun handleBackClick() {
        if (binding.fcvMovieDetail.isVisible) {
            binding.fcvMovieDetail.toGone()
            requestFocus()
            if (isRecentView) {
                binding.parentRecyclerView.toGone()
                binding.recentRecyclerView.toVisible()
            } else {
                binding.recentRecyclerView.toGone()
                binding.parentRecyclerView.toVisible()
            }
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                handleBackClick()
            }
        }
        return false
    }

}