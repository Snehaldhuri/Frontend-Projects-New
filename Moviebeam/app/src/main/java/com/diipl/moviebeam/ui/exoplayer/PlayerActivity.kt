package com.diipl.moviebeam.ui.exoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceException
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.UdpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.ActivityPlayerBinding
import com.diipl.moviebeam.utils.toJson
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


private const val TAG = "PlayerActivity"

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    companion object {
        private const val USE_TEXTURE_VIEW = false
        private const val ENABLE_SUBTITLES = true
        private const val URI = "rtp://232.100.103.10:1310/AMC.m2t"
    }

    private lateinit var player: ExoPlayer
    private lateinit var mediaSource: MediaSource
    val videoUri = Uri.parse("udp://@232.100.103.10:1310")

  /*  private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer*/

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)

        player = ExoPlayer.Builder(this).build()

        /*     val address = "232.100.103.1"
             val port = 1301 // your UDP port number
             val udpMediaSourceFactory = UdpMediaSourceFactory()
             val factory = DataSource.Factory { UdpDataSource(3000, 100000) }
             val dataSource = UdpDataSourceFactory(address, port).createDataSource()
             Log.e(TAG, "responseHeaders: ${dataSource.responseHeaders}")
             val mediaItem = MediaItem.Builder().setUri(videoUri).build()
     //        mediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
             mediaSource = udpMediaSourceFactory.createMediaSource(mediaItem)
             player.setMediaSource(mediaSource)
             binding.playerView.player = player
             player.addListener(playerListener)
             player.playWhenReady = true
             player.prepare()*/

     /*   try {

            val options = ArrayList<String>()
            //options.add("--subsdec-encoding <encoding>");
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles")
            options.add("--audio-time-stretch")
            options.add("-vvv") // verbosity
            libVLC = LibVLC(this, options).also {
                it.instance
            }

            // Creating media player

            // Creating media player
            mediaPlayer = MediaPlayer(libVLC)
//        mediaPlayer.setEventListener(mPlayerListener)
            binding.videoContainer.holder.setKeepScreenOn(true)
            // Seting up video output

            // Seting up video output
            val vout = mediaPlayer.vlcVout
            vout.setVideoView(binding.videoContainer)
            vout.addCallback(this)
            vout.attachViews()

            val m = Media(libVLC, videoUri)
            mediaPlayer.media = m
            mediaPlayer.play()
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: ${e.localizedMessage}")
        }
*/

        /*   val args = arrayListOf(
               "--file-caching=1000",
               "--vvv",
               "--network-caching=150",
               "--clock-jitter=0",
               "--live-caching=150",
               "--drop-late-frames",
               "--skip-frames",
               "--vout=android-display",
               "--sout-transcode-vb=20",
               "--no-audio",
               "--sout=#transcode{vcodec=h264,vb=20,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display}",
               "--sout-x264-nf"
           )
           libVLC = LibVLC(this, args)
           mediaPlayer = MediaPlayer(libVLC)
           mediaPlayer.attachViews(binding.videoContainer, null, false, false)
           mediaPlayer.setEventListener { event ->
               Log.e(TAG, "event: ${event.toJson()}")
           }
           // Set up the media source (replace udp_stream_url with your actual UDP stream URL)
           val media = Media(libVLC, videoUri)
           mediaPlayer.media = media
           // Start playback
           mediaPlayer.play()*/
    }

    /*   override fun onResume() {
        super.onResume()

        val cmd = arrayOf("-i", videoUri, "-an", "-vcodec", "mpeg4", "-f", "rawvideo", "-pix_fmt", "rgb24", "-")
        FFmpegKit.executeAsync(cmd.toString(),
            { session ->
                val state = session.state
                val returnCode = session.returnCode

                // CALLED WHEN SESSION IS EXECUTED
                Log.e(
                    TAG,
                    String.format(
                        "FFmpeg process exited with state %s and rc %s.%s",
                        state,
                        returnCode,
                        session.failStackTrace
                    )
                )
            }, {
                // CALLED WHEN SESSION PRINTS LOGS
                Log.e(TAG, "SESSION PRINTS: ${it.message}")
            }) {
            // CALLED WHEN SESSION GENERATES STATISTICS
            Log.e(TAG, "SESSION GENERATES: ${it.toJson()}")
        }
    }*/

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError: ${error.localizedMessage}")
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            Log.e(TAG, "onEvents: ${player.mediaMetadata.toJson()}")
        }

    }
}

@UnstableApi
class UdpDataSource(private val address: String, private val port: Int) : DataSource {

    private lateinit var socket: DatagramSocket
    private lateinit var packet: DatagramPacket
    private val buffer = ByteArray(BUFFER_SIZE)

    override fun open(dataSpec: DataSpec): Long {
        socket = DatagramSocket()
        socket.connect(InetAddress.getByName(address), port)
        packet = DatagramPacket(buffer, BUFFER_SIZE)
        return C.LENGTH_UNSET.toLong()
    }

    @Throws(DataSourceException::class)
    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        packet.data = buffer
        socket.receive(packet)
        return packet.length
    }

    override fun addTransferListener(transferListener: TransferListener) {

    }

    override fun getUri(): Uri? {
        return null
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val BUFFER_SIZE = 1024
    }
}

@UnstableApi
class UdpDataSourceFactory(private val address: String, private val port: Int) :
    DataSource.Factory {

    override fun createDataSource(): DataSource {
        return UdpDataSource(address, port)
    }
}

@UnstableApi
class UdpMediaSourceFactory : MediaSource.Factory {

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val factory = DataSource.Factory { UdpDataSource(3000, 100000) }
        return ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
    }

    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        TODO("Not yet implemented")
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        TODO("Not yet implemented")
    }

    override fun getSupportedTypes(): IntArray {
        return intArrayOf(C.TYPE_OTHER)
    }

}