package com.diipl.moviebeam.ui.exoplayer

import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.ActivityExoPlayerBinding
import com.diipl.moviebeam.ui.base.BaseActivity

private const val TAG = "ExoPlayerActivity"
class ExoPlayerActivity : BaseActivity() {

    private lateinit var binding: ActivityExoPlayerBinding

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private val playerListener: Player.Listener = playerListener()

    private var playbackUrl = ""
    private var isTrailer = false
    private var isContent = false

    private var releaseId = ""

    override fun observeViewModel() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent!=null){
           intent.extras?.getString(Constants.RELEASE_ID)?.let { releaseId = it  }
           intent.extras?.getBoolean(Constants.IS_TRAILER)?.let { isTrailer = it  }
           intent.extras?.getBoolean(Constants.IS_CONTENT)?.let { isContent = it  }
//            releaseId = intent.getBundleExtra(Constants.RELEASE_ID).toString()
//            isTrailer = intent.getBooleanExtra(Constants.IS_TRAILER,false)
//            isContent = intent.getBooleanExtra(Constants.IS_CONTENT,false)
        }
    }

    override fun initViewBinding() {
        binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @androidx.media3.common.util.UnstableApi
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    @androidx.media3.common.util.UnstableApi
    public override fun  onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    public override fun  onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    public override fun  onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()
                binding.playerView.player = exoPlayer

                // For using DASH format use this mediaItem Builder
                /*val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()*/
                if (isTrailer){
                    playbackUrl = Constants.BASE_PLAYBACK_URL + releaseId + Constants.TRAILER_EXTENSION
                }
                if (isContent){
                    playbackUrl = Constants.BASE_PLAYBACK_URL + releaseId + Constants.CONTENT_EXTENSION
                }
                if(playbackUrl.isNotEmpty()){
                    val mediaItem = MediaItem.fromUri(playbackUrl)

                    exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.addListener(playerListener)
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
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun playerListener() = object : Player.Listener {
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
            when(playbackState){
                ExoPlayer.STATE_ENDED -> {
                    releasePlayer()
                    onBackPressed()
                }

                Player.STATE_BUFFERING -> {
                }

                Player.STATE_IDLE -> {
                }

                Player.STATE_READY -> {
                }
            }
            Log.d("ExoPlayer state", "changed state to $stateString")
        }

    }


    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}