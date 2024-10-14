package com.diipl.moviebeam.ui.exoplayer

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalReversalResponse
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityExoPlayerBinding
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.room.models.ShowTimeModel
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.movies.MoviesViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.getLastSeek
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


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

    private var releaseId = 0
    private lateinit var rentalMovieModel: RentalMovieModel
    private lateinit var showTimeModel: ShowTimeModel
    private lateinit var movieData: ContentDto
    private lateinit var seriesData: Detail

    //Variables from datastore
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper =
        PreferenceDataStoreHelper(this)
    private var ua = ""

    override fun observeViewModel() {
        observe(moviesViewModel.rentalReversal, ::handleRentalReversalResponse)
    }

    private fun handleRentalReversalResponse(resource: Resource<RentalReversalResponse>) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.errorCode == 0 && it.description == "success") {
                        moviesViewModel.deleteMovieDetails(rentalMovieModel)
                    }
                    if (it.errorCode == 0 && it.description.isEmpty()) {
                        moviesViewModel.deleteMovieDetails(rentalMovieModel)
                    }
                    finish()
                }

            }

            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.initializeDatastoreParams()

        if (intent != null) {
            intent.extras?.getString(Constants.MOVIE_DETAILS)?.let { movieData = it.fromJson() }
            intent.extras?.getString(Constants.SHOW_DETAILS)?.let { seriesData = it.fromJson() }
            intent.extras?.getBoolean(Constants.IS_TRAILER)?.let { isTrailer = it }
            intent.extras?.getBoolean(Constants.IS_CONTENT)?.let { isContent = it }
            intent.extras?.getLong(Constants.IS_CONTINUE, 0)?.let { seekPosition = it }
        }

        releaseId = if (::movieData.isInitialized) movieData.releaseId else seriesData.releaseId

        moviesViewModel.getRentalMovie(releaseId)
        moviesViewModel.movieData.observe(this) {
            if (it != null)
                rentalMovieModel = it
        }

        moviesViewModel.getShowData(releaseId)
        moviesViewModel.seriesData.observe(this) {
            if (it != null)
                showTimeModel = it
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
        try {
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

                    // TODO remove below code in release
                    /*if (releaseId == 41232)
                        releaseId = 41391*/

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
                        exoPlayer.prepare()
                        exoPlayer.play()
                        exoPlayer.seekTo(seekPosition)
                    }

                }
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("initializePlayer Exception: ${e.message}", "")
        }
    }

    private fun releasePlayer() {
        try {
            player?.let { exoPlayer ->
                if (isContent && ::rentalMovieModel.isInitialized) {
                    rentalMovieModel.currentSeek = exoPlayer.getLastSeek()
                    moviesViewModel.updateMovieDetails(rentalMovieModel)
                }
                if (isContent && ::showTimeModel.isInitialized) {
                    showTimeModel.currentSeek = exoPlayer.getLastSeek()
                    moviesViewModel.updateShowDetails(showTimeModel)
                }
                playbackPosition = exoPlayer.currentPosition
                mediaItemIndex = exoPlayer.currentMediaItemIndex
                playWhenReady = exoPlayer.playWhenReady
                exoPlayer.release()
            }
            player = null
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("releasePlayer Exception: ${e.message}", "")
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
        apiCall(0, 2)
        finish()
    }

    fun handleBackRemoteClick() {
        Log.e(TAG, "handleBackRemoteClick: ")
        onBackPressed()
    }

    override fun onBackPressed() {
        try {
            releasePlayer()
            super.onBackPressed()
            Log.e(TAG, "onBackPressed: ")
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "onBackPressed: Exception ${e.localizedMessage}")
            LoggingService.sendMessageToWebSocket("onBackPressed Exception: ${e.message}", "")
        }
    }

    private fun playerListener() = object : Player.Listener {

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError: ${error.localizedMessage}")
            if (error.localizedMessage!! == "Source error") {
                if (::showTimeModel.isInitialized) {
                    moviesViewModel.deleteShowDetails(showTimeModel)
                    moviesViewModel.viewModelScope.cancel()
                    finish()
                }
                if (::rentalMovieModel.isInitialized) {
                    moviesViewModel.setRentalReversal(rentalMovieModel)
                    return
                }
                finish()
            }

        }

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
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE   -"
            }
            when (playbackState) {
                ExoPlayer.STATE_ENDED -> {
                    releasePlayer()
                    apiCall(0, 2)
                    finish()
                }

                Player.STATE_BUFFERING -> {}

                Player.STATE_IDLE -> {}

                Player.STATE_READY -> {}
            }
            Log.e("ExoPlayer state", "changed state to $stateString")
        }

    }
    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        Log.e(TAG, "onKeyDown:  keyCode == $keyCode  scanCode --> ${keyEvent.scanCode}", )
        when(keyCode){
            KeyEvent.KEYCODE_BACK -> handleBackRemoteClick()

            0 -> {
                when(keyEvent.scanCode){
                    Constants.PLAY_MEDIA_BACKWARD-> player?.seekBack()

                    Constants.PLAY_MEDIA_FORWARD-> player?.seekForward()

                    Constants.MEDIA_PLAY_PAUSE->{
                        if(player?.isPlaying == true) {
                            player?.pause()
                        }else{
                            player?.play()
                        }
                    }
                }
            }
            else -> {
                if (keyEvent.scanCode == Constants.EXIT_KEY) handleBackRemoteClick()
            }
        }
        return false
    }

    private fun apiCall(seekType: Int, a: Int) {
        val request = RentalMovieRequest()
        request.UA = ua
        if (::movieData.isInitialized) {
            movieData.let {
                request.productId = it.productId
                request.releaseID = it.releaseId
                request.price = it.price
                request.contentTypeID = it.contentTypeId
                request.rentalID =
                    if (::rentalMovieModel.isInitialized) rentalMovieModel.rentalID.toString() else ""
                request.productType = it.releaseTypeId
                request.a = a
                request.ra = 0
                request.seekType = seekType
                request.seek = player.getLastSeek()
            }
            moviesViewModel.updateRentalMovieLog(request)
        }
        if (::seriesData.isInitialized) {
            seriesData.let {
                request.productId = it.productId
                request.releaseID = it.releaseId
                request.contentTypeID = it.contentTypeId
                request.rentalID =
                    if (::showTimeModel.isInitialized) showTimeModel.rentalID.toString() else ""
                request.productType = Constants.SHOWTIME_RELEASE_TYPE_ID
                request.a = a
                request.ra = 0
                request.seekType = seekType
                request.seek = player.getLastSeek()
            }
            moviesViewModel.updateRentalMovieLog(request)
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

}

