package com.diipl.moviebeam.data.dto.accountsetup

data class HotelChannel(
    val aspectRatio: Int,
    val channelBroadcastType: String,
    val channelName: String,
    val channelNo: String,
    val fileName: String,
    val httpStreaming: Boolean,
    val id: Int,
    val major: String,
    val minor: String,
    val recordable: Boolean,
    val tvChannelBroadcastType: String
)