package com.diipl.moviebeam.data.dto.showtime

data class Detail(
    val actor: String,
    val contentType: String,
    val contentTypeId: Int,
    val director: String,
    val episodeHeaderDetails: String,
    val episodesPresent: Boolean,
    val genre1: String,
    val headingDetails: String,
    val headingDetailsShowtime: String,
    val id: Int,
    val imagePathPoster: String,
    val imagePathSushi: String,
    val imgTap: String,
    val movieName: String,
    val parentMovieName: String,
    val parentReleaseId: Int,
    val productId: Int,
    val releaseId: Int,
    val rentalDuration: Int,
    val secImagePathPoster: String,
    val secImagePathSushi: String,
    val sushiHoverText: String,
    val synopsis: String,
    val trailerAvailable: Boolean,
    val videoPath: String
)