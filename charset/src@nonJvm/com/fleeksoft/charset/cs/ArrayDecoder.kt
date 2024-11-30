package com.fleeksoft.charset.cs

interface ArrayDecoder {
    fun decode(src: ByteArray, off: Int, len: Int, dst: CharArray): Int

    // Default implementation for isASCIICompatible
    fun isASCIICompatible(): Boolean {
        return false
    }

    // Default implementation for isLatin1Decodable
    fun isLatin1Decodable(): Boolean {
        return false
    }

    // Default implementation for decodeToLatin1
    fun decodeToLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
        return 0
    }
}