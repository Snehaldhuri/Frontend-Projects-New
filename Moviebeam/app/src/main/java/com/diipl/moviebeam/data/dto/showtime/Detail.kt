package com.diipl.moviebeam.data.dto.showtime

import kotlinx.serialization.Serializable

@Serializable
data class Detail(
    val actor: String = "",
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val director: String = "",
    val episodeHeaderDetails: String = "",
    val episodesPresent: Boolean = false,
    val genre1: String = "",
    val headingDetails: String = "",
    val headingDetailsShowtime: String = "",
    val id: Int = 0,
    val imagePathPoster: String = "",
    val imagePathSushi: String = "",
    val imgTap: String = "",
    val movieName: String = "",
    val parentMovieName: String = "",
    val parentReleaseId: Int = 0,
    val productId: Int = 0,
    val releaseId: Int = 0,
    val rentalDuration: Int = 0,
    val secImagePathPoster: String = "",
    val secImagePathSushi: String = "",
    val sushiHoverText: String = "",
    val synopsis: String = "",
    val trailerAvailable: Boolean = false,
    val videoPath: String = ""
)