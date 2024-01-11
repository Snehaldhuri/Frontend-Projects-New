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
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ShowtimeDetailFragment : BaseFragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    private var show: Detail? = null
    private var gradient: GradientDrawable? = null

    private var position: Int = 0

    private val showtimeViewModel: ShowtimeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("movieReleaseId")
        }
    }
    override fun observeViewModel() {
        observe(showtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
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
        binding.btnRentNow.setOnClickListener {
            show?.let { it1 -> (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(it1,false,true) }
        }
    }
    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> { binding.loaderView.toVisible() }
            is Resource.Success -> {
                val response = showtimeViewModel.showtimeLiveData.value?.data

                val detail = response?.shoGenreList?.get(0)?.detailList?.find { detail ->
                    detail.releaseId == position
                }
                detail?.let { setShowDetails(it)  }

                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorCode?.let { showtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    fun setShowDetails(show: Detail) {
        this.show = show

        val httpStreamingHotelvideoUrl ="http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        show.imagePathPoster =httpStreamingHotelvideoUrl+show.releaseId+"/"+show.releaseId+"_P.jpg"
        show.imagePathPoster.let {
            binding.ivMovieImage.loadImagesWithGlideExt(it)
        }
        binding.ivMovieImage.setBackgroundResource(R.drawable.round_outline_5dp)
        binding.ivMovieImage.clipToOutline =true

        binding.tvTitle.text = show.movieName
        binding.tvHeading.isVisible=false
        binding.tvSynopsis.text = show.synopsis
        binding.tvCastTitle.isVisible=false
        binding.tvDirectorTitle.isVisible=false
        binding.btnRentNow.text = getString(R.string.watch_free)

        val layoutParams = binding.btnRentNow.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.dp_225)
        binding.btnRentNow.layoutParams = layoutParams
        binding.btnRentNow.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnWatchTrailer.isVisible=false
        binding.btnRentNow.requestFocus()
    }
    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }

}