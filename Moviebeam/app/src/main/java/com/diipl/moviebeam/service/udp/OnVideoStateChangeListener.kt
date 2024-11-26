package com.diipl.moviebeam.service.udp

interface OnVideoStateChangeListener {
    fun onPrepare()
    fun onPrepared()
    fun onPlaying()
    fun onPause()
    fun onStop()
    fun onBuffering()
    fun onComplete()
    fun onError(error: String)
}

