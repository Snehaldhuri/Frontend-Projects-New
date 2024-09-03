package com.diipl.moviebeam.ui.exoplayer

import android.media.tv.TvContentRating
import android.media.tv.TvContract
import android.media.tv.TvTrackInfo
import android.media.tv.TvView
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.UdpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.ActivityPlayerBinding
import com.diipl.moviebeam.utils.Constants.DTV_INPUT_ID
import com.diipl.moviebeam.utils.toVisible


private const val TAG = "PlayerActivity"

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    companion object {
        const val CBT_TERRESTRIAL = "TERRESTRIAL"
        const val KEY_MAJOR = "major_number"
        const val KEY_MINOR = "minor_number"
        val CHANNEL_URI: Uri = TvContract.buildChannelUri(0)

//        val UDP_URI: Uri = Uri.parse("udp://@232.100.103.1:1301")
        val UDP_URI: Uri = Uri.parse("udp://@232.100.103.10:1310")
    }

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)

        startRFPlay()

    }

    @OptIn(UnstableApi::class)
    private fun startIPTuning() {
        binding.playerView.toVisible()
        val player = ExoPlayer.Builder(this).build()

        val factory = DataSource.Factory { UdpDataSource(300000, 100000) }
        val mediaItem = MediaItem.Builder().setUri(UDP_URI).build()
        val mediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        player.setMediaSource(mediaSource)
        binding.playerView.player = player
        player.playWhenReady = true
        player.prepare()

    }

    private fun startRFPlay(){
        binding.tvView.toVisible()

        val majorNumber = 40
        val minorNumber = 2

        val bundle = Bundle().apply {
            putInt(KEY_MAJOR, majorNumber)
            putInt(KEY_MINOR, minorNumber)
        }

        binding.tvView.tune(DTV_INPUT_ID, CHANNEL_URI, bundle)

        binding.tvView.setCallback(object : TvView.TvInputCallback() {
            override fun onConnectionFailed(inputId: String?) {
                super.onConnectionFailed(inputId)
                Log.e(TAG, "onConnectionFailed: $inputId")
            }
            override fun onDisconnected(inputId: String?) {
                super.onDisconnected(inputId)
                Log.e(TAG, "onDisconnected: $inputId")
            }
            override fun onTracksChanged(inputId: String?, tracks: MutableList<TvTrackInfo>?) {
                super.onTracksChanged(inputId, tracks)
                Log.e(TAG, "onTracksChanged: $inputId   ${tracks?.size}")
            }
            override fun onTrackSelected(inputId: String?, type: Int, trackId: String?) {
                super.onTrackSelected(inputId, type, trackId)
                Log.e(TAG, "onTrackSelected: $inputId, $type, $trackId")
            }
            override fun onVideoSizeChanged(inputId: String?, width: Int, height: Int) {
                super.onVideoSizeChanged(inputId, width, height)
                Log.e(TAG, "onVideoSizeChanged: $inputId, $width, $height")
            }
            override fun onVideoAvailable(inputId: String?) {
                super.onVideoAvailable(inputId)
                Log.e(TAG, "onVideoAvailable: $inputId")
            }
            override fun onVideoUnavailable(inputId: String?, reason: Int) {
                super.onVideoUnavailable(inputId, reason)
                Log.e(TAG, "onVideoUnavailable: $inputId  $reason")
            }
            override fun onContentAllowed(inputId: String?) {
                super.onContentAllowed(inputId)
                Log.e(TAG, "onContentAllowed: $inputId")
            }
            override fun onContentBlocked(inputId: String?, rating: TvContentRating?) {
                super.onContentBlocked(inputId, rating)
                Log.e(TAG, "onContentBlocked: $inputId   $rating")
            }
        })

    }
}
