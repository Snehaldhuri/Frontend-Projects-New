package com.diipl.moviebeam.data.dto.epg

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "channelDetails")
data class ChannelEpgDTO(

    @PrimaryKey(autoGenerate = true) val pk: Int?,

    val id: Int?,
    val PID: String?,
    val PT: String?,
    val SY: String?,
    val CI: Int?,
    var CN: String?,
    var CNO: String?,
    var CBT: String?,
    var AR: Int?,
    var CL: String?,
    var VP: String?,
    var CLCloud: String?,
    var httpStreamingUrl: String?,
    var httpStreaming: Boolean?,
    var recordable: Boolean?,
    val C: String?,
    val SI: Int?,
    val SF: Int?,
    var param1: String?,
    var param2: String?,

    val P1_ID: String?,
    val P1_ST: String?,
    val P1_ET: String?,
    var P1_PT: String?,
    var P1_SY: String?,
    val P1_CLS: String?,
    val P1_DST: String?,
    val P1_DET: String?,


    val P2_ID: String?,
    val P2_ST: String?,
    val P2_ET: String?,
    var P2_PT: String?,
    var P2_SY: String?,
    val P2_CLS: String?,
    val P2_DST: String?,
    val P2_DET: String?,

    val P3_ID: String?,
    val P3_ST: String?,
    val P3_ET: String?,
    var P3_PT: String?,
    var P3_SY: String?,
    val P3_CLS: String?,
    val P3_DST: String?,
    val P3_DET: String?,

    val P4_ID: String?,
    val P4_ST: String?,
    val P4_ET: String?,
    var P4_PT: String?,
    var P4_SY: String?,
    val P4_CLS: String?,
    val P4_DST: String?,
    val P4_DET: String?,

    val P5_ID: String?,
    val P5_ST: String?,
    val P5_ET: String?,
    var P5_PT: String?,
    var P5_SY: String?,
    val P5_CLS: String?,
    val P5_DST: String?,
    val P5_DET: String?,

    val P6_ID: String?,
    val P6_ST: String?,
    val P6_ET: String?,
    var P6_PT: String?,
    var P6_SY: String?,
    val P6_CLS: String?,
    val P6_DST: String?,
    val P6_DET: String?,

    val P7_ID: String?,
    val P7_ST: String?,
    val P7_ET: String?,
    var P7_PT: String?,
    var P7_SY: String?,
    val P7_CLS: String?,
    val P7_DST: String?,
    val P7_DET: String?,

    val P8_ID: String?,
    val P8_ST: String?,
    val P8_ET: String?,
    var P8_PT: String?,
    var P8_SY: String?,
    val P8_CLS: String?,
    val P8_DST: String?,
    val P8_DET: String?,

    var channelNameNo: String?,
    val epgEndTime: String?,
    var lastProg: String?,
    var liveProg1: String?,
    var liveProg2: String?,
    var prog1Time: String?,
    var prog2Time: String?,
    var progInfo: String?,
    var progInfo1: String?,
    var progInfo2: String?,
    var progSynopsis: String?,

    var key: String?
)