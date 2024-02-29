package com.diipl.moviebeam.data.dto.program

import kotlinx.serialization.Serializable

@Serializable
data class ChannelEpgDTO(
    val AR: Int?,
    val CBT: String?,
    val CI: Int?,
    val CL: String?,
    val CLCloud: String?,
    val CN: String?,
    val CNO: String?,
    val VP: String?,
    val httpStreaming: Boolean?,
    val httpStreamingUrl: String?,
    val id: Int?,
    val param1: String?,
    val param2: String?,
    val recordable: Boolean?
)