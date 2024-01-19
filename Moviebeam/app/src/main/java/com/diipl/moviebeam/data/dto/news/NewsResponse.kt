package com.diipl.moviebeam.data.dto.news

data class NewsResponse(
    val id: Int,
    val newsList: List<News>,
    val type: String
)