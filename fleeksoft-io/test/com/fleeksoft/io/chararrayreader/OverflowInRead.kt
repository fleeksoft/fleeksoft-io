package com.fleeksoft.io.chararrayreader

import com.fleeksoft.io.CharArrayReader
import kotlin.test.Test

class OverflowInRead {
    @Test
    fun main() {
        val a = "_123456789_123456789_123456789_123456789".toCharArray() // a.length > 33
        CharArrayReader(a).use { car ->
            val len1 = 33
            val buf1 = CharArray(len1)
            if (car.read(buf1, 0, len1) != len1) throw Exception("Expected to read $len1 chars")

            val len2: Int = 500
            val buf2 = CharArray(len2)
            val expLen2 = a.size - len1
            if (car.read(buf2, 0, len2) != expLen2) throw Exception("Expected to read $expLen2 chars")
        }
    }
}
