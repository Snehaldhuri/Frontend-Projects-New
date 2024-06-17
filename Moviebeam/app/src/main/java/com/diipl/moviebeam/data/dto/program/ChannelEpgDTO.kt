package com.diipl.moviebeam.data.dto.program

import kotlinx.serialization.Serializable

@Serializable
data class ChannelEpgDTO(
    val AR: Int? = null,
    val CBT: String? = null,
    val CI: Int? = null,
    val CL: String? = null,
    val CLCloud: String? = null,
    val CN: String? = null,
    val CNO: String? = null,
    val VP: String? = null,
    val httpStreaming: Boolean? = null,
    val httpStreamingUrl: String? = null,
    val id: Int? = null,
    val param1: String? = null,
    val param2: String? = null,
    val recordable: Boolean? = null,
    )