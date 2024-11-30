package com.fleeksoft.charset.cs

class CharsetMapping {
    //init from sjis0213.dat in SJIS_0213
    val b2cSB: CharArray? = null
    val b2cDB1: CharArray? = null
    val b2cDB2: CharArray? = null

    //min/max(start/end) value of 2nd byte
    val b2Min: Int? = null
    val b2Max: Int? = null

    //min/Max(start/end) value of 1st byte/db1
    val b1MinDB1: Int? = null
    val b1MaxDB1: Int? = null

    //min/Max(start/end) value of 1st byte/db2
    val b1MinDB2: Int? = null
    val b1MaxDB2: Int? = null

    val dbSegSize: Int? = null

    fun decodeSingle(b: Int): Char {
        return b2cSB!![b]
    }

    fun decodeDouble(b1: Int, b2: Int): Char {
        if (b2 in (b2Min!! until b2Max!!)) {
            var adjustedB2 = b2 - b2Min
            var adjustedB1 = b1
            if (b1 in (b1MinDB1!!..b1MaxDB1!!)) {
                adjustedB1 = b1 - b1MinDB1
                return b2cDB1!![adjustedB1 * dbSegSize!! + adjustedB2]
            }
            if (b1 in (b1MinDB2!!..b1MaxDB2!!)) {
                adjustedB1 = b1 - b1MinDB2
                return b2cDB2!![adjustedB1 * dbSegSize!! + adjustedB2]
            }
        }
        return UNMAPPABLE_DECODING
    }

    companion object {
        const val UNMAPPABLE_DECODING: Char = '\uFFFD'
        const val UNMAPPABLE_ENCODING: Int = 0xFFFD
        const val UNMAPPABLE_ENCODING_CHAR = UNMAPPABLE_ENCODING.toChar()
    }
}