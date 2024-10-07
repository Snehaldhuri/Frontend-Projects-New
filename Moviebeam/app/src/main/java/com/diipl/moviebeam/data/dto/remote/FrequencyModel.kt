package com.diipl.moviebeam.data.dto.remote

data class IRFrequencyModel(
    var tvBrandName: String = "",
    var frequency: Int = 0,
    var delayMs: Long = 1000,
    var HDMI1: IntArray = intArrayOf(),
    var OK: IntArray = intArrayOf(),
    var TV: IntArray = intArrayOf(),
    var tv0: IntArray = intArrayOf(),
    var tv1: IntArray = intArrayOf(),
    var tv2: IntArray = intArrayOf(),
    var tv3: IntArray = intArrayOf(),
    var tv4: IntArray = intArrayOf(),
    var tv5: IntArray = intArrayOf(),
    var tv6: IntArray = intArrayOf(),
    var tv7: IntArray = intArrayOf(),
    var tv8: IntArray = intArrayOf(),
    var tv9: IntArray = intArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IRFrequencyModel

        if (tvBrandName != other.tvBrandName) return false
        if (!HDMI1.contentEquals(other.HDMI1)) return false
        if (!TV.contentEquals(other.TV)) return false
        if (!tv0.contentEquals(other.tv0)) return false
        if (!tv1.contentEquals(other.tv1)) return false
        if (!tv2.contentEquals(other.tv2)) return false
        if (!tv3.contentEquals(other.tv3)) return false
        if (!tv4.contentEquals(other.tv4)) return false
        if (!tv5.contentEquals(other.tv5)) return false
        if (!tv6.contentEquals(other.tv6)) return false
        if (!tv7.contentEquals(other.tv7)) return false
        if (!tv8.contentEquals(other.tv8)) return false
        if (!tv9.contentEquals(other.tv9)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tvBrandName.hashCode()
        result = 31 * result + HDMI1.contentHashCode()
        result = 31 * result + TV.contentHashCode()
        result = 31 * result + tv0.contentHashCode()
        result = 31 * result + tv1.contentHashCode()
        result = 31 * result + tv2.contentHashCode()
        result = 31 * result + tv3.contentHashCode()
        result = 31 * result + tv4.contentHashCode()
        result = 31 * result + tv5.contentHashCode()
        result = 31 * result + tv6.contentHashCode()
        result = 31 * result + tv7.contentHashCode()
        result = 31 * result + tv8.contentHashCode()
        result = 31 * result + tv9.contentHashCode()
        return result
    }
}


data class BTCommandModel(
    var tvBrandName: String = "",
    var frequency: Int = 0,
    var delayMs: Long = 1000,
    var HDMI1: ByteArray = byteArrayOf(),
    var HDMI2: ByteArray = byteArrayOf(),
    var HDMI3: ByteArray = byteArrayOf(),
    var OK: ByteArray = byteArrayOf(),
    var TV: ByteArray = byteArrayOf(),
    var tv0: ByteArray = byteArrayOf(),
    var tv1: ByteArray = byteArrayOf(),
    var tv2: ByteArray = byteArrayOf(),
    var tv3: ByteArray = byteArrayOf(),
    var tv4: ByteArray = byteArrayOf(),
    var tv5: ByteArray = byteArrayOf(),
    var tv6: ByteArray = byteArrayOf(),
    var tv7: ByteArray = byteArrayOf(),
    var tv8: ByteArray = byteArrayOf(),
    var tv9: ByteArray = byteArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BTCommandModel

        if (tvBrandName != other.tvBrandName) return false
        if (!HDMI1.contentEquals(other.HDMI1)) return false
        if (!TV.contentEquals(other.TV)) return false
        if (!tv0.contentEquals(other.tv0)) return false
        if (!tv1.contentEquals(other.tv1)) return false
        if (!tv2.contentEquals(other.tv2)) return false
        if (!tv3.contentEquals(other.tv3)) return false
        if (!tv4.contentEquals(other.tv4)) return false
        if (!tv5.contentEquals(other.tv5)) return false
        if (!tv6.contentEquals(other.tv6)) return false
        if (!tv7.contentEquals(other.tv7)) return false
        if (!tv8.contentEquals(other.tv8)) return false
        if (!tv9.contentEquals(other.tv9)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tvBrandName.hashCode()
        result = 31 * result + HDMI1.contentHashCode()
        result = 31 * result + TV.contentHashCode()
        result = 31 * result + tv0.contentHashCode()
        result = 31 * result + tv1.contentHashCode()
        result = 31 * result + tv2.contentHashCode()
        result = 31 * result + tv3.contentHashCode()
        result = 31 * result + tv4.contentHashCode()
        result = 31 * result + tv5.contentHashCode()
        result = 31 * result + tv6.contentHashCode()
        result = 31 * result + tv7.contentHashCode()
        result = 31 * result + tv8.contentHashCode()
        result = 31 * result + tv9.contentHashCode()
        return result
    }
}