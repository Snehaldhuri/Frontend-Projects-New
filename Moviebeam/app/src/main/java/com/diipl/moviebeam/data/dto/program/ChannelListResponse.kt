package com.diipl.moviebeam.data.dto.program

import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import kotlinx.serialization.Serializable

@Serializable
data class ChannelListResponse(
    val id: Int? = null,
    val channelLcnList: MutableList<ChannelEpgDTO> = mutableListOf(),
    val type: String? = null
)