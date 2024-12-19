package com.diipl.moviebeam.data.dto.concierge

data class ConciergeApiResponse (
    var ticketNo: String? = null,
    var date: String? = null,
    var time: String? = null,
    var toiletryList: List<ToiletryDataResponse>? = null

)

data class ToiletryDataResponse(
    var name: String? = null,
    var price: Int? = null,
    var quantity: Int? = null,
)