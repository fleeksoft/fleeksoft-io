package com.fleeksoft.io.chararrayreader

import com.fleeksoft.io.CharArrayReader
import kotlin.test.Test

class OverflowInSkip {
    @Test
    fun main() {
        val a = "_123456789_123456789_123456789_123456789"
            .toCharArray() // a.length > 33
        CharArrayReader(a).use { car ->
            val small: Long = 33
            val big = Long.MAX_VALUE

            val smallSkip: Long = car.skip(small)
            if (smallSkip != small) throw Exception(
                ("Expected to skip " + small
                        + " chars, but skipped " + smallSkip)
            )

            val expSkip = a.size - small
            val bigSkip: Long = car.skip(big)
            if (bigSkip != expSkip) throw Exception(
                ("Expected to skip " + expSkip
                        + " chars, but skipped " + bigSkip)
            )
        }
    }
}
