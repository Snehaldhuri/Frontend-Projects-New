package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class ItemMenu(
    val dispPrice: String = "",
    val itemId: Int = 0,
    val langWiseList: Map<String, TVToiletryItemDTO> = emptyMap(),
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Int = 1
)
@Serializable
data class TVToiletryItemDTO(
    val name: String = ""
)