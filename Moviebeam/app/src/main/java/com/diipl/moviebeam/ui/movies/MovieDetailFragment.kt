package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExtPoster
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

private const val TAG = "MovieDetailFragment"

class MovieDetailFragment : BaseFragment() {

    private val viewModel: MoviesViewModel by activityViewModels()
    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    private lateinit var movie: ContentDto
    private var gradient: GradientDrawable? = null
    private var isCheckedIn = false
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var seekPosition: Long = 0
    private var rentalID = ""

    override fun observeViewModel() {
        observe(viewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
//        observe(viewModel.rentalMovieResponse, ::handleMovieResponse)
        observeToast(viewModel.showToast)
    }

    override fun initViewBinding() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(requireContext())
        viewModel.validateSession(preferenceDataStoreHelper)

        binding.btnWatchTrailer.setOnClickListener {
            apiCall(0, Constants.C_TYPE_TRAILER)
            movie.let { it1 ->
                (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                    it1,
                    true,
                    false,
                    0
                )
            }
        }

        binding.btnRentNow.setOnClickListener {
            if (binding.btnRentNow.text == getString(R.string.watch_free)){
                apiCall(0, Constants.C_TYPE_MOVIE)
                viewModel.insertMovieDetails(RentalMovieResponse(), movie)
                movie.let { it1 ->
                    (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                        it1,
                        false,
                        true,
                        seekPosition
                    )
                }
            } else {
                val data = Gson().toJson(movie)
                startActivity(
                    Intent(requireActivity(), ConfirmRentalActivity::class.java).putExtra(
                        Constants.MOVIE_RENTALS,
                        data
                    )
                )
            }

        }

        binding.btnContinueWatch.setOnClickListener {
            val seekType = if (binding.btnContinueWatch.text.toString() == getString(R.string.watch_now)) 0 else 1
            apiCall(seekType, Constants.C_TYPE_MOVIE)
            movie.let { it1 ->
                (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    seekPosition
                )
            }
        }

        binding.btnWatchFromStart.setOnClickListener {
            apiCall(1, Constants.C_TYPE_MOVIE)
            movie.let { it1 ->
                (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    0
                )
            }
        }

    }

    private fun apiCall(seekType: Int,  cType: String) {
        val request = RentalMovieRequest()
        if (::movie.isInitialized){
            movie.let {
                request.productId = it.productId
                request.releaseID = it.releaseId
                request.price = it.price
                request.contentTypeID = it.contentTypeId
                request.productType = it.releaseTypeId
                request.rentalID = rentalID
                request.ra = 0
                request.cType = cType
                request.seekType = seekType
                request.seek = seekPosition
                viewModel.updateRentalMovieLog(request)
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if (::movie.isInitialized)
            setMovieDetails(movie)

    }

    fun setMovieDetails(movie: ContentDto) {
        this.movie = movie

        viewModel.getRentalMovie(movie.releaseId)

        viewModel.movieData.observe(this) { data ->
            if (data != null) {
                seekPosition = data.currentSeek
                rentalID = data.rentalID.toString()
                if (data.currentSeek <= 0) {
                    binding.btnContinueWatch.text = getString(R.string.watch_now)
                } else {
                    binding.btnContinueWatch.text = getString(R.string.continue_watch)
                }
                binding.btnRentNow.toGone()
                binding.btnWatchTrailer.toGone()
                binding.btnContinueWatch.toVisible()
                binding.btnWatchFromStart.toVisible()
                binding.btnContinueWatch.requestFocus()
            } else {
                seekPosition = 0
                binding.btnContinueWatch.toGone()
                binding.btnWatchFromStart.toGone()
                binding.btnRentNow.toVisible()
                binding.btnWatchTrailer.toVisible()
                binding.btnRentNow.requestFocus()
            }
        }


        val httpStreamingHotelVideoUrl = "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        movie.imagePathPoster = httpStreamingHotelVideoUrl + movie.releaseId + "/" + movie.releaseId + "_P.jpg"
        movie.imagePathPoster.let {
            binding.ivMovieImage.loadImagesWithGlideExtPoster(it)
        }

        binding.tvTitle.text = movie.movieName
        binding.tvHeading.text = movie.headingDetailsNew
        binding.tvSynopsis.text = movie.synopsis
        binding.tvCastTitle.text = "Cast : " + movie.actor
        binding.tvDirectorTitle.text = "Director : " + movie.director
        if (movie.releaseTypeId == Constants.FREE_MOVIE_RELEASE_TYPE_ID) {
            binding.btnRentNow.text = getString(R.string.watch_free)
        } else {
            binding.btnRentNow.text = getString(R.string.rent_now, movie.qos, movie.price.toString())
        }

        binding.btnRentNow.setOnFocusChangeListener(::handleBackClick)
        binding.btnWatchTrailer.setOnFocusChangeListener(::handleBackClick)
        binding.btnContinueWatch.setOnFocusChangeListener(::handleBackClick)
        binding.btnWatchFromStart.setOnFocusChangeListener(::handleBackClick)

    }

    private fun handleValidateSessionResponse(status: Boolean) {
        this.isCheckedIn = status
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    fun setGradient(gradient: GradientDrawable) {
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