package com.diipl.moviebeam.ui.showtime

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.datastore.core.DataStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.ActivityShowtimeBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.ui.movies.MoviesViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowtimeActivity : BaseActivity() {

    private lateinit var binding: ActivityShowtimeBinding

    @Inject
    lateinit var showtimeDataStore: DataStore<ShowTimeResponse>

    private var gradient: GradientDrawable? = null

    private val list: List<BtnModel> = Constants.SHOWTIME_PAGE_MENU_BUTTON_LIST

    private val showtimeViewModel: ShowtimeViewModel by viewModels()
    private val moviesViewModel: MoviesViewModel by viewModels()
    private var selectedView: View? = null

    override fun observeViewModel() {
        observe(showtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityShowtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            fetchDetailsFromBundle()
            fetchDataFromDataStore()
            binding.btnBack.setOnFocusChangeListener { view, isFocused ->
                if (isFocused) {
                    view.background = gradient
                } else {
                    view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
            binding.btnBack.setOnClickListener {
                handleBackClick()
            }

            val parentRecyclerView: RecyclerView = binding.parentRecyclerView
            parentRecyclerView.setHasFixedSize(true)
            binding.parentRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY < oldScrollY) {
                    v.scrollTo(scrollX, scrollY.minus(100))
                }
            })

            val cardRecyclerView: RecyclerView = binding.menuRecyclerView
            cardRecyclerView.layoutManager = LinearLayoutManager(this)
            LoggingService.sendMessageToWebSocket("In ShowtimeMainPage activity", "12")
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In ShowtimeMainPage activity onCreate: ${e.message}",
                "12"
            )
        }

    }

    private fun requestFocus() {
        selectedView?.let {
            binding.menuRecyclerView.post {
                binding.menuRecyclerView.findContainingItemView(it)?.requestFocus()
            }
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                status.data?.let { response ->
                    Constants.SHOWS_COUNT = response.shoContentList.size
                    val showTimeGenreMap: Map<String, List<Detail>> =
                        response.shoGenreList.associate { genre ->
                            genre.name to genre.detailList
                        }

                    val adapter = ShowtimeMenuAdapter(list,
                        onMoviesMenuItemClicked = { view, btnId ->
                            binding.fcvMovieDetail.toInvisible()
                            binding.parentRecyclerView.toVisible()
                            selectedView = view
                            when (btnId) {
                                Constants.ALL_SHOWS_ID -> {
                                    val showtimeParentAdapter =
                                        ShowtimeParentAdapter(onItemClicked = ::onShowsClick) {
                                            if (it) {
                                                requestFocus()
                                            }
                                        }
                                    showtimeParentAdapter.setShowsList(showTimeGenreMap)
                                    binding.parentRecyclerView.adapter = showtimeParentAdapter
                                }

                                Constants.SHO_SPORTS_ID,
                                Constants.SHO_SERIES_ID,
                                Constants.SHO_DOCS_ID -> {
                                    val shoSportsGenre =
                                        response.shoGenreList?.find { it.name == getGenreName(btnId) }

                                    val showTimeGenreMap: Map<String, List<Detail>> =
                                        shoSportsGenre?.let {
                                            mapOf(it.name to it.detailList)
                                        } ?: emptyMap()

                                    val showtimeParentAdapter =
                                        ShowtimeParentAdapter(onItemClicked = ::onShowsClick) {
                                            if (it) {
                                                requestFocus()
                                            }
                                        }
                                    showtimeParentAdapter.setShowsList(showTimeGenreMap)
                                    binding.parentRecyclerView.adapter = showtimeParentAdapter
                                }
                            }
                        },
                        onRightKeyPressed = {
                            if (binding.fcvMovieDetail.isVisible) {
                                binding.fcvMovieDetail.post {
                                    binding.fcvMovieDetail.findViewById<Spinner>(R.id.btn_season_list)
                                        ?.requestFocus()
                                }
                                binding.fcvMovieDetail.post {
                                    binding.fcvMovieDetail.findViewById<Button>(R.id.btn_rent_now)
                                        ?.requestFocus()
                                }
                            }
                        }
                    )

                    binding.fcvMovieDetail.toInvisible()

                    val showtimeParentAdapter =
                        ShowtimeParentAdapter(onItemClicked = ::onShowsClick) {
                            if (it) {
                                requestFocus()
                            }
                        }

                    showtimeParentAdapter.setShowsList(showTimeGenreMap)
                    binding.parentRecyclerView.adapter = showtimeParentAdapter
                    adapter.setGradient(gradient)
                    binding.menuRecyclerView.adapter = adapter
                    binding.loaderView.toInvisible()
                }
            }

            else -> {
                status.errorCode?.let { showtimeViewModel.showToastMessage(getString(it)) }
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

    private fun getGradient(startColor: String?, endColor: String?): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun onShowsClick(shows: Detail, position: Int) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            if (shows.episodesPresent) {
                val bundle = Bundle()
                bundle.putInt("movieReleaseId", shows.releaseId)
                val fragment = ShowtimeSeasonFragment()
                fragment.arguments = bundle
                fragment.setGradient(gradient)
                transaction.replace(R.id.fcv_movie_detail, fragment)
            } else {
                val bundle = Bundle()
                bundle.putInt("movieReleaseId", shows.releaseId)
                val fragment = ShowtimeDetailFragment()
                fragment.arguments = bundle
                fragment.setGradient(gradient)
                transaction.replace(R.id.fcv_movie_detail, fragment)
            }
            binding.parentRecyclerView.toInvisible()
            binding.fcvMovieDetail.toVisible()
            transaction.commit()
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In ShowtimeMainPage activity onShowsClick: ${e.message}",
                "12"
            )
        }
    }

    fun gotoExoPlayerActivity(
        movieDetails: Detail, isTrailer: Boolean, isContent: Boolean,
        seekPosition: Long
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.RELEASE_ID, "ShowTime")
        bundle.putString(Constants.SHOW_DETAILS, movieDetails.toJson())
        bundle.putBoolean(Constants.IS_TRAILER, isTrailer)
        bundle.putBoolean(Constants.IS_CONTENT, isContent)
        bundle.putLong(Constants.IS_CONTINUE, seekPosition)

        val intent = Intent(this, ExoPlayerActivity::class.java)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    private fun handleBackClick() {
        if (binding.fcvMovieDetail.isVisible) {
            binding.fcvMovieDetail.toInvisible()
            binding.parentRecyclerView.toVisible()
            activityStack.add(this::class.java.simpleName)
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

    private fun fetchDetailsFromBundle() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        intent.extras?.let {
            gradient =
                getGradient(it.getString("gradientStartColor"), it.getString("gradientEndColor"))
        }
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        loadBg(intent.extras?.getString("themeBackgroundFileName"))
    }

    private fun fetchDataFromDataStore() {
        showtimeViewModel.getShowtimeResponseData(showtimeDataStore)
    }

}


