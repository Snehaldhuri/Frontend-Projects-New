package com.diipl.moviebeam.service.udp

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.nes.libplayerapi.PlayerApi
import com.nes.libplayerapi.bean.TrackBean
import com.nes.libplayerapi.constant.ConstantKeys
import com.nes.libplayerapi.listener.OnVideoStateListener
import com.nes.libseiplayer.AbstractVideoPlayer
import com.nes.libseiplayer.SeiPlayerImpl

class UDPPlayer(context: Context) {

    private var playerApi: PlayerApi = SeiPlayerImpl<AbstractVideoPlayer>()
    private lateinit var stateListener: OnVideoStateChangeListener

    init {
        playerApi.init(context)
        playerApi.setLooping(true)
        playerApi.addOnStateChangeListener(object : OnVideoStateListener {
            override fun onPlayStateChanged(p0: Int) {
                when (p0) {
                    ConstantKeys.CurrentState.STATE_PREPARING -> if (::stateListener.isInitialized) stateListener.onPrepare()
                    ConstantKeys.CurrentState.STATE_PREPARED -> if (::stateListener.isInitialized) stateListener.onPrepared()
                    ConstantKeys.CurrentState.STATE_PLAYING -> if (::stateListener.isInitialized) stateListener.onPlaying()
                    ConstantKeys.CurrentState.STATE_PAUSED -> if (::stateListener.isInitialized) stateListener.onPause()
                    ConstantKeys.CurrentState.STATE_BUFFERING_START -> if (::stateListener.isInitialized) stateListener.onBuffering()
                    ConstantKeys.CurrentState.STATE_COMPLETED -> if (::stateListener.isInitialized) stateListener.onComplete()
                    ConstantKeys.CurrentState.STATE_START_ABORT -> if (::stateListener.isInitialized) stateListener.onError("Video state is aborted.")
                    ConstantKeys.CurrentState.STATE_BUFFERING_END -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_BUFFERING_END.")
                    }

                    ConstantKeys.CurrentState.STATE_ERROR -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_ERROR.")
                    }

                    ConstantKeys.CurrentState.STATE_IDLE -> {
                        if (::stateListener.isInitialized) stateListener.onStop()
                    }

                    ConstantKeys.CurrentState.STATE_NETWORK_ERROR -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_NETWORK_ERROR.")
                    }

                    ConstantKeys.CurrentState.STATE_ONCE_LIVE -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_ONCE_LIVE.")
                    }

                    ConstantKeys.CurrentState.STATE_PARSE_ERROR -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_PARSE_ERROR.")
                    }

                    ConstantKeys.CurrentState.STATE_URL_NULL -> {
                        if (::stateListener.isInitialized) stateListener.onError("Video state is STATE_URL_NULL.")
                    }
                }
            }

            override fun onImplPlayerInit() {

            }

        })
    }

    fun setSubTitleView(root: ViewGroup) {
        playerApi.subTitleViewGroup = root
    }

    fun setUrl(udpUrl: String) {
        playerApi.url = udpUrl
    }

    fun onVideoStateChangeListener(listener: OnVideoStateChangeListener) {
        stateListener = listener
    }

    fun playStart() {
        playerApi.start()
    }

    fun setupSurface(surfaceView: SurfaceView) {
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
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

    fun toggleVideoTrack(currentPos: Int): Int {
        var position = currentPos
        val trackBeans: List<TrackBean> = playerApi.videoList
        if (position >= trackBeans.size - 1) {
            position = 0
        } else {
            position++
        }
        playerApi.selectTrack(trackBeans[currentPos])
        return position
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

    fun seekForward(second: Long) {
        val position: Long = playerApi.currentPosition + second * 1000
        playerApi.seekTo(position)
    }

    fun seekBackward(second: Int) {
        val position: Long = playerApi.currentPosition - second * 1000
        playerApi.seekTo(position)
    }

    fun playPause() {
        if (playerApi.isPlaying) {
            playerApi.pause()
        } else {
            playerApi.resume()
        }
    }

    fun stop() {
        playerApi.stop()
    }

}