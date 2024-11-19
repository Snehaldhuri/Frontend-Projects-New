package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtPoster
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

private const val TAG = "MovieDetailFragment"

class MovieDetailFragment : BaseFragment() {

    private val viewModel: MoviesViewModel by activityViewModels()
    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    var movie: ContentDto? = null
    private var isCheckedIn = false

    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(requireContext())
    }

    private var ua = ""

    private var seekPosition: Long = 0
    private var rentalID = ""
    private var isAdultDayPassPurchased = false

    override fun observeViewModel() {
        observe(viewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(viewModel.adultDayPassStatus, ::handleAdultPassResponse)
        observeToast(viewModel.showToast)
    }

    private fun handleAdultPassResponse(purchased: Boolean) {
        isAdultDayPassPurchased = purchased
    }

    override fun initViewBinding() {
        initializeDatastoreParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        activityStack.add(this::class.java.simpleName)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.validateSession(preferenceDataStoreHelper)

        viewModel.getAdultStatus(preferenceDataStoreHelper)

        binding.btnWatchTrailer.setOnClickListener {
            apiCall(0, Constants.C_TYPE_TRAILER)
            movie?.let { it1 ->
                (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                    it1,
                    true,
                    false,
                    0
                )
            }
        }

        binding.btnRentNow.setOnClickListener {
            if (binding.btnRentNow.text == getString(R.string.watch_free)) {
                apiCall(0, Constants.C_TYPE_MOVIE)
                movie?.let { it1 ->
                    viewModel.insertMovieDetails(RentalMovieResponse(), it1)
                    (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                        it1,
                        false,
                        true,
                        seekPosition
                    )
                }
            } else {
                startActivity(
                    Intent(requireActivity(), ConfirmRentalActivity::class.java).putExtra(
                        Constants.MOVIE_RENTALS,
                        movie.toJson()
                    )
                )
            }
        }
        binding.btnAdultPlay.setOnClickListener {
            if (binding.btnAdultPlay.text == getString(R.string.watch_free) || binding.btnAdultPlay.text == getString(
                    R.string.watch_now
                )
                || binding.btnAdultPlay.text == getString(R.string.continue_watch)
            ) {
                apiCall(0, Constants.C_TYPE_MOVIE)
                movie?.let { it1 ->
                    viewModel.insertMovieDetails(RentalMovieResponse(), it1)
                    (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                        it1,
                        false,
                        true,
                        seekPosition
                    )
                }
            } else {
                startActivity(
                    Intent(requireActivity(), ConfirmRentalActivity::class.java).putExtra(
                        Constants.MOVIE_RENTALS,
                        movie.toJson()
                    )
                )
            }
        }

        binding.btnContinueWatch.setOnClickListener {
            val seekType =
                if (binding.btnContinueWatch.text.toString() == getString(R.string.watch_free)) 0 else 1
            apiCall(seekType, Constants.C_TYPE_MOVIE)
            movie?.let { it1 ->
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
            viewModel.insertMovieDetails(RentalMovieResponse(), movie!!)
            movie?.let { it1 ->
                (activity as MoviesActivity?)?.gotoExoPlayerActivity(
                    it1,
                    false,
                    true,
                    0
                )
            }
        }

    }

    private fun apiCall(seekType: Int, cType: String) {
        val request = RentalMovieRequest()
        movie?.let {
            request.UA = ua
            request.productId = it.productId
            request.releaseID = it.releaseId
            request.price = it.price
            request.contentTypeID = it.contentTypeId
            request.productType = it.releaseTypeId
            request.rentalID = rentalID
            request.ra = 0
            request.cType = cType
            request.seekType = seekType.toLong()
            request.seek = seekPosition
            viewModel.updateRentalMovieLog(request)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnRentNow.setOnFocusChangeListener(::handleFocus)
        binding.btnWatchTrailer.setOnFocusChangeListener(::handleFocus)
        binding.btnContinueWatch.setOnFocusChangeListener(::handleFocus)
        binding.btnWatchFromStart.setOnFocusChangeListener(::handleFocus)
        binding.btnAdultPlay.setOnFocusChangeListener(::handleFocus)

        if (movie != null)
            setMovieDetails(movie!!)

    }

    fun setMovieDetails(content: ContentDto) {
        viewModel.getRentalMovie(content.releaseId)

        viewModel.movieData.observe(this) { data ->
            movie = if (data != null) data.movieData!! else content
            updateUI(movie!!, data)
            binding.root.invalidate()
        }

    }

    private fun updateUI(content: ContentDto, data: RentalMovieModel?) {
        Log.e(TAG, "updateUI: ${data?.currentSeek ?: "-1"} == $content")

        binding.btnAdultPlay.toGone()

        when (content.releaseTypeId) {
            Constants.FREE_MOVIE_RELEASE_TYPE_ID -> {
//                if (content.genre1 == getString(R.string.adult)) {
//                    binding.btnAdultPlay.text = getString(R.string.watch_free)
//                    binding.btnAdultPlay.toVisible()
//                    binding.btnAdultPlay.requestFocus()
//                } else {
                binding.btnRentNow.text = getString(R.string.watch_free)
                binding.layoutMovie.toVisible()
                binding.btnRentNow.requestFocus()
                updateBtn(data)
//                }
            }

            Constants.PAID_MOVIE_RELEASE_TYPE_ID -> {
                binding.btnRentNow.text =
                    getString(R.string.rent_now, content.qos, content.price.toString())
                if (content.genre1 == getString(R.string.adult)) {
                    binding.layoutMovie.toGone()
                    binding.btnAdultPlay.toVisible()
                    if (isAdultDayPassPurchased) {
                        binding.btnAdultPlay.text = getString(R.string.watch_free)
                    } else {
                        binding.btnAdultPlay.text =
                            getString(R.string.rent_now, content.qos, content.price.toString())
                    }
                    if (data != null) {
                        seekPosition = data.currentSeek
                        rentalID = if (data.rentalID == 0) "" else data.rentalID.toString()
                        if (data.currentSeek <= 0) {
                            binding.btnAdultPlay.text = getString(R.string.watch_free)
                        } else {
                            binding.btnAdultPlay.text = getString(R.string.continue_watch)
                        }
                    } else {
                        seekPosition = 0
                    }
                    binding.btnAdultPlay.requestFocus()
                } else {
                    binding.btnAdultPlay.toGone()
                    binding.layoutMovie.toVisible()
                    updateBtn(data)
                }

            }
        }

        val httpStreamingHotelVideoUrl = "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        val url =
            httpStreamingHotelVideoUrl + content.releaseId + "/" + content.releaseId + "_P.jpg"
        url.let {
            binding.ivMovieImage.loadImagesWithGlideExtPoster(it)
        }

        binding.tvTitle.text = content.movieName
        binding.tvHeading.text = content.headingDetailsNew
        binding.tvSynopsis.text = content.synopsis
        binding.tvCastTitle.text = buildString {
            append("Cast : ")
            append(content.actor)
        }
        binding.tvDirectorTitle.text = buildString {
            append("Director : ")
            append(content.director)
        }
    }

    private fun updateBtn(data: RentalMovieModel?) {
        Log.e(TAG, "updateBtn: $data")
        if (data != null) {
            seekPosition = data.currentSeek
            rentalID = if (data.rentalID == 0) "" else data.rentalID.toString()
            if (data.currentSeek <= 0) {
                binding.btnContinueWatch.text = getString(R.string.watch_free)
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
            if (movie?.trailerAvailable == true) binding.btnWatchTrailer.toVisible()
            else binding.btnWatchTrailer.toGone()
            binding.btnRentNow.requestFocus()
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
        this.isCheckedIn = status
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            ua = getUa()
        }
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    private fun handleFocus(view: View, focused: Boolean){
        if(focused){
            view.background = getGradientColor()
        }else{
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

    private fun getGradientColor(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(ThemeDetails.GRADIENT_COLOR_START), Color.parseColor(ThemeDetails.GRADIENT_COLOR_END))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

/*    fun View.handleFocusChange() {
        setOnFocusChangeListener { _, b ->
            if (b) {
                background = getGradientColor()
            } else {
                setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
    }

    fun getGradientColor(): GradientDrawable {
        val startColor =
            ThemeDetails.GRADIENT_COLOR_START?.ifEmpty { Constants.DEFAULTGRADIENTSTARTCOLOR }
        val endColor = ThemeDetails.GRADIENT_COLOR_END?.ifEmpty { Constants.DEFAULTGRADIENTENDCOLOR }
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }*/

}