package com.diipl.moviebeam.ui.movies

import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
//        movie?.secImagePathPoster?.let {
//            binding.ivMovieImage.loadImagesWithGlideExt(it)
//        }
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
    }

}