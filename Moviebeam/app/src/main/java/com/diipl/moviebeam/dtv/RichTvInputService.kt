package com.diipl.moviebeam.dtv

import android.content.Context
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.View

private const val TAG = "RichTvInputService"
class RichTvInputService : TvInputService() {


    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: Started")

        val session = HardwareInputSession(this)
        Log.e(TAG, "onCreate: ${session.hardwareInputId}")

    }

//    fun isHardwareInput(inputId: String): Boolean {
//        val tvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
//        val tvInputInfo = tvInputManager.getTvInputInfo(inputId)
//        return tvInputInfo?.serviceInfo?.type == TvInputServiceInfo.SERVICE_TYPE_HARDWARE
//    }

    override fun onCreateSession(inputId: String): Session? {
        Log.e(TAG, "onCreateSession: $inputId")
        return null
    }

    inner class HardwareInputSession(context: RichTvInputService) : HardwareSession(context) {
        override fun onRelease() {

        }

        override fun onSetStreamVolume(p0: Float) {

        }

        override fun onHardwareVideoAvailable() {
            Log.e(TAG, "onHardwareVideoAvailable: ")
        }

        override fun notifyTuned(channelUri: Uri) {
            Log.e(TAG, "notifyTuned: $channelUri")
        }

        override fun onTune(p0: Uri?): Boolean {
            Log.e(TAG, "onTune: $p0")
            return false
        }

        override fun onSetCaptionEnabled(p0: Boolean) {

        }

        override fun getHardwareInputId(): String {
            return ""
        }

    }

    class Session(context: Context?) : TvInputService.Session(context) {

        override fun onCreateOverlayView(): View? {
           return null
        }


        override fun onTune(channelUri: Uri): Boolean {
            notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING)
            return true
        }

        override fun onSetCaptionEnabled(p0: Boolean) {

        }

        override fun onRelease() {

        }

        override fun onSetSurface(p0: Surface?): Boolean {
            TODO("Not yet implemented")
        }

        override fun onSetStreamVolume(p0: Float) {
            TODO("Not yet implemented")
        }

    }

}