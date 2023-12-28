package com.diipl.moviebeam.ui.movies

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.databinding.FragmentMovieDetailBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    val binding get() = _binding!!

    private var movie: ContentDto? = null
    private var gradient: GradientDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setMovieDetails(movie: ContentDto) {
        this.movie = movie
        movie.secImagePathPoster.let {
            binding.ivMovieImage.loadImagesWithGlideExt(it)
        }
        binding.tvTitle.text = movie.movieName
        binding.tvHeading.text = movie.headingDetailsNew
        binding.tvSynopsis.text = movie.synopsis
        binding.tvCast.text = movie.actor
        binding.tvDirector.text = movie.director
        binding.btnRentNow.text = getString(R.string.rent_now, movie.qos, movie.price.toString())
        binding.btnRentNow.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnWatchTrailer.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnRentNow.requestFocus()
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }

}