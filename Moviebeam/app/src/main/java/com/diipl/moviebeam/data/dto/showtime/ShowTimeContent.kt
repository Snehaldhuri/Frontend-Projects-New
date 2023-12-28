package com.diipl.moviebeam.data.dto.showtime

data class ShowTimeContent(
    val contentType: String,
    val contentTypeId: Int,
    val director: String,
    val episodesPresent: Boolean,
    val genre1: String,
    val id: Int,
    val imagePathSushi: String,
    val imgTap: String,
    val movieName: String,
    val parentMovieName: String,
    val parentReleaseId: Int,
    val productId: Int,
    val releaseId: Int,
    val seasonList: List<Season>,
    val secImagePathSushi: String,
    val sushiHoverText: String,
    val trailerAvailable: Boolean
)