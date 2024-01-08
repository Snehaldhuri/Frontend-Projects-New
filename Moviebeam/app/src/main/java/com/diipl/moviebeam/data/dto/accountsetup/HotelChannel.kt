package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class HotelChannel(
    val aspectRatio: Int = 0,
    val channelBroadcastType: String = "",
    val channelName: String = "",
    val channelNo: String = "",
    val fileName: String = "",
    val httpStreaming: Boolean = false,
    val id: Int = 0,
    val major: String = "",
    val minor: String = "",
    val recordable: Boolean = false,
    val tvChannelBroadcastType: String = ""
)