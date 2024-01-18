package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.view.KeyEvent
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.databinding.ActivityPrgGuidePlayerBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrgGuidePlayerActivity : BaseActivity(), KeyEvent.Callback {

    private lateinit var binding: ActivityPrgGuidePlayerBinding
    private var player: ExoPlayer? = null
    private var contentList: List<MediaItem> = emptyList()
    private var channelList: List<ProgramDTO> = emptyList()
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
        player?.let {
            it.setMediaItems(contentList, index, 0)
            it.playWhenReady = true
            it.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            it.prepare()
        }

    }

    private fun fetchDetails() {
        if (intent != null) {
            intent.extras?.getStringArrayList(Constants.CONTENT_LIST_PARAM)
                ?.let { contentList = it.map { MediaItem.fromUri(it) } }
            intent.extras?.getInt(Constants.SELECTED_CHANNEL_INDEX)?.let { index = it }
            intent.extras?.getString(Constants.CHANNEL_LIST_PARAM)?.let {
                val listType = object : TypeToken<List<ProgramDTO>>() {}.type
                channelList = Gson().fromJson(it, listType)
            }
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
        player?.currentMediaItemIndex?.let { setChannelInfo(channelList[it]) }
        binding.channelInfo.toVisible()
        binding.channelInfo.postDelayed(::hideChannelInfo, 8000)
    }

    private fun hideChannelInfo() {
        binding.channelInfo.toInvisible()
    }

    private fun setChannelInfo(program: ProgramDTO) {
        program.CL?.let {
            binding.layoutChannelInfo.ivChannelLogo.loadImagesWithGlideExt(it)
        }
        binding.layoutChannelInfo.tvChannelNo.text =
            getString(R.string.channel_no, program.CNO.toString())
        binding.layoutChannelInfo.tvChannelName.text = program.CN
        binding.layoutChannelInfo.tvNowShowing.text = program.liveProg1
        binding.layoutChannelInfo.tvNext.text = program.liveProg2
        binding.layoutChannelInfo.tvNowShowingTime.text = program.prog1Time
        binding.layoutChannelInfo.tvNextTime.text = program.prog2Time
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

}