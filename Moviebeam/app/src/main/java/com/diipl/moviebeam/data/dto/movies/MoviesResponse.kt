package com.diipl.moviebeam.data.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    val accountId: String = "",
    val adultDayPassPrice: Int = 0,
    val id: Int = 0,
    val type: String = "",
    val version: String = "",
    val freeContentList: List<ContentDto> = emptyList(),
    val freeGenreList: List<GenreDto> = emptyList(),
    val premiumContentList: List<ContentDto> = emptyList(),
    val premiumGenreList: List<PremiumGenre> = emptyList()
//    @Serializable(with = ContentDtoPersistentListSerializer::class)
//    val freeContentList: PersistentList<ContentDto> = persistentListOf(),
//    @Serializable(with = GenreDtoContentDtoPersistentListSerializer::class)
//    val freeGenreList: PersistentList<GenreDto> = persistentListOf(),
//    @Serializable(with = ContentDtoPersistentListSerializer::class)
//    val premiumContentList: PersistentList<ContentDto> = persistentListOf(),
//    @Serializable(with = PremiumGenreContentDtoPersistentListSerializer::class)
//    val premiumGenreList: PersistentList<PremiumGenre> = persistentListOf()

)