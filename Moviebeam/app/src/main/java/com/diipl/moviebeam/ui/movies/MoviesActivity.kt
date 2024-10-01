package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.os.Bundle
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
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityMoviesBinding
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.dialogs.AdultContentDialog
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.ADULT_CONTENT_DISABLED
import com.diipl.moviebeam.utils.Constants.ADULT_LOCKED
import com.diipl.moviebeam.utils.Constants.ADULT_MCD_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_MAIN
import com.diipl.moviebeam.utils.Constants.C_TYPE_MOVIE
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
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

@AndroidEntryPoint
class MoviesActivity : BaseActivity() {

    private lateinit var binding: ActivityMoviesBinding

    private val list: MutableList<BtnModel> = Constants.MOVIES_PAGE_MENU_BUTTON_LIST

    private val moviesViewModel: MoviesViewModel by viewModels()
    private val movieDetailFragment = MovieDetailFragment()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var moviesDataStore: DataStore<MoviesResponse>

    @Inject
    lateinit var preference: SharedPreference

    private var selectedView: View? = null
    private var itemView: View? = null
    private var isRecentView = false
    private var isAdultDayPassPurchased = false
    private var isUserCheckedIn = false
    private lateinit var adultResponse: MoviesResponse

    override fun observeViewModel() {
        observe(moviesViewModel.moviesLiveData, ::handleMoviesServiceResponse)
        observe(moviesViewModel.adultDayPassStatus, ::handleAdultDayPassResponse)

    }

    private fun handleAdultDayPassResponse(purchased: Boolean) {
        isAdultDayPassPurchased = purchased
    }

    override fun initViewBinding() {
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(applicationContext)

        moviesViewModel.getAdultStatus(preferenceDataStoreHelper)
        moviesViewModel.getMoviesInfoResponseData(moviesDataStore)

        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener {
            handleBackClick()
        }

        binding.btnBack.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    binding.menuRecyclerView.getChildAt(0).requestFocus()
                    return@setOnKeyListener true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> return@setOnKeyListener true
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    binding.menuRecyclerView.getChildAt(0).requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
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


        try {
            LoggingService.sendMessageToWebSocket("In MoviesMain activity", getCurrentPanelNumber())
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "In MoviesMain activity onResume: ${e.message}",
                getCurrentPanelNumber()
            )
        }

    }

    override fun onResume() {
        super.onResume()

        binding.dialogContainer.toGone()

        lifecycleScope.launch {
            val isFree = preferenceDataStoreHelper.getFirstPreference(
                PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS,
                false
            )
            if (isFree && activityStack.contains(C_TYPE_MOVIE)) {
                if (preference.isAdultPassCodeEmpty)
                    setAdultData(adultResponse)
                else if (!preference.isAdultLocked) setAdultData(adultResponse) else openACDDialog(
                    ADULT_LOCKED
                )
                activityStack.remove(C_TYPE_MOVIE)
            }

            binding.root.loadBg()
            binding.layoutHeader.ivHotelLogo.loadLogo()
            binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE

        }

    }

    override fun onStart() {
        super.onStart()

        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE

        isUserCheckedIn =
            (GuestDetails.SESSION_ID.isNotEmpty() && GuestDetails.SESSION_ID != "null")

        if (isUserCheckedIn) {
            if (!preference.isMainAdultMCW) {
                if (!preference.isAdultPassCodeEmpty)
                    openACDDialog(ADULT_MCW_MAIN)
                else
                    if (!preference.isAdultMCD)
                        openACDDialog(ADULT_MCD_BTN)
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
                    status.data?.let { response ->
                        updateMoviesCount(
                            moviesCount = response.freeContentList.size.plus(response.premiumContentList.size),
                            cListVersion = response.version
                        )
                        adultResponse = response
                        val genreMap: LinkedHashMap<String, MutableList<ContentDto>> =
                            LinkedHashMap()
                        withContext(Dispatchers.IO) {
                            response.premiumContentList.forEach {
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

                        val adapter =
                            MoviesBtnAdapter(onMoviesMenuItemClicked = { view, btnId ->
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
                                        val parentAdapter =
                                            ParentAdapter(onItemClicked = { it, v ->
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
                                        response.freeContentList.forEach {
                                            if (freeGenreMap[it.genre1] != null) {
                                                freeGenreMap[it.genre1]?.add(it)
                                            } else {
                                                val movieList = mutableListOf<ContentDto>()
                                                movieList.add(it)
                                                freeGenreMap[it.genre1] = movieList
                                            }
                                        }
                                        val parentAdapter =
                                            ParentAdapter(onItemClicked = { it, v ->
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
                                        if (isUserCheckedIn) {
                                            if (!preference.isAdultContentEnabled) {
                                                openACDDialog(ADULT_CONTENT_DISABLED)
                                            } else {
                                                if (!preference.isAdultPassCodeEmpty) {
                                                    if (!preference.isAdultMCD) {
                                                        openACDDialog(ADULT_MCD_BTN)
                                                    }
                                                    if (preference.isAdultLocked) {
                                                        openACDDialog(ADULT_LOCKED)
                                                    }
//                                                } else {
                                                    if (!preference.isBtnAdultMCW) {
                                                        openACDDialog(ADULT_MCW_BTN)
                                                    }
                                                }

                                                if (preference.isBtnAdultMCW && !isAdultDayPassPurchased && isUserCheckedIn) {
                                                    startActivity(
                                                        Intent(
                                                            this@MoviesActivity,
                                                            ConfirmRentalActivity::class.java
                                                        ).putExtra(
                                                            "price",
                                                            response.adultDayPassPrice.toString()
                                                        )
                                                    )
                                                    return@MoviesBtnAdapter
                                                } else if (isAdultDayPassPurchased) {
                                                    if (preference.isAdultPassCodeEmpty)
                                                        setAdultData(adultResponse)
                                                    else if (!preference.isAdultLocked)
                                                        setAdultData(adultResponse)
                                                    else openACDDialog(ADULT_LOCKED)
                                                }
                                            }
                                        } else {
                                            if (!preference.isBtnAdultMCW)
                                                openACDDialog(ADULT_MCW_BTN)
//                                            setAdultData(response)
                                            return@MoviesBtnAdapter
                                        }
                                    }

                                    Constants.ADULT_ID -> {
                                        if (isUserCheckedIn) {
                                            if (!preference.isAdultContentEnabled)
                                                openACDDialog(ADULT_CONTENT_DISABLED)
                                            else {
                                                if (!preference.isAdultPassCodeEmpty) {
                                                    if (!preference.isAdultMCD)
                                                        openACDDialog(ADULT_MCD_BTN)
                                                    else if (preference.isAdultLocked)
                                                        openACDDialog(ADULT_LOCKED)
                                                } else {
                                                    if (!preference.isBtnAdultMCW)
                                                        openACDDialog(ADULT_MCW_BTN)
                                                }
                                                if (isAdultDayPassPurchased) {
                                                    if (preference.isAdultPassCodeEmpty)
                                                        setAdultData(adultResponse)
                                                    else if (!preference.isAdultLocked) setAdultData(
                                                        adultResponse
                                                    ) else openACDDialog(ADULT_LOCKED)
                                                } else setAdultData(adultResponse)
                                            }
                                        } else {
                                            if (!preference.isBtnAdultMCW)
                                                openACDDialog(ADULT_MCW_BTN)
                                            setAdultData(response)
                                        }
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
                        binding.menuRecyclerView.adapter = adapter
                        delay(200)
                        binding.loaderView.toGone()
                    }
                }
            }

            else -> {
                status.errorCode?.let { moviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun setAdultData(response: MoviesResponse?) {
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

        val parentAdapter = ParentAdapter(onItemClicked = { i, v ->
            onMovieClick(i, v)
        }, onLeftKey = {
            if (it) {
                requestFocus()
            }
        })
        parentAdapter.setMovieList(adultGenreMap, null, true)
        binding.parentRecyclerView.adapter = parentAdapter
    }

    override fun onBackPressed() {

    }

    private fun openACDDialog(viewType: Int) {
        binding.dialogContainer.toVisible()
        val dialog = AdultContentDialog(viewType) { i ->
            binding.dialogContainer.toGone()
            when (i) {
                1 -> {
                    if (viewType == ADULT_LOCKED) {
                        setAdultData(adultResponse)
                    }
                }
            }
        }
        dialog.show(supportFragmentManager, null)
    }

    private fun onMovieClick(movie: ContentDto, view: View) {
        itemView = view
        binding.parentRecyclerView.toGone()
        binding.recentRecyclerView.toGone()
        binding.fcvMovieDetail.toVisible()
        movieDetailFragment.movie = null
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

    fun handleBackClick() {
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
            activityStack.add(this::class.java.simpleName)
        } else {
            finish()
        }
    }

    private fun updateMoviesCount(moviesCount: Int? = null, cListVersion: String? = null) {
        lifecycleScope.launch {
            moviesCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.MOVIES_COUNT_KEY,
                    it
                )
            }
            cListVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.C_LIST_VERSION_KEY,
                    it
                )
            }
        }
    }

}