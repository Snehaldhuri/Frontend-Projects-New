package com.diipl.moviebeam.ui.showtime

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.movies.MoviesViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExtPoster
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ShowtimeDetailFragment : BaseFragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    private lateinit var show: Detail
    private var gradient: GradientDrawable? = null

    private var position: Int = 0
    private var seekPosition: Long = 0
    private val showtimeViewModel: ShowtimeViewModel by activityViewModels()
    private val viewModel : MoviesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityStack.add(this::class.java.simpleName)
        arguments?.let {
            position = it.getInt("movieReleaseId")
        }

    }

    override fun observeViewModel() {
        observe(showtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }

    override fun initViewBinding() {}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        LoggingService.sendMessageToWebSocket("In ShowtimeDetailPage create ","13")
        binding.layoutMovie.toVisible()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRentNow.setOnClickListener {
            apiCall(0, Constants.C_TYPE_MOVIE)
            viewModel.insertShowDetails(show)
            show.let { it1 ->
                (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    seekPosition
                )
            }
        }
        binding.btnContinueWatch.setOnClickListener {
            val seekType = if (binding.btnContinueWatch.text.toString() == getString(R.string.watch_now)) 0 else 1
            apiCall(seekType, Constants.C_TYPE_MOVIE)
            show.let { it1 ->
                (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    seekPosition
                )
            }
        }

        binding.btnWatchFromStart.setOnClickListener {
            apiCall(1, Constants.C_TYPE_MOVIE)
            show.let { it1 ->
                (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    0
                )
            }
        }
    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {
                binding.loaderView.toVisible()
            }

            is Resource.Success -> {
                val response = showtimeViewModel.showtimeLiveData.value?.data

                val detail = response?.shoGenreList?.get(0)?.detailList?.find { detail ->
                    detail.releaseId == position
                }
                detail?.let { setShowDetails(it) }

                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { showtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::show.isInitialized)
            setShowDetails(show)

    }


    private fun setShowDetails(show: Detail) {
        this.show = show
        binding.btnWatchTrailer.toGone()

        viewModel.getShowData(show.releaseId)
        viewModel.seriesData.observe(this) { data ->
            if (data != null) {
                seekPosition = data.currentSeek

                if (data.currentSeek <= 0) {
                    binding.btnContinueWatch.text = getString(R.string.watch_now)
                } else {
                    binding.btnContinueWatch.text = getString(R.string.continue_watch)
                }

                binding.btnRentNow.toGone()
                binding.btnContinueWatch.toVisible()
                binding.btnWatchFromStart.toVisible()
                binding.btnContinueWatch.requestFocus()
            } else {
                seekPosition = 0
                binding.btnContinueWatch.toGone()
                binding.btnWatchFromStart.toGone()
                binding.btnRentNow.toVisible()
                binding.btnRentNow.requestFocus()
            }
        }

        val httpStreamingHotelVideoUrl = "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        show.imagePathPoster =
            httpStreamingHotelVideoUrl + show.releaseId + "/" + show.releaseId + "_P.jpg"
        show.imagePathPoster.let {
            binding.ivMovieImage.loadImagesWithGlideExtPoster(it)
        }
        binding.ivMovieImage.setBackgroundResource(R.drawable.round_outline_5dp)
        binding.ivMovieImage.clipToOutline = true

        binding.tvTitle.text = show.movieName
        binding.tvHeading.isVisible = false
        binding.tvSynopsis.text = show.synopsis
        binding.tvCastTitle.isVisible = false
        binding.tvDirectorTitle.isVisible = false
        binding.btnRentNow.text = getString(R.string.watch_free)

        val layoutParams = binding.btnRentNow.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.dp_225)
        binding.btnRentNow.layoutParams = layoutParams

        binding.btnRentNow.setOnFocusChangeListener(::handleBackClick)
        binding.btnContinueWatch.setOnFocusChangeListener(::handleBackClick)
        binding.btnWatchFromStart.setOnFocusChangeListener(::handleBackClick)

    }

    private fun apiCall(seekType: Int,  cType: String) {
        val request = RentalMovieRequest()
        if (::show.isInitialized){
            show.let {
                request.productId = it.productId
                request.releaseID = it.releaseId
                request.contentTypeID = it.contentTypeId
                request.productType = Constants.SHOWTIME_RELEASE_TYPE_ID
                request.ra = 0
                request.cType = cType
                request.seekType = seekType
                request.seek = seekPosition
                viewModel.updateRentalMovieLog(request)
            }
        }
    }

    fun setGradient(gradient: GradientDrawable?) {
        this.gradient = gradient
    }

    private fun handleBackClick(view: View, focus: Boolean) {
        if (focus) {
            view.background = gradient
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

}