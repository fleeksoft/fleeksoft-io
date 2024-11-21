package com.fleeksoft.io.stringreader

import com.fleeksoft.io.StringReader
import kotlin.test.Test

/* @test
   @bug 4153020
   @summary Negative marklimit value should throw an exception
   */
class NegativeMarkLimit {
    @Test
    fun main() {
        val `in`: StringReader = StringReader("aaaaaaaaaaaaaaa")
        try {
            `in`.mark(-1)
        } catch (e: IllegalArgumentException) {
            return
        }
        throw Exception(" Negative marklimit value should throw an exception")
    }
}
