package com.fleeksoft.charset.internal

// todo: jdk @IntrinsicCandidate methods replaces with hand-written assembly and/ or hand-written compiler IR -- a compiler intrinsic -- to improve performance
object JLA {
    fun decodeASCII(sa: ByteArray, sp: Int, da: CharArray, dp: Int, len: Int): Int {
        var count = StringCoding.countPositives(sa, sp, len)
        while (count < len) {
            if (sa[sp + count] < 0) {
                break
            }
            count++
        }
        StringLatin1.inflate(sa, sp, da, dp, count)
        return count
    }

    fun encodeASCII(sa: CharArray, sp: Int, da: ByteArray, dp: Int, len: Int): Int {
        var i = 0
        var currentSp = sp
        var currentDp = dp

        while (i < len) {
            val c = sa[currentSp++]
            if (c >= '\u0080') break
            da[currentDp++] = c.code.toByte()
            i++
        }
        return i
    }

    fun inflateBytesToChars(src: ByteArray, srcOff: Int, dst: CharArray, dstOff: Int, len: Int) {
        StringLatin1.inflate(src, srcOff, dst, dstOff, len)
    }

}


internal object StringCoding {
    fun countPositives(ba: ByteArray, off: Int, len: Int): Int {
        val limit = off + len
        for (i in off until limit) {
            if (ba[i] < 0) {
                return i - off
            }
        }
        return len
    }
}


internal object StringLatin1 {
    // TODO: @IntrinsicCandidate
    fun inflate(src: ByteArray, srcOff: Int, dst: CharArray, dstOff: Int, len: Int) {
        var currentSrcOff = srcOff
        var currentDstOff = dstOff
        for (i in 0 until len) {
            dst[currentDstOff++] = (src[currentSrcOff++].toInt() and 0xff).toChar()
        }
    }
}