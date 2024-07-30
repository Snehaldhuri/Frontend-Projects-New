package com.diipl.moviebeam.data.dto.program

import android.media.tv.TvContract

data class DvbChannel(
    var name: String,
    var number: Int,
    var channelId: Long,
    var inputId: String
) {
    var uri = TvContract.buildChannelUri(channelId)
    var imageUri = TvContract.buildChannelLogoUri(channelId)
//        val uri = Uri.parse("dvb://0001.0001.0001.8002.0001")

    override fun toString(): String {
        return "$channelId $number $name"
    }

}