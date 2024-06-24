package com.diipl.moviebeam.data.dto.epg

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "channelDetails")
data class ChannelEpgDTO(

    @PrimaryKey(autoGenerate = true) val pk: Int? = null,

    val id: Int? = null,
    val PID: String? = null,
    val PT: String? = null,
    val SY: String? = null,
    val CI: Int? = null,
    var CN: String? = null,
    var CNO: String? = null,
    var CBT: String? = null,
    var AR: Int? = null,
    var CL: String? = null,
    var VP: String? = null,
    var CLCloud: String? = null,
    var httpStreamingUrl: String? = null,
    var httpStreaming: Boolean? = null,
    var recordable: Boolean? = null,
    val C: String? = null,
    val SI: Int? = null,
    val SF: Int? = null,
    var param1: String? = null,
    var param2: String? = null,

    val P1_ID: String? = null,
    val P1_ST: String? = null,
    val P1_ET: String? = null,
    var P1_PT: String? = null,
    var P1_SY: String? = null,
    val P1_CLS: String? = null,
    val P1_DST: String? = null,
    val P1_DET: String? = null,


    val P2_ID: String? = null,
    val P2_ST: String? = null,
    val P2_ET: String? = null,
    var P2_PT: String? = null,
    var P2_SY: String? = null,
    val P2_CLS: String? = null,
    val P2_DST: String? = null,
    val P2_DET: String? = null,

    val P3_ID: String? = null,
    val P3_ST: String? = null,
    val P3_ET: String? = null,
    var P3_PT: String? = null,
    var P3_SY: String? = null,
    val P3_CLS: String? = null,
    val P3_DST: String? = null,
    val P3_DET: String? = null,

    val P4_ID: String? = null,
    val P4_ST: String? = null,
    val P4_ET: String? = null,
    var P4_PT: String? = null,
    var P4_SY: String? = null,
    val P4_CLS: String? = null,
    val P4_DST: String? = null,
    val P4_DET: String? = null,

    val P5_ID: String? = null,
    val P5_ST: String? = null,
    val P5_ET: String? = null,
    var P5_PT: String? = null,
    var P5_SY: String? = null,
    val P5_CLS: String? = null,
    val P5_DST: String? = null,
    val P5_DET: String? = null,

    val P6_ID: String? = null,
    val P6_ST: String? = null,
    val P6_ET: String? = null,
    var P6_PT: String? = null,
    var P6_SY: String? = null,
    val P6_CLS: String? = null,
    val P6_DST: String? = null,
    val P6_DET: String? = null,

    val P7_ID: String? = null,
    val P7_ST: String? = null,
    val P7_ET: String? = null,
    var P7_PT: String? = null,
    var P7_SY: String? = null,
    val P7_CLS: String? = null,
    val P7_DST: String? = null,
    val P7_DET: String? = null,

    val P8_ID: String? = null,
    val P8_ST: String? = null,
    val P8_ET: String? = null,
    var P8_PT: String? = null,
    var P8_SY: String? = null,
    val P8_CLS: String? = null,
    val P8_DST: String? = null,
    val P8_DET: String? = null,

    var channelNameNo: String? = null,
    val epgEndTime: String? = null,
    var lastProg: String? = null,
    var liveProg1: String? = null,
    var liveProg2: String? = null,
    var prog1Time: String? = null,
    var prog2Time: String? = null,
    var progInfo: String? = null,
    var progInfo1: String? = null,
    var progInfo2: String? = null,
    var progSynopsis: String? = null,

    var key: String? = null
)