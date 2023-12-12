package com.diipl.moviebeam.data.dto.accountsetup

data class ItemMenu(
    val dispPrice: String,
    val itemId: Int,
    val langWiseList: Map<String, TVToiletryItemDTO>,
    val name: String,
    val price: Double
)

data class TVToiletryItemDTO(
    val name: String
)