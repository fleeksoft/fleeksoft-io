package com.fleeksoft.lang

object Character {
    const val MIN_SUPPLEMENTARY_CODE_POINT: Int = 0x010000
    const val MAX_CODE_POINT: Int = 0X10FFFF

    fun isSupplementaryCodePoint(codePoint: Int): Boolean {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint < MAX_CODE_POINT + 1
    }

    fun highSurrogate(codePoint: Int): Char {
        return ((codePoint ushr 10) + (Char.MIN_HIGH_SURROGATE.code - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10))).toChar()
    }

    fun lowSurrogate(codePoint: Int): Char {
        return ((codePoint and 0x3ff) + Char.MIN_LOW_SURROGATE.code).toChar()
    }

    fun isSurrogate(char: Char): Boolean = char.isSurrogate()

    fun isHighSurrogate(char: Char): Boolean = char.isHighSurrogate()
    fun isLowSurrogate(char: Char): Boolean = char.isLowSurrogate()

    fun toCodePoint(high: Char, low: Char): Int {
        /*return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT
        - (MIN_HIGH_SURROGATE << 10)
        - MIN_LOW_SURROGATE);*/
        return ((high.code shl 10) + low.code) + (MIN_SUPPLEMENTARY_CODE_POINT - (Char.MIN_HIGH_SURROGATE.code shl 10) - Char.MIN_LOW_SURROGATE.code)
    }

    fun isBmpCodePoint(codePoint: Int): Boolean {
//        return codePoint >>> 16 == 0;
        return codePoint ushr 16 == 0
    }

    fun isValidCodePoint(codePoint: Int): Boolean {
        /*int plane = codePoint >>> 16;
        return plane < ((MAX_CODE_POINT + 1) >>> 16);*/
        return (codePoint ushr 16) < ((MAX_CODE_POINT + 1) ushr 16)
    }

    fun compare(x: Char, y: Char): Int {
        return x - y
    }
}