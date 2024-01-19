package com.diipl.moviebeam.data.dto.showtime

import kotlinx.serialization.Serializable

@Serializable
data class ShowTimeContent(
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val director: String = "",
    val episodesPresent: Boolean = false,
    val genre1: String = "",
    val id: Int = 0,
    val imagePathSushi: String = "",
    val imgTap: String = "",
    val movieName: String = "",
    val parentMovieName: String = "",
    val parentReleaseId: Int = 0,
    val productId: Int = 0,
    val releaseId: Int = 0,
    val seasonList: List<Season> = emptyList(),
    val secImagePathSushi: String = "",
    val sushiHoverText: String = "",
    val trailerAvailable: Boolean = false
)