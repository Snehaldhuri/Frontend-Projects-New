package com.diipl.moviebeam.ui.exoplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import androidx.databinding.DataBindingUtil
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.databinding.ActivityPlayerBinding
import com.diipl.moviebeam.service.udp.UDPPlayer
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import com.nes.libplayerapi.PlayerApi
import com.nes.libplayerapi.bean.TrackBean
import com.nes.libplayerapi.constant.ConstantKeys
import com.nes.libplayerapi.listener.OnFingerPrintListener
import com.nes.libplayerapi.listener.OnVideoSizeListener
import com.nes.libplayerapi.listener.OnVideoStateListener
import com.nes.libseiplayer.AbstractVideoPlayer
import com.nes.libseiplayer.SeiPlayerImpl
import java.util.concurrent.TimeUnit

private const val TAG = "PlayerActivity"

class PlayerActivity : BaseActivity(), OnVideoStateListener {

    private lateinit var binding: ActivityPlayerBinding
    private var currentPos = 0
    private val handler = Handler(Looper.getMainLooper())
    private var timeInSeconds = 0L
    private val timeHandler = Handler(Looper.getMainLooper())
    private var playerApi: PlayerApi = SeiPlayerImpl<AbstractVideoPlayer>()

    private lateinit var player: UDPPlayer

    private val changeChannelRunnable = Runnable {
        binding.cardTv.toGone()
    }
    private val runnable = object : Runnable {
        override fun run() {
            timeInSeconds++
//            binding.tvNumber.text = formatTime(timeInSeconds)
            timeHandler.postDelayed(this, 1000) // Update every second
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    companion object {
        val programGuideList = mutableListOf<ChannelEpgDTO>()
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)

    }

    override fun onResume() {
        super.onResume()

        if (programGuideList[0].CNO.equals("100") || programGuideList[0].CN?.contains("Hotel") == true) {
            programGuideList.removeAt(0)
            currentPos -= 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentPos = intent.getIntExtra("currentPos", 0)

        playerApi.init(context)
        playerApi.setLooping(true)
        playerApi.subTitleViewGroup = binding.frameLayout

        val program = programGuideList[currentPos]
        val udpUrl = program.setupUrl()
        playerApi.url = udpUrl

        setupSurface()

    }

    private fun startPlayback(index: Int) {
        currentPos = index
        handler.removeCallbacks(changeChannelRunnable)
        timeHandler.removeCallbacks(runnable)

        timeHandler.post(runnable)

        val program = programGuideList[currentPos]
        val udpUrl = program.setupUrl()

        playerApi.url = udpUrl
        playerApi.start()

        Log.e(TAG, "startPlayback: udpUrl ->> ${program.CNO} -- $udpUrl")

        binding.tvChannelName.text =
            "\nChannel No.   -->  ${program.CNO}  \nChannel Name  -->  ${program.CN} \nUDP  --> $udpUrl "
        binding.cardTv.toVisible()
        handler.postDelayed(changeChannelRunnable, 4000)

    }

    private fun ChannelEpgDTO.setupUrl(): String {
        val udpUrl = "udp://@${this.param1}:${this.param2}"
        return udpUrl
    }

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
        playerApi.addOnVideoSizeChangeListeners(OnVideoSizeListener { i, i1 -> })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        playerApi.stop()
        programGuideList.clear()
        finish()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                seekBackward(30)
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                seekForward(30)
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> playPause()

            KeyEvent.KEYCODE_CHANNEL_DOWN,
            KeyEvent.KEYCODE_DPAD_DOWN,
            -> {
                playerApi.stop()
                channelDown()
            }

            KeyEvent.KEYCODE_CHANNEL_UP,
            KeyEvent.KEYCODE_DPAD_UP,
            -> {
                playerApi.stop()
                channelUp()
            }


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
        Log.e(TAG, "onPlayStateChanged: $state")

        if (i == -1) {
            binding.cardError.toVisible()
            binding.tvError.text = "Unable to tune, please try later..."
        } else {
            binding.cardError.toGone()
            binding.tvError.text = ""
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
