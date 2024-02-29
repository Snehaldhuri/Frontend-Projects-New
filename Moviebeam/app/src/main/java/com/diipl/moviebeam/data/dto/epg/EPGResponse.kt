package com.diipl.moviebeam.data.dto.epg

import kotlinx.serialization.Serializable

@Serializable
data class EPGResponse(
    val id: Int? = null,
    val accountId: String? = null,
    val type: String? = null,
    val epgListMap: LinkedHashMap<String, MutableList<ChannelEpgDTO>>? = null,
    val programsListMap: LinkedHashMap<String, ChannelEpgDTO>? = null,
    val ST: String? = null,
    val ET: String? = null
)