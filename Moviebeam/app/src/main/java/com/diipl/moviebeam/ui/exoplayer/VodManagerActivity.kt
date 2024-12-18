package com.diipl.moviebeam.ui.exoplayer

import android.media.tv.TvContentRating
import android.media.tv.TvContract
import android.media.tv.TvTrackInfo
import android.media.tv.TvView
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import androidx.databinding.DataBindingUtil
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.VodMovieResponse
import com.diipl.moviebeam.databinding.ActivityVodManagerBinding
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.LiveTVActivity.Track
import com.diipl.moviebeam.utils.Constants.DTV_INPUT_ID
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import com.nes.libplayerapi.PlayerApi
import com.nes.libplayerapi.bean.TrackBean
import com.nes.libplayerapi.constant.ConstantKeys
import com.nes.libplayerapi.listener.OnFingerPrintListener
import com.nes.libplayerapi.listener.OnVideoStateListener
import com.nes.libseiplayer.AbstractVideoPlayer
import com.nes.libseiplayer.SeiPlayerImpl
import java.util.concurrent.TimeUnit

private const val TAG = "VodManagerActivity"

class VodManagerActivity : BaseActivity(), OnVideoStateListener {

    private lateinit var binding: ActivityVodManagerBinding
    private var currentPos = 0
    private val handler = Handler(Looper.getMainLooper())
    private var timeInSeconds = 0L
    private val timeHandler = Handler(Looper.getMainLooper())
    private var playerApi: PlayerApi = SeiPlayerImpl<AbstractVideoPlayer>()

    private var isFirst = true

    private val changeChannelRunnable = Runnable {
        binding.cardTv.toGone()
    }
    private val mAudioTrackList = arrayListOf<Track>()
    private val mSubtitleTrackList = arrayListOf<Track>()
    private var mPrevTracksIds = HashSet<String>()

    private val runnable = object : Runnable {
        override fun run() {
            timeInSeconds++
//            binding.tvNumber.text = formatTime(timeInSeconds)
            timeHandler.postDelayed(this, 1000) // Update every second
        }
    }

    companion object {
        var vodResponse = RentalMovieModel()
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

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vod_manager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Snehal $vodResponse")
        if (vodResponse.channelType == "RF") {
            tuneRF()
            Log.d(TAG, "onCreate: ${vodResponse.channelType}")
            Log.d(TAG, "onCreate: In rf")
        } else {
            tuneIP()
            Log.d(TAG, "onCreate: ${vodResponse.channelType}")
            Log.d(TAG, "onCreate: In Ip")
        }
        Log.d(TAG, "onCreate: VodManagerActivity created, $vodResponse")
    }

    private fun tuneIP() {
        binding.tvView.toGone()
        binding.surfaceView.toVisible()

        playerApi.init(this)
        playerApi.setLooping(true)
        playerApi.subTitleViewGroup = binding.frameLayout

        startPlayback()
        setupSurface()
        Log.d(TAG, "tuneIP: tview showing ")
    }

    private fun tuneRF() {
        binding.surfaceView.toGone()
        binding.tvView.toVisible()

        val majorNumber = vodResponse.major.toInt()
        val minorNumber = vodResponse.minor.toInt()
        val bundle = Bundle().apply {
            putInt(KEY_MAJOR, majorNumber)
            putInt(KEY_MINOR, minorNumber)
        }
        binding.tvView.tune(DTV_INPUT_ID, CHANNEL_URI, bundle)
        Log.d(TAG, "tuneRF1: $DTV_INPUT_ID")
        Log.d(TAG, "tuneRF2: $CHANNEL_URI")
        Log.d(TAG, "tuneRF3: $bundle")

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
        Log.d(TAG, "tuneIP: surface view showing")

    }

    private fun startPlayback() {
        handler.removeCallbacks(changeChannelRunnable)
        timeHandler.removeCallbacks(runnable)

        timeHandler.post(runnable)

        val udpUrl = vodResponse.setupUrl()
        playerApi.url = udpUrl

        if (isFirst) isFirst = false
        else playerApi.start()

        Log.e(TAG, "startPlayback: udpUrl ->> $udpUrl")

        binding.cardTv.toVisible()
        handler.postDelayed(changeChannelRunnable, 4000)
        Log.d(TAG, "onCreate: VodManagerActivity startPlayback")

    }

    private fun RentalMovieModel.setupUrl() = "udp://@${this.ip}:${this.port}"

    fun setupSurface() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val surface = holder.surface
                playerApi.surface = surface
                playerApi.start()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })
        Log.d(TAG, "onCreate: PlayerActivity setupSurface")
        playerApi.setOnFingerPrintListener(object : OnFingerPrintListener {
            override fun onVMXFingerPrintEvent(
                mFingerPrintArray: ByteArray,
                webClientVersion: String,
                uniqueIdentifier: String,
            ) {
            }

            override fun onVMXFingerPrintEvent(
                type: Int,
                duration: Int,
                quadrant: Int,
                frequency: Int,
                opaqueness: Int,
                reserve: Int,
                localTime: String,
                deviceId: String,
                clientVersion: String,
            ) {
            }

            override fun onNagraFingerPrintEvent(mFingerPrint: String) {}
        })
        playerApi.addOnStateChangeListener(this)
        playerApi.addOnVideoSizeChangeListeners { i, i1 -> }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        playerApi.stop()
        binding.tvView.reset()
        finish()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            /* KeyEvent.KEYCODE_DPAD_LEFT -> {
                 seekBackward(30)
             }

             KeyEvent.KEYCODE_DPAD_RIGHT -> {
                 seekForward(30)
             }

             KeyEvent.KEYCODE_DPAD_CENTER -> playPause()*/

            KeyEvent.KEYCODE_1 -> {
                toggleAudioTrack()
            }

            KeyEvent.KEYCODE_2 -> {
                toggleVideoTrack()
            }

            KeyEvent.KEYCODE_3 -> {
                toggleSubtitle()
            }

            KeyEvent.KEYCODE_BACK -> onBackPressed()

        }
        return super.onKeyDown(keyCode, event)
    }

    fun playPause() {
        if (playerApi.isPlaying) {
            playerApi.pause()
        } else {
            playerApi.resume()
        }
    }

    fun seekForward(second: Long) {
        val position: Long = playerApi.currentPosition + second * 1000
        playerApi.seekTo(position)
    }

    fun seekBackward(second: Int) {
        val position: Long = playerApi.currentPosition - second * 1000
        playerApi.seekTo(position)
    }

    override fun onPlayStateChanged(i: Int) {
        var state = "IDLE"
        when (i) {
            ConstantKeys.CurrentState.STATE_ERROR -> state = "ERROR"
            ConstantKeys.CurrentState.STATE_IDLE -> state = "IDLE"
            ConstantKeys.CurrentState.STATE_PREPARING -> state = "PREPARING"
            ConstantKeys.CurrentState.STATE_PREPARED -> state = "PREPARED"
            ConstantKeys.CurrentState.STATE_PLAYING -> state = "PLAYING"
            ConstantKeys.CurrentState.STATE_PAUSED -> state = "PAUSED"
            ConstantKeys.CurrentState.STATE_BUFFERING_END -> state = "BUFFERING_END"
            ConstantKeys.CurrentState.STATE_BUFFERING_START -> state = "BUFFERING_START"
            ConstantKeys.CurrentState.STATE_COMPLETED -> state = "COMPLETED"
            ConstantKeys.CurrentState.STATE_START_ABORT -> state = "START_ABORT"
        }
        //binding.tvNumber.text = state
        Log.e(TAG, "onPlayStateChanged: $state  ${playerApi.isPlaying}")
        binding.progressBar.toVisible()
        if (i == ConstantKeys.CurrentState.STATE_ERROR) {
            binding.progressBar.toGone()
            binding.cardError.toVisible()
            binding.tvError.text = "Unable to tune, please try later..."
        } else {
            binding.cardError.toGone()
            binding.tvError.text = ""
            if (i == ConstantKeys.CurrentState.STATE_PLAYING) binding.progressBar.toGone()
        }

    }

    override fun onImplPlayerInit() {

    }

    fun toggleSubtitle() {
        val trackBeans: List<TrackBean> = playerApi.subTitleList
        var i = 0
        while (i < trackBeans.size) {
            if (trackBeans[i].isSelected) {
                break
            }
            i++
        }
        if (i >= trackBeans.size - 1) {
            i = 0
        } else {
            i++
        }
        playerApi.selectTrack(trackBeans[i])
    }

    fun toggleVideoTrack() {
        val trackBeans: List<TrackBean> = playerApi.videoList
        if (currentPos >= trackBeans.size - 1) {
            currentPos = 0
        } else {
            currentPos++
        }
        playerApi.selectTrack(trackBeans[currentPos])
    }

    fun toggleAudioTrack() {
        val trackBeans: List<TrackBean> = playerApi.audioList
        var i = 0
        while (i < trackBeans.size) {
            if (trackBeans[i].isSelected) {
                break
            }
            i++
        }
        if (i >= trackBeans.size - 1) {
            i = 0
        } else {
            i++
        }
        playerApi.selectTrack(trackBeans[i])
    }



}