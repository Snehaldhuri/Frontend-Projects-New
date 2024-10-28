package com.diipl.moviebeam.ui.exoplayer

import android.media.tv.TvContentRating
import android.media.tv.TvContract
import android.media.tv.TvTrackInfo
import android.media.tv.TvView
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.DvbChannel
import com.diipl.moviebeam.databinding.ActivityLiveTvactivityBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.DTV_INPUT_ID
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import kotlinx.coroutines.launch

class LiveTVActivity : BaseActivity() {

    private val TAG = "LiveTVActivity"

    private lateinit var binding: ActivityLiveTvactivityBinding
    private val mAudioTrackList = arrayListOf<Track>()
    private val mSubtitleTrackList = arrayListOf<Track>()
    private var mPrevTracksIds = HashSet<String>()
    private var currentPos = 0
    private var isDone = false

    private var channelNumberInput = ""
    private val channelChangeDelay = 2000L // 2 seconds delay for channel switching
    private val handler = Handler(Looper.getMainLooper())
    private val channelChangeRunnable = Runnable {
        if (channelNumberInput.isNotEmpty()) {
            switchChannel(channelNumberInput)
        }
    }

    private val tuneChannelRunnable = Runnable {
        val program = programGuideList[currentPos]

        if (DEVICE_MODEL == Constants.SEI_MB730) {
            if (!program.param1.isNullOrEmpty()) {
                val majorNumber = program.param1?.toInt()!!
                val minorNumber = program.param2?.toInt()!!
                val bundle = Bundle().apply {
                    putInt(KEY_MAJOR, majorNumber)
                    putInt(KEY_MINOR, minorNumber)
                }
                binding.tvView.tune(DTV_INPUT_ID, CHANNEL_URI, bundle)
            } else {
                showToast("Channel No. ${program.CNO} is not playable.")
            }
        } else {
            val list = mChannelList.toList()
            val dvb = list.filter { program.CNO?.toInt() == it.number }
            if (dvb.isNotEmpty()) {
                binding.tvView.tune(DTV_INPUT_ID, dvb[0].uri)
            } else showToast("Channel No. ${program.CNO} is not available.")

        }
    }

    private val changeChannelRunnable = Runnable {
        binding.cardTv.toGone()
    }

    companion object {
        val DEVICE_MODEL: String = Build.MODEL
        val mChannelList = mutableListOf<DvbChannel>()
        val programGuideList = mutableListOf<ChannelEpgDTO>()
        const val CBT_TERRESTRIAL = "TERRESTRIAL"
        const val KEY_MAJOR = "major_number"
        const val KEY_MINOR = "minor_number"
        val CHANNEL_URI: Uri = TvContract.buildChannelUri(-1)
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

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_tvactivity)
    }


    private fun startPlayback(index: Int) = lifecycleScope.launch {
        currentPos = index
        Log.e(TAG, "startPlayback: index $currentPos")
        mPrevTracksIds.clear()
        handler.removeCallbacks(tuneChannelRunnable)
        handler.removeCallbacks(changeChannelRunnable)

        handler.postDelayed(tuneChannelRunnable, 600)

        val program = programGuideList[index]

        binding.tvView.setCallback(object : TvView.TvInputCallback() {
            override fun onTracksChanged(inputId: String?, tracks: MutableList<TvTrackInfo>?) {
                Log.e(TAG, "onTracksChanged $tracks")

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
                            Log.e(
                                TAG, "- Audio Track: ${t.id} / lang: ${t.language} " +
                                        "/ channels ${t.audioChannelCount} " +
                                        "/ sample rate ${t.audioSampleRate} "
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.e(TAG, "   - extra $k -> ${t.extra.get(k)}")
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
                            Log.e(
                                TAG, "- Video Track: ${t.id} / " +
                                        "${t.videoWidth} x ${t.videoHeight} @ ${t.videoFrameRate}"
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.e(TAG, "   - extra $k -> ${t.extra.get(k)}")
                                }
                            }
                        }

                        TvTrackInfo.TYPE_SUBTITLE -> {
                            Log.e(
                                TAG, "- Subtitle Track: ${t.id} / lang: ${t.language} " +
                                        "extra: ${t.extra}"
                            )
                            if (t.extra != null) {
                                t.extra.keySet().forEach { k ->
                                    Log.e(TAG, "   - extra $k -> ${t.extra.get(k)}")
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
                Log.e(TAG, "onVideoAvailable $inputId")
                super.onVideoAvailable(inputId)
            }

            override fun onTimeShiftStatusChanged(inputId: String?, status: Int) {
                Log.e(TAG, "onTimeShiftStatusChanged")
                super.onTimeShiftStatusChanged(inputId, status)
            }

            override fun onVideoUnavailable(inputId: String?, reason: Int) {
                Log.e(TAG, "onVideoUnavailable reason $reason")
                super.onVideoUnavailable(inputId, reason)
            }

            override fun onContentBlocked(inputId: String?, rating: TvContentRating?) {
                Log.e(TAG, "onContentBlocked $rating")
                super.onContentBlocked(inputId, rating)
            }

            override fun onVideoSizeChanged(inputId: String?, width: Int, height: Int) {
                Log.e(TAG, "onVideoSizeChanged $width x $height")
                super.onVideoSizeChanged(inputId, width, height)
            }

            override fun onChannelRetuned(inputId: String?, channelUri: Uri?) {
                Log.e(TAG, "onChannelRetuned $inputId / $channelUri")
                super.onChannelRetuned(inputId, channelUri)
            }

            override fun onConnectionFailed(inputId: String?) {
                Log.e(TAG, "onConnectionFailed: $inputId")
                super.onConnectionFailed(inputId)
            }

            override fun onTrackSelected(inputId: String?, type: Int, trackId: String?) {
                when (type) {
                    TvTrackInfo.TYPE_AUDIO -> Log.e(TAG, "audio track selected $trackId")
                    TvTrackInfo.TYPE_VIDEO -> Log.e(TAG, "video track selected $trackId")
                    TvTrackInfo.TYPE_SUBTITLE -> Log.e(TAG, "subtitle track selected $trackId")
                }
                super.onTrackSelected(inputId, type, trackId)
            }

            override fun onDisconnected(inputId: String?) {
                Log.e(TAG, "onDisconnected $inputId")
                super.onDisconnected(inputId)
            }

            override fun onContentAllowed(inputId: String?) {
                Log.e(TAG, "onContentAllowed $inputId")
                super.onContentAllowed(inputId)
            }
        })

        binding.tvChannelName.text =
            "\nChannel No.   -->  ${program.CNO}  \nChannel Name  -->  ${program.CN} \nMajor  -->  ${program.param1}   Minor  --> ${program.param2}"
        binding.cardTv.toVisible()
        handler.postDelayed(changeChannelRunnable, 4000)

    }

    override fun onResume() {
        super.onResume()

        if (programGuideList[0].CNO.equals("100")) {
            programGuideList.removeAt(0)
            currentPos -= 1
            Log.e(TAG, "onResume: $currentPos")
        }
    }

    fun handleBackRemoteClick() {
        Log.e(TAG, "handleBackRemoteClick: ")
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.tvView.reset()
        finish()
    }

    override fun onPause() {
        super.onPause()
        binding.tvView.reset()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        programGuideList.clear()
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

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_PAGE_UP -> {
                binding.tvView.reset()
                channelUp()
                return true
            }

            KeyEvent.KEYCODE_CHANNEL_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> {
                binding.tvView.reset()
                channelDown()
                return true
            }

            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9,
            -> {
                // Append the numeric key to the channel number input
                channelNumberInput += (keyCode - KeyEvent.KEYCODE_0).toString()

                if (channelNumberInput.length > 4) {
                    channelNumberInput = channelNumberInput.substring(1, 5)
                }

                binding.cardNumber.toVisible()
                binding.tvNumber.text = channelNumberInput

                // Reset any previous channel change delay
                handler.removeCallbacks(channelChangeRunnable)

                // Schedule the channel change after a delay
                handler.postDelayed(channelChangeRunnable, channelChangeDelay)
                return true
            }

            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                // Handle channel switch immediately on "OK" button press
                if (channelNumberInput.isNotEmpty()) {
                    handler.removeCallbacks(channelChangeRunnable)
                    switchChannel(channelNumberInput)
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun switchChannel(channelNumber: String) {
        if (channelNumber.isNotEmpty()) {
            // Logic to switch to the entered channel number
            // Replace this with actual channel switching code
            binding.cardNumber.toGone()
            binding.tvView.reset()

            val originalNum = channelNumber.trimStart('0')
            Log.e(TAG, "Switching to channel: $channelNumber ->  $originalNum")

            val channel = programGuideList.filter { it.CNO == originalNum }

            if (channel.isNotEmpty()) {
                val position = programGuideList.indexOf(channel[0])
                startPlayback(position)
            } else {
                showToast("Channel No. $originalNum is not available!")
            }
            channelNumberInput = "" // Reset channel input after switching
            binding.tvNumber.text = channelNumberInput
        }
    }

    private fun channelUp() {
        Log.e(TAG, "channelUp: $currentPos")
        currentPos++
        Log.e(TAG, "channelUp: ${programGuideList.count()}   $currentPos")
        if (currentPos == programGuideList.count())
            currentPos = 0

        startPlayback(currentPos)
    }

    private fun channelDown() {
        Log.e(TAG, "channelDown: $currentPos")
        currentPos--
        Log.e(TAG, "channelDown: After $currentPos")
        if (currentPos < 0)
            currentPos = programGuideList.count() - 1

        startPlayback(currentPos)
    }

}