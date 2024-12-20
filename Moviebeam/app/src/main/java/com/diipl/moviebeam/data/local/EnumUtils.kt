package com.diipl.moviebeam.data.local

const val TV_SAMSUNG = "Samsung"
const val TV_LG = "LG"
const val TV_DUSANE = "Dusane TV"
const val DTH_TATA_PLAY = "Tata Play"
const val DTH_AIRTEL_BOX = "Airtel Box"
const val DTH_DISH_TV = "DishTV"

const val PROTOCOL_SAMSUNG = "01"
const val PROTOCOL_LG = "02"
const val PROTOCOL_AIRTEL_BOX = "04"
const val PROTOCOL_TATA_PLAY = "05"
const val PROTOCOL_DUSANE_TV = "06"
const val PROTOCOL_DISH_TV = "07"

enum class TvBrand(val id: Int, val brand: String) {
    LG(1, TV_LG),
    SAMSUNG(2, TV_SAMSUNG),
    DUSANE_TV(3, TV_DUSANE);

    companion object {
        private fun fromId(id: Int): TvBrand? = values().find { it.id == id }

        fun findBrand(id: Int): String = fromId(id)?.brand.toString()
    }
}

enum class DTHBrand(val id: Int, val brand: String) {
    TATA_PLAY(1, DTH_TATA_PLAY),
    AIRTEL_BOX(2, DTH_AIRTEL_BOX),
    DISH_TV(3, DTH_DISH_TV);

    companion object {
        private fun fromId(id: Int): DTHBrand? = values().find { it.id == id }

        fun findBrand(id: Int): String = fromId(id)?.brand.toString()
    }
}

enum class RemoteProtocols(val protocol: String, val brand: String) {
    SAMSUNG(PROTOCOL_SAMSUNG, TV_SAMSUNG),
    LG(PROTOCOL_LG, TV_LG),
    AIRTEL_BOX(PROTOCOL_AIRTEL_BOX, DTH_AIRTEL_BOX),
    TATA_PLAY(PROTOCOL_TATA_PLAY, DTH_TATA_PLAY),
    DUSANE_TV(PROTOCOL_DUSANE_TV, TV_DUSANE),
    DISH_TV(PROTOCOL_DISH_TV, DTH_DISH_TV);

    companion object {
        private fun fromCodes(protocol: String): RemoteProtocols? =
            values().find { it.protocol == protocol }

        private fun findCodeByte(protocol: String, num: Int): Byte {
            val brand = fromCodes(protocol)?.brand
            val code = brand?.let { CodeSet.findInputByte(it, num) }
            return code!!
        }

        private fun Int.getByteArray(tvProtocol: String, dthProtocol: String): ByteArray {
            val protocol = dthProtocol.ifEmpty { tvProtocol }
            val protocolByte = protocol.toInt(16).toByte()
            val codeByte = findCodeByte(protocol, this)

            return byteArrayOf(0x48, 0x4C, 0x0C, protocolByte, codeByte)
        }

        fun getNumber0(tvProtocol: String, dthProtocol: String): ByteArray = 0.getByteArray(tvProtocol, dthProtocol)
        fun getNumber1(tvProtocol: String, dthProtocol: String): ByteArray = 1.getByteArray(tvProtocol, dthProtocol)
        fun getNumber2(tvProtocol: String, dthProtocol: String): ByteArray = 2.getByteArray(tvProtocol, dthProtocol)
        fun getNumber3(tvProtocol: String, dthProtocol: String): ByteArray = 3.getByteArray(tvProtocol, dthProtocol)
        fun getNumber4(tvProtocol: String, dthProtocol: String): ByteArray = 4.getByteArray(tvProtocol, dthProtocol)
        fun getNumber5(tvProtocol: String, dthProtocol: String): ByteArray = 5.getByteArray(tvProtocol, dthProtocol)
        fun getNumber6(tvProtocol: String, dthProtocol: String): ByteArray = 6.getByteArray(tvProtocol, dthProtocol)
        fun getNumber7(tvProtocol: String, dthProtocol: String): ByteArray = 7.getByteArray(tvProtocol, dthProtocol)
        fun getNumber8(tvProtocol: String, dthProtocol: String): ByteArray = 8.getByteArray(tvProtocol, dthProtocol)
        fun getNumber9(tvProtocol: String, dthProtocol: String): ByteArray = 9.getByteArray(tvProtocol, dthProtocol)


    }
}

enum class CodeSet(val brand: String, val num: Int, val code: Byte) {

    //Samsung
    SAMSUNG_TV0(TV_SAMSUNG, 0, 0x11),
    SAMSUNG_TV1(TV_SAMSUNG, 1, 0x04),
    SAMSUNG_TV2(TV_SAMSUNG, 2, 0x05),
    SAMSUNG_TV3(TV_SAMSUNG, 3, 0x06),
    SAMSUNG_TV4(TV_SAMSUNG, 4, 0x08),
    SAMSUNG_TV5(TV_SAMSUNG, 5, 0x09),
    SAMSUNG_TV6(TV_SAMSUNG, 6, 0x0a),
    SAMSUNG_TV7(TV_SAMSUNG, 7, 0x0C),
    SAMSUNG_TV8(TV_SAMSUNG, 8, 0x0d),
    SAMSUNG_TV9(TV_SAMSUNG, 9, 0x0E),

    //LG
    LG_TV0(TV_LG, 0, 0X10),
    LG_TV1(TV_LG, 1, 0X11),
    LG_TV2(TV_LG, 2, 0X12),
    LG_TV3(TV_LG, 3, 0X13),
    LG_TV4(TV_LG, 4, 0X14),
    LG_TV5(TV_LG, 5, 0X15),
    LG_TV6(TV_LG, 6, 0X16),
    LG_TV7(TV_LG, 7, 0X17),
    LG_TV8(TV_LG, 8, 0X18),
    LG_TV9(TV_LG, 9, 0X19),

    //Airtel Box
    AIRTEL_TV0(DTH_AIRTEL_BOX, 0, 0x00),
    AIRTEL_TV1(DTH_AIRTEL_BOX, 1, 0x01),
    AIRTEL_TV2(DTH_AIRTEL_BOX, 2, 0x02),
    AIRTEL_TV3(DTH_AIRTEL_BOX, 3, 0x03),
    AIRTEL_TV4(DTH_AIRTEL_BOX, 4, 0x04),
    AIRTEL_TV5(DTH_AIRTEL_BOX, 5, 0x05),
    AIRTEL_TV6(DTH_AIRTEL_BOX, 6, 0x06),
    AIRTEL_TV7(DTH_AIRTEL_BOX, 7, 0x07),
    AIRTEL_TV8(DTH_AIRTEL_BOX, 8, 0x08),
    AIRTEL_TV9(DTH_AIRTEL_BOX, 9, 0x09),

    //TATA Play
    TATA_PLAY_TV0(DTH_TATA_PLAY, 0, 0X00),
    TATA_PLAY_TV1(DTH_TATA_PLAY, 1, 0X01),
    TATA_PLAY_TV2(DTH_TATA_PLAY, 2, 0X02),
    TATA_PLAY_TV3(DTH_TATA_PLAY, 3, 0X03),
    TATA_PLAY_TV4(DTH_TATA_PLAY, 4, 0X04),
    TATA_PLAY_TV5(DTH_TATA_PLAY, 5, 0X05),
    TATA_PLAY_TV6(DTH_TATA_PLAY, 6, 0X06),
    TATA_PLAY_TV7(DTH_TATA_PLAY, 7, 0X07),
    TATA_PLAY_TV8(DTH_TATA_PLAY, 8, 0X08),
    TATA_PLAY_TV9(DTH_TATA_PLAY, 9, 0X09),

    // DishTV
    DISH_TV0(DTH_DISH_TV, 0, 0X10),
    DISH_TV1(DTH_DISH_TV, 1, 0X11),
    DISH_TV2(DTH_DISH_TV, 2, 0X12),
    DISH_TV3(DTH_DISH_TV, 3, 0X13),
    DISH_TV4(DTH_DISH_TV, 4, 0X14),
    DISH_TV5(DTH_DISH_TV, 5, 0X15),
    DISH_TV6(DTH_DISH_TV, 6, 0X16),
    DISH_TV7(DTH_DISH_TV, 7, 0X17),
    DISH_TV8(DTH_DISH_TV, 8, 0X08),
    DISH_TV9(DTH_DISH_TV, 9, 0X09);


    companion object {
        fun findInputByte(brand: String, num: Int): Byte =
            values().find { it.brand == brand && it.num == num }?.code!!
    }
}