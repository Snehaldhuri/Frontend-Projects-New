package com.diipl.moviebeam.ui.exoplayer

import android.media.tv.TvContentRating
import android.media.tv.TvTrackInfo
import android.media.tv.TvView
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.media3.extractor.mp4.Track
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.program.DvbChannel
import com.diipl.moviebeam.databinding.ActivityLiveTvactivityBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants.DVB_INPUT_ID
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveTVActivity : BaseActivity() {

    private val TAG = "LiveTVActivity"

    private lateinit var binding: ActivityLiveTvactivityBinding
    private val mAudioTrackList = arrayListOf<Track>()
    private val mSubtitleTrackList = arrayListOf<Track>()
    private var mPrevTracksIds = HashSet<String>()
    private var currentPos = 0
    private var isDone = false

    companion object{
        val mChannelList = mutableListOf<DvbChannel>()
    }

    class Track(var infos: TvTrackInfo?, var type: Int) {
        override fun toString(): String {
            return infos?.let {
                when (it.type) {
                    TvTrackInfo.TYPE_AUDIO -> it.language
                    TvTrackInfo.TYPE_VIDEO -> "${it.videoWidth} x ${it.videoHeight} @ ${it.videoFrameRate}"
                    TvTrackInfo.TYPE_SUBTITLE -> it.language
                    else -> infos.toString()
                }
            } ?: "None"
        }
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_tvactivity)

    }

    private fun startPlayback(index: Int) {
        mPrevTracksIds.clear()
//        val channel = mChannelList[index]
        val channel = DvbChannel("Test", index, index.toLong(), DVB_INPUT_ID)
//        mChannelList.add(channel)
        Log.e(TAG, "start tv view with: $index  $channel / ${channel.uri} / ${channel.inputId}")
        lifecycleScope.launch {
//            Glide.with(applicationContext).load(channel.imageUri).into(binding.image)
            binding.tvChannelName.text = "Channel ID    -->  ${channel.channelId} \nChannel No.   -->  ${channel.number}  \nChannel Name  -->  ${channel.name}"
            binding.cardTv.toVisible()
            delay(5000)
            binding.cardTv.toInvisible()
            isDone = true
        }
        binding.tvView.tune(channel.inputId, channel.uri)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.tvView.reset()
        finish()
    }

    override fun onPause() {
        binding.tvView.reset()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentPos = intent.getIntExtra("currentPos", 0)

        startPlayback(currentPos)

        binding.tvView.setCallback(object : TvView.TvInputCallback() {
            override fun onTracksChanged(inputId: String?, tracks: MutableList<TvTrackInfo>?) {
                Log.i(TAG, "onTracksChanged $tracks")

                val trackIds = HashSet<String>()
                tracks?.forEach { t -> trackIds.add(t.id) }

                if (trackIds == mPrevTracksIds) {
                    Log.i(TAG, "ignore spurious track changed event")
                    return
                }

                mPrevTracksIds = trackIds

                mAudioTrackList.clear()
                mSubtitleTrackList.clear()

                mSubtitleTrackList.add(
                    Track(
                        null,
                        TvTrackInfo.TYPE_SUBTITLE
                    )
                )

                tracks?.forEach { t ->
                    when (t.type) {
                        TvTrackInfo.TYPE_AUDIO -> {
                            Log.i(
                                TAG, "- Audio Track: ${t.id} / lang: ${t.language} " +
                                        "/ channels ${t.audioChannelCount} " +
                                        "/ sample rate ${t.audioSampleRate} "
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.i(TAG, "   - extra $k -> ${t.extra.get(k)}")
                                }
                            }

                            mAudioTrackList.add(
                                Track(
                                    t,
                                    t.type
                                )
                            )
                        }

                        TvTrackInfo.TYPE_VIDEO -> {
                            Log.i(
                                TAG, "- Video Track: ${t.id} / " +
                                        "${t.videoWidth} x ${t.videoHeight} @ ${t.videoFrameRate}"
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.i(TAG, "   - extra $k -> ${t.extra.get(k)}")
                                }
                            }
                        }

                        TvTrackInfo.TYPE_SUBTITLE -> {
                            Log.i(
                                TAG, "- Subtitle Track: ${t.id} / lang: ${t.language} " +
                                        "extra: ${t.extra}"
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.i(TAG, "   - extra $k -> ${t.extra.get(k)}")
                                }
                            }
                            mSubtitleTrackList.add(
                                Track(
                                    t,
                                    t.type
                                )
                            )
                        }
                    }
                }
                super.onTracksChanged(inputId, tracks)
            }

            override fun onVideoAvailable(inputId: String?) {
                Log.i(TAG, "onVideoAvailable $inputId")
                super.onVideoAvailable(inputId)
            }

            override fun onTimeShiftStatusChanged(inputId: String?, status: Int) {
                Log.i(TAG, "onTimeShiftStatusChanged")
                super.onTimeShiftStatusChanged(inputId, status)
            }

            override fun onVideoUnavailable(inputId: String?, reason: Int) {
                Log.i(TAG, "onVideoUnavailable reason $reason")
                super.onVideoUnavailable(inputId, reason)
            }

            override fun onContentBlocked(inputId: String?, rating: TvContentRating?) {
                Log.i(TAG, "onContentBlocked $rating")
                super.onContentBlocked(inputId, rating)
            }

            override fun onVideoSizeChanged(inputId: String?, width: Int, height: Int) {
                Log.i(TAG, "onVideoSizeChanged $width x $height")
                super.onVideoSizeChanged(inputId, width, height)
            }

            override fun onChannelRetuned(inputId: String?, channelUri: Uri?) {
                Log.i(TAG, "onChannelRetuned $inputId / $channelUri")
                super.onChannelRetuned(inputId, channelUri)
            }

            override fun onConnectionFailed(inputId: String?) {
                Log.i(TAG, "onConnectionFailed: $inputId")
                super.onConnectionFailed(inputId)
            }

            override fun onTrackSelected(inputId: String?, type: Int, trackId: String?) {
                when (type) {
                    TvTrackInfo.TYPE_AUDIO -> Log.i(TAG, "audio track selected $trackId")
                    TvTrackInfo.TYPE_VIDEO -> Log.i(TAG, "video track selected $trackId")
                    TvTrackInfo.TYPE_SUBTITLE -> Log.i(TAG, "subtitle track selected $trackId")
                }
                super.onTrackSelected(inputId, type, trackId)
            }

            override fun onDisconnected(inputId: String?) {
                Log.i(TAG, "onDisconnected $inputId")
                super.onDisconnected(inputId)
            }

            override fun onContentAllowed(inputId: String?) {
                Log.i(TAG, "onContentAllowed $inputId")
                super.onContentAllowed(inputId)
            }
        })
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
         return event?.let {
            when (it.keyCode) {
                KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_PAGE_UP -> {
                    if (isDone)channelUp()
                    true
                }

                KeyEvent.KEYCODE_CHANNEL_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> {
                    if (isDone)channelDown()
                    true
                }

                else -> super.onKeyUp(keyCode, event)
            }
        } ?: super.onKeyUp(keyCode, event)
    }

    private fun channelUp() {
        isDone = false
        currentPos++
        if (currentPos >= mChannelList.count())
            currentPos -= 1
        Log.d(TAG, "set position to $currentPos")
        startPlayback(currentPos)
    }

    private fun channelDown() {
        isDone = false
        currentPos--
        if (currentPos < 0)
            currentPos = 0
        Log.d(TAG, "set position to $currentPos")
        startPlayback(currentPos)
    }

}