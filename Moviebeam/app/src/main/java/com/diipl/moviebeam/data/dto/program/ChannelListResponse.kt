package com.diipl.moviebeam.data.dto.program

import kotlinx.serialization.Serializable

@Serializable
data class ChannelListResponse(
    val id: Int? = null,
    val channelLcnList: MutableList<ChannelEpgDTO>? = null,
    val type: String? = null
)