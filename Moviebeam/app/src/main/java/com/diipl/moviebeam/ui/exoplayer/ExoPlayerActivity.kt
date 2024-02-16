package com.diipl.moviebeam.ui.exoplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.databinding.ActivityExoPlayerBinding
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.movies.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "ExoPlayerActivity"

@AndroidEntryPoint
class ExoPlayerActivity : BaseActivity() {

    private lateinit var binding: ActivityExoPlayerBinding
    private val moviesViewModel: MoviesViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private val playerListener: Player.Listener = playerListener()

    private var playbackUrl = ""
    private var isTrailer = false
    private var isContent = false
    private var seekPosition: Long = 0
    private var seekTime: Int = 0

    private var releaseId = ""
    private lateinit var movieModel: RentalMovieModel

    override fun observeViewModel() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null) {
            intent.extras?.getString(Constants.RELEASE_ID)?.let { releaseId = it }
            intent.extras?.getBoolean(Constants.IS_TRAILER)?.let { isTrailer = it }
            intent.extras?.getBoolean(Constants.IS_CONTENT)?.let { isContent = it }
            intent.extras?.getLong(Constants.IS_CONTINUE, 0)?.let { seekPosition = it }
        }

        moviesViewModel.getRentalMovie(releaseId.toInt())
        moviesViewModel.movieData.observe(this) {
            if (it != null)
                movieModel = it
        }

    }

    override fun initViewBinding() {
        binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) {
            initializePlayer()
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()
                binding.playerView.player = exoPlayer

//                binding.playerView.setShowFastForwardButton(false)
//                binding.playerView.setShowRewindButton(false)
                binding.playerView.setShowNextButton(false)
                binding.playerView.setShowPreviousButton(false)


                if (isTrailer) {
                    playbackUrl =
                        Constants.BASE_PLAYBACK_URL + releaseId + Constants.TRAILER_EXTENSION
                }
                if (isContent) {
                    playbackUrl =
                        Constants.BASE_PLAYBACK_URL + releaseId + Constants.CONTENT_EXTENSION
                }
                if (playbackUrl.isNotEmpty()) {
                    val mediaItem = MediaItem.Builder()
                        .setUri(playbackUrl)
//                        .setMimeType(MimeTypes.APPLICATION_MPD) // For using DASH format use this mediaItem Builder
                        .build()
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.addListener(playerListener)
                    exoPlayer.seekTo(seekPosition)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
                val mediaItem1 = MediaItem.fromUri(Constants.MOVIE_URL1)
                val mediaItem2 = MediaItem.fromUri(Constants.MOVIE_URL2)
                val mediaItem3 = MediaItem.fromUri(Constants.MOVIE_URL3)
                val secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))

            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            if (::movieModel.isInitialized){
                movieModel.currentSeek = exoPlayer.currentPosition
                moviesViewModel.updateMovieDetails(movieModel)
            }
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    override fun onBackPressed() {
        releasePlayer()
        super.onBackPressed()
        finish()
    }

    private fun playerListener() = object : Player.Listener {
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            val prev = oldPosition.positionMs.toInt()
            val new = newPosition.positionMs.toInt()
            val seekType = if (prev < new) 1 else 0
            if (prev != 0)
                apiCall(seekType, 3)

        }


        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> {
                    "ExoPlayer.STATE_ENDED     -"
                }

                else -> "UNKNOWN_STATE             -"
            }
            when (playbackState) {
                ExoPlayer.STATE_ENDED -> {
                    movieModel.currentSeek = 0
                    moviesViewModel.updateMovieDetails(movieModel)
                    apiCall(1, 2)
                    finish()
                }

                Player.STATE_BUFFERING -> {
                }

                Player.STATE_IDLE -> {
                }

                Player.STATE_READY -> {

                }
            }
            Log.e("ExoPlayer state", "changed state to $stateString")
        }

        private fun apiCall(seekType: Int, a: Int) {
            val request = RentalMovieRequest()
            if (::movieModel.isInitialized){
                movieModel.movieData?.let {
                    request.productId = it.productId
                    request.releaseID = it.releaseId
                    request.price = it.price
                    request.contentTypeID = it.contentTypeId
                    request.rentalID = movieModel.rentalID.toString()
                    request.productType = it.releaseTypeId
                    request.a = a
                    request.ra = 0
                    request.seekType = seekType
                    request.seek = player?.currentPosition!!
                    moviesViewModel.updateRentalMovieLog(request)
                }
            }
        }


    }


    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}