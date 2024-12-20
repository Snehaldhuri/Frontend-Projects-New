package com.diipl.moviebeam.data.dto.concierge


import com.google.gson.annotations.SerializedName

data class ConciergeResponse(
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("ticketNo")
    var ticketNo: String? = null,
    @SerializedName("toiletries")
    var toiletries: List<ToiletryData>? = null

) {
    override fun toString(): String {
        return listOf(
            "id" to id,
            "date" to date,
            "time" to time,
            "ticketNo" to ticketNo,
            "toiletries" to toiletries?.joinToString("; ") { it.toString() }
        ).filter { it.second != null }
            .joinToString(", ") { "${it.first}=${it.second}" }
    }
}

data class ToiletryData(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("quantity")
    var quantity: Int? = null
) {
    override fun toString(): String {
        return listOf(
            "name" to name,
            "quantity" to quantity
        ).filter { it.second != null }
            .joinToString(", ") { "${it.first}=${it.second}" }
    }
}