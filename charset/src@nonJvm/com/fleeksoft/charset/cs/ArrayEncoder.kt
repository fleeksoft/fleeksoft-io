package com.fleeksoft.charset.cs

interface ArrayEncoder {
    //  is only used by j.u.zip.ZipCoder for utf8
    fun encode(src: CharArray, offset: Int, len: Int, dst: ByteArray): Int

    fun encodeFromLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
        return -1
    }

    fun encodeFromUTF16(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
        return -1
    }

    fun isASCIICompatible(): Boolean {
        return false
    }
}