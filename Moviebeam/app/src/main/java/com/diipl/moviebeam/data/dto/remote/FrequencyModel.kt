package com.diipl.moviebeam.data.dto.remote

import com.diipl.moviebeam.data.local.DTHBrand
import com.diipl.moviebeam.data.local.PROTOCOL_AIRTEL_BOX
import com.diipl.moviebeam.data.local.PROTOCOL_LG
import com.diipl.moviebeam.data.local.RemoteProtocols
import com.diipl.moviebeam.data.local.TvBrand

// Assigned value is default for LG and Airtel
data class RemoteModel(
    var tvId: Int = 1,
    var dthId: Int = 2,
    var delayMs: Long = 1000,
    var sourceId: Int = 2,
    var destinationId: Int = 1,
    var tvProtocol: String = PROTOCOL_LG,
    var dthProtocol: String = PROTOCOL_AIRTEL_BOX,
    var sourceCode: String = "CE",
    var destinationCode: String = "CC",
) {

    var tvBrandName: String = TvBrand.findBrand(tvId)
    var dthBrandName: String = DTHBrand.findBrand(dthId)

    var sourceInput: ByteArray = combineProtocolCode(tvProtocol, sourceCode)
    var destinationInput: ByteArray = combineProtocolCode(tvProtocol, destinationCode)

    var tv0: ByteArray = RemoteProtocols.getNumber0(tvProtocol, dthProtocol)
    var tv1: ByteArray = RemoteProtocols.getNumber1(tvProtocol, dthProtocol)
    var tv2: ByteArray = RemoteProtocols.getNumber2(tvProtocol, dthProtocol)
    var tv3: ByteArray = RemoteProtocols.getNumber3(tvProtocol, dthProtocol)
    var tv4: ByteArray = RemoteProtocols.getNumber4(tvProtocol, dthProtocol)
    var tv5: ByteArray = RemoteProtocols.getNumber5(tvProtocol, dthProtocol)
    var tv6: ByteArray = RemoteProtocols.getNumber6(tvProtocol, dthProtocol)
    var tv7: ByteArray = RemoteProtocols.getNumber7(tvProtocol, dthProtocol)
    var tv8: ByteArray = RemoteProtocols.getNumber8(tvProtocol, dthProtocol)
    var tv9: ByteArray = RemoteProtocols.getNumber9(tvProtocol, dthProtocol)


    private fun combineProtocolCode(protocol: String, code: String): ByteArray {
        val protocolByte = protocol.toInt(16).toByte() // Convert hex string "04" to byte
        val codeByte = code.toInt(16).toByte()         // Convert hex string "DE" to byte

        return byteArrayOf(0x48, 0x4C, 0x0C, protocolByte, codeByte)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoteModel

        if (tvBrandName != other.tvBrandName) return false
        if (!sourceInput.contentEquals(other.sourceInput)) return false
        if (!destinationInput.contentEquals(other.destinationInput)) return false
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
        result = 31 * result + sourceInput.contentHashCode()
        result = 31 * result + destinationInput.contentHashCode()
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