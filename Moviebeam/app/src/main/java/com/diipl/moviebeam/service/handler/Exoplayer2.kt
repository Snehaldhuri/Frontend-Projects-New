package com.diipl.moviebeam.service.handler

import android.content.Context
import android.net.Uri
import android.util.Log
import com.diipl.moviebeam.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util

class Exoplayer2(private val context: Context) {

    private val TAG = "Exoplayer"
    private lateinit var player: ExoPlayer

    private val userAgent by lazy {
        Util.getUserAgent(
            context,
            context.getString(R.string.app_name)
        )
    }

    private val renderersFactory by lazy {
        DefaultRenderersFactory(context)
            .forceEnableMediaCodecAsynchronousQueueing()
            .setEnableDecoderFallback(true)
    }

    private val defaultHttpDataSourceFactory by lazy {
        DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(context)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )
    }

    private val manifestDataSourceFactory by lazy {
        DefaultHttpDataSource.Factory().setUserAgent(userAgent)
    }
    private val dashChunkSourceFactory: DashChunkSource.Factory by lazy {
        DefaultDashChunkSource.Factory(
            manifestDataSourceFactory
        )
    }

    init {
        initializePlayer()
    }

    private fun initializePlayer() {

        val mediaSourceFactory =
            DefaultMediaSourceFactory(context).setDrmSessionManagerProvider { getDrmSessionManager() }
        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setUpstreamDataSourceFactory(defaultHttpDataSourceFactory)
                .setCacheWriteDataSinkFactory(null) // Disable writing.

        player = ExoPlayer.Builder(context)
//            .setMediaSourceFactory(mediaSourceFactory)
//            .setMediaSourceFactory(
//                DefaultMediaSourceFactory(context).setDataSourceFactory(
//                    cacheDataSourceFactory
//                )
//            )
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .setRenderersFactory(renderersFactory)
            .build()
            .also { exoPlayer ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

//                exoPlayer.addAnalyticsListener(EventLogger())
            }
    }

    private fun getDrmSessionManager(): DrmSessionManager {
        Log.e(TAG, "getDrmSessionManager: ")
        val drmCallback = LocalMediaDrmCallback(
            "{\"keys\":[{\"kty\":\"oct\",\"k\":\"zdYEdG8tBvBfDa4ar2j6Ag\",\"kid\":\"1S9Fx-qxRYGrlKSxtafp4Q\"}],\"type\":\"temporary\"}".toByteArray()
        )
        return DefaultDrmSessionManager.Builder().setMultiSession(false).build(drmCallback)
    }

    private fun buildMediaSource(url: String): MediaSource {

        val uri: Uri = Uri.parse(url)

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(drmSchemeUuid)
                    .setLicenseUri(DRM_LICENSE_URL)
                    .setScheme(drmSchemeUuid)
                    .build()
            ).build()

        return if (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")
            || uri.lastPathSegment!!.contains("mkv")
        ) {
            ProgressiveMediaSource.Factory(manifestDataSourceFactory)
                .createMediaSource(mediaItem)
        } else if (uri.lastPathSegment!!.contains("m3u8")) {
            HlsMediaSource.Factory(manifestDataSourceFactory)
                .createMediaSource(mediaItem)
        } else if (uri.lastPathSegment!!.contains("mpd")) {
            DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(manifestDataSourceFactory),
                manifestDataSourceFactory
            ).setManifestParser(FilteringManifestParser(DashManifestParser(), null))
                .createMediaSource(mediaItem)
        } else {
            DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                .createMediaSource(mediaItem)
        }
    }

    fun attachPlayer(playerView: PlayerView){
        playerView.player = player
        player.playWhenReady = true
        player.prepare()
        player.play()
    }

    fun play() {
        player.play()
    }

    fun release() {
        player.stop()
        player.release()
    }

    fun setMediaUrl(url: String) {
        player.setMediaSource(buildMediaSource(url))
    }

    companion object {
        const val URL = "https://d14ez9fl8x9e1s.cloudfront.net/DRM+Test/42054_T.mpd"
        //        const val URL = "https://pcontents.s3.amazonaws.com/DRM+Test/Subtital.m2t"
        const val DRM_LICENSE_URL = "https://widevine-dash.ezdrm.com/widevine-php/widevine-foreignkey.php?pX=B84CB6"
        private val drmSchemeUuid = C.WIDEVINE_UUID // DRM Type
    }

}