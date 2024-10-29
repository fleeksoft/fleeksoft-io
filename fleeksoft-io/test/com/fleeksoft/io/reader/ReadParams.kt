package com.fleeksoft.io.reader

import com.fleeksoft.io.*
import kotlin.test.Test

/*
 * @test
 * @bug 4127657
 * @summary Check for correct handling of parameters to
 *          XXXXReader.read(b, off, len).
 *
 */
class ReadParams {
    var values: IntArray? = intArrayOf(
        Int.MIN_VALUE, -1, 0, 1, 4, 16, 31,
        32, 33, Int.MAX_VALUE
    )
    var b: Array<CharArray?> = arrayOf<CharArray?>(null, CharArray(32))

    @Test
    fun main() {
        val sr = StringReader(ByteArray(512).decodeToString())
        test(sr)

        test(BufferedReader(sr))

        test(CharArrayReader(CharArray(8)))

        val ir = InputStreamReader(ByteArrayInputStream(ByteArray(512)))
        test(ir)

        test(PushbackReader(sr, 2))

//        val pw = PipedWriter()
//        val pir = PipedReader(pw)
//        pw.write(CharArray(512), 0, 512)
//        test(pir)
    }

    fun test(rdr: Reader) {
        var i = 0
        var j = 0
        var k = 0
        var nullPtr = false
        var indexOutBnd = false

        i = 0
        while (i < b.size) {
            j = 0
            while (j < values!!.size) {
                k = 0
                while (k < values!!.size) {
                    nullPtr = (b[i] == null)

                    val bufLen = if (nullPtr) 0 else b[i]!!.size
                    indexOutBnd = ((values!![j] + values!![k]) < 0)
                            || (values!![j] < 0)
                            || (values!![j] > bufLen)
                            || (values!![k] < 0)
                            || ((values!![j] + values!![k]) > bufLen)

                    try {
                        rdr.read(b[i]!!, values!![j], values!![k])
                    } catch (e: NullPointerException) {
                        if (!nullPtr) {
                            throw Exception("should not throw NullPointerException$i $j $k")
                        }
                        k++
                        continue
                    } catch (e: IndexOutOfBoundsException) {
                        if (!indexOutBnd) {
                            throw Exception("should not throw IndexOutOfBoundsException")
                        }
                        k++
                        continue
                    }

                    if (nullPtr || indexOutBnd) {
                        throw Exception("Should have thrown an exception")
                    }
                    k++
                }
                j++
            }
            i++
        }
    }
}
