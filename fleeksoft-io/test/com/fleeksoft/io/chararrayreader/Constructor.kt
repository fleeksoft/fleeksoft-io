package com.fleeksoft.io.chararrayreader

import com.fleeksoft.io.CharArrayReader
import kotlin.test.Test

class Constructor {
    @Test
    fun main() {
        val values: IntArray? = intArrayOf(
            Int.MIN_VALUE, -1, 0, 1, 4, 16, 31,
            32, 33, Int.MAX_VALUE
        )
        val b: Array<CharArray?> = arrayOf<CharArray?>(null, CharArray(32))

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
                while (k < values.size) {
                    nullPtr = (b[i] == null)

                    val bufLen = if (nullPtr) 0 else b[i]!!.size
                    indexOutBnd = (values[j] < 0)
                            || (values[j] > bufLen)
                            || (values[k] < 0)
                            || ((values[j] + values[k]) < 0)

                    try {
                        val rdr = CharArrayReader(b[i]!!, values[j], values[k])
                    } catch (e: NullPointerException) {
                        if (!nullPtr) {
                            throw Exception("should not throw NullPointerException")
                        }
                        k++
                        continue
                    } catch (e: IllegalArgumentException) {
                        if (!indexOutBnd) {
                            throw Exception("should not throw IllegalArgumentException")
                        }
                        k++
                        continue
                    }

                    if (nullPtr || indexOutBnd) {
                        throw Exception("Failed to detect illegal argument")
                    }
                    k++
                }
                j++
            }
            i++
        }
    }
}
