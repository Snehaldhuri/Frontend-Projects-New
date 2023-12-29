package com.diipl.moviebeam.ui.showtime

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeGenre
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ShowtimeDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    private var show: Detail? = null
    private var gradient: GradientDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setShowDetails(show: Detail) {
        this.show = show
        show.secImagePathSushi.let {
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