package com.diipl.moviebeam.data.dto.ticker

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class TickerResponse(
    val id: Int? = null,
    var tvTickerList: MutableList<TvTickerDTO>? = null,
    val type: String? = null,
    val version: String? = null
): Cloneable {
    fun deepCopy(
        id: Int? = this.id,
        tvTickerList: MutableList<TvTickerDTO>? = this.tvTickerList,
        type: String? = this.type,
        version: String? = this.version
    ) = TickerResponse(id, tvTickerList, type, version)

    public override fun clone(): TickerResponse {
        var gson = Gson()
        var jsonString = gson.toJson(this)
        return gson.fromJson(jsonString, TickerResponse::class.java)
    }
}