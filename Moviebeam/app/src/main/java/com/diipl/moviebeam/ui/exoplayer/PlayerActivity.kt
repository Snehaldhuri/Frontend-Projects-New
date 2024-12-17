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
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import com.nes.libplayerapi.PlayerApi
import com.nes.libplayerapi.bean.TrackBean
import com.nes.libplayerapi.constant.ConstantKeys
import com.nes.libplayerapi.listener.OnFingerPrintListener
import com.nes.libplayerapi.listener.OnVideoStateListener
import com.nes.libseiplayer.AbstractVideoPlayer
import com.nes.libseiplayer.SeiPlayerImpl

private const val TAG = "PlayerActivity"

class PlayerActivity : BaseActivity(), OnVideoStateListener {

    private lateinit var binding: ActivityPlayerBinding
    private var currentPos = 0
    private val handler = Handler(Looper.getMainLooper())
    private var playerApi: PlayerApi = SeiPlayerImpl<AbstractVideoPlayer>()

    private var isFirst = true
    private var channelNumberInput = ""
    private val channelChangeDelay = 2000L // 2 seconds delay for channel switching
    private val channelChangeRunnable = Runnable {
        if (channelNumberInput.isNotEmpty()) {
            switchChannel(channelNumberInput)
        }
    }

    private val changeChannelRunnable = Runnable {
        binding.cardTv.toGone()
    }

    companion object {
        val programGuideList = mutableListOf<ChannelEpgDTO>()
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentPos = intent.getIntExtra("currentPos", 0)

        playerApi.init(this)
        playerApi.setLooping(true)
        playerApi.subTitleViewGroup = binding.frameLayout

        startPlayback()

        setupSurface()

    }

    private fun startPlayback() {
        handler.removeCallbacks(changeChannelRunnable)

        val program = programGuideList[currentPos]
        val udpUrl = program.setupUrl()

        playerApi.url = udpUrl

        if (isFirst) isFirst = false
        else playerApi.start()

        logD("startPlayback: udpUrl ->> ${program.CNO} -- $udpUrl")

        binding.tvChannelName.text =
            "\nChannel No.   -->  ${program.CNO}  \nChannel Name  -->  ${program.CN} \nUDP  --> $udpUrl "
        binding.cardTv.toVisible()
        handler.postDelayed(changeChannelRunnable, 4000)

    }

    private fun ChannelEpgDTO.setupUrl() = "udp://@${this.param1}:${this.param2}"

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
        playerApi.addOnVideoSizeChangeListeners { i, i1 -> }
    }

    override fun onPause() {
        playerApi.stop()
        playerApi.release()
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        programGuideList.clear()
    }

    private fun channelUp() {
        currentPos++
        if (currentPos == programGuideList.count())
            currentPos = 0

        startPlayback()
    }

    private fun channelDown() {
        currentPos--
        if (currentPos < 0)
            currentPos = programGuideList.count() - 1

        startPlayback()
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

            KeyEvent.KEYCODE_BACK -> handleBackRemoteClick()

            /*KeyEvent.KEYCODE_1 -> {
                toggleAudioTrack()
            }

            KeyEvent.KEYCODE_2 -> {
                toggleVideoTrack()
            }

            KeyEvent.KEYCODE_3 -> {
                toggleSubtitle()
            }*/


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
        return super.onKeyDown(keyCode, event)
    }
    private fun switchChannel(channelNumber: String) {
        if (channelNumber.isNotEmpty()) {
            // Logic to switch to the entered channel number
            // Replace this with actual channel switching code
            binding.cardNumber.toGone()
            playerApi.reset()

            val originalNum = channelNumber.trimStart('0')
            Log.e(TAG, "Switching to channel: $channelNumber ->  $originalNum")

            val channel = programGuideList.filter { it.CNO == originalNum }

            if (channel.isNotEmpty()) {
                currentPos = programGuideList.indexOf(channel[0])
                playerApi.stop()
                startPlayback()
            } else {
                showToast("Channel No. $originalNum is not available!")
            }
            channelNumberInput = "" // Reset channel input after switching
            binding.tvNumber.text = channelNumberInput
        }
    }


    fun handleBackRemoteClick() {
        onBackPressed()
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
