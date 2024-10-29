package com.fleeksoft.io.inputstreamreader

import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStreamReader

/* @test
   @bug 4401798
   @summary Check that single-character reads work properly
 */
class One {
    @kotlin.test.Test
    fun testOne() {
        test("x")
        test("xy")
        test("xyz")
        test("\ud800\udc00")
        test("x\ud800\udc00")
    }

    private fun test(expect: String) {
        val `in`: ByteArray = expect.encodeToByteArray()

        object : Test(`in`, expect) {
            public override fun read() {
                while (true) {
                    val c: Int
                    if ((isr.read().also { c = it }) == -1) break
//                    sb.append(c.toChar())
                    sb += c.toChar()
                }
            }
        }

        object : Test(`in`, expect) {
            public override fun read() {
                while (true) {
                    val cb = CharArray(1)
                    if (isr.read(cb) == -1) break
//                    sb.append(cb[0])
                    sb += cb[0]
                }
            }
        }

        object : Test(`in`, expect) {
            public override fun read() {
                while (true) {
                    val cb = CharArray(2)
                    val n: Int
                    if ((isr.read(cb).also { n = it }) == -1) break
//                    sb.append(cb[0])
                    sb += cb[0]
//                    if (n == 2) sb.append(cb[1])
                    if (n == 2) sb += cb[1]
                }
            }
        }
    }

    private abstract class Test(`in`: ByteArray, var expect: String) {
        var isr: InputStreamReader = InputStreamReader(ByteArrayInputStream(`in`), "UTF-8")
        var sb: String = "" // FIXME: replace with StringBuffer

        init {
            go()
        }

        fun go() {
            read()
            if (expect != sb.toString()) throw Exception(
                ("Expected $expect, got $sb")
            )
        }

        abstract fun read()
    }
}
