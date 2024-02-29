package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.databinding.ActivityPrgGuidePlayerBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class PrgGuidePlayerActivity : BaseActivity(), KeyEvent.Callback {


    private val TAG = this::class.java.simpleName
    private lateinit var binding: ActivityPrgGuidePlayerBinding
    private lateinit var player: ExoPlayer
    private var contentList = mutableListOf<MediaItem>()
    private var channelList: List<ChannelEpgDTO>? = Constants.CURRENT_PROGRAMS
    private var index = 0

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityPrgGuidePlayerBinding.inflate(layoutInflater)
        fetchDetails()
        setContentView(binding.root)
        initializePlayer()
        showChannelInfo()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .setRenderersFactory(DefaultRenderersFactory(this).setEnableDecoderFallback(true))
            .build()
        val playerView = binding.pvProgram
        playerView.player = player
        if (::player.isInitialized) {
            player.setMediaItems(contentList, index, 0)
            player.playWhenReady = true
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            player.prepare()
            player.play()
        }
//        LoggingService.sendMessageToWebSocket("In Program guide live services ","04")
    }

    private fun fetchDetails() {
        if (intent != null) {
            intent.extras?.getStringArrayList(Constants.CONTENT_LIST_PARAM)
                ?.let {
                    it.forEachIndexed { index, str ->
                        if (str != null) {
                            contentList.add(MediaItem.fromUri(str))
                        } else {
                            contentList.add(MediaItem.fromUri(it[index - 1]))
                        }
                    }
                }
            intent.extras?.getInt(Constants.SELECTED_CHANNEL_INDEX)?.let { index = it }
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                seekToPrevious()
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                seekToNext()
            }

            KeyEvent.KEYCODE_BACK -> {
                finish()
            }
        }
        return false
    }

    private fun seekToPrevious() {
        if (player?.hasPreviousMediaItem() == true) {
            player?.seekToPreviousMediaItem()
            showChannelInfo()
        }
    }

    private fun seekToNext() {
        if (player?.hasNextMediaItem() == true) {
            player?.seekToNextMediaItem()
            showChannelInfo()
        }
    }

    private fun showChannelInfo() {
        binding.channelInfo.removeCallbacks { }
        player?.currentMediaItemIndex?.let { setChannelInfo(channelList?.get(it)) }
        binding.channelInfo.toVisible()
        binding.channelInfo.postDelayed(::hideChannelInfo, 8000)
    }

    private fun hideChannelInfo() {
        binding.channelInfo.toInvisible()
    }

    private fun setChannelInfo(program: ChannelEpgDTO?) {
        program?.CL?.let {
        }
        if (program?.CL != null) {
            binding.layoutChannelInfo.ivChannelLogo.loadImagesWithGlideExt(program.CL!!)
            binding.layoutChannelInfo.ivChannelLogo.toVisible()
        } else {
            binding.layoutChannelInfo.ivChannelLogo.toInvisible()
        }

        binding.layoutChannelInfo.tvChannelNo.text =
            getString(R.string.channel_no, program?.CNO.toString())
        binding.layoutChannelInfo.tvChannelName.text = program?.CN
        binding.layoutChannelInfo.tvNowShowing.text = program?.liveProg1
        binding.layoutChannelInfo.tvNext.text = program?.liveProg2
        binding.layoutChannelInfo.tvNowShowingTime.text = program?.prog1Time
        binding.layoutChannelInfo.tvNextTime.text = program?.prog2Time
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

}