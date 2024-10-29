package com.fleeksoft.io.bytearrayinputstream

import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStream
import kotlin.test.Test

/* @test
 * @bug 6720170
 * @summary check for ByteArrayInputStream.skip
 */
class Skip {
    @Test
    fun main() {
        val total = 1024
        val `in`: ByteArrayInputStream = ByteArrayInputStream(ByteArray(total))

        /* test for skip */
        dotest(`in`, 0, total.toLong(), 23, 23)
        dotest(`in`, 10, total.toLong(), 23, 23)

        /* test for negative skip */
        dotest(`in`, 0, total.toLong(), -23, 0)

        /* check for skip after EOF */
        dotest(`in`, -1, total.toLong(), 20, 0)

        /* check for skip beyond EOF starting from before EOF */
        dotest(`in`, 0, total.toLong(), (total + 20).toLong(), total.toLong())

        /* check for skip if the pos + toskip causes integer overflow */
        dotest(`in`, 10, total.toLong(), Long.MAX_VALUE, (total - 10).toLong())
    }

    private fun dotest(`in`: InputStream, curpos: Int, total: Long, toskip: Long, expected: Long) {
        println(
            "\nCurrently at pos = " + curpos +
                    "\nTotal bytes in the stream = " + total +
                    "\nNumber of bytes to skip = " + toskip +
                    "\nNumber of bytes that should be skipped = " +
                    expected
        )

        // position to curpos; EOF if negative
        `in`.reset()
        val avail = if (curpos >= 0) curpos else `in`.available()
        val n: Long = `in`.skip(avail.toLong())
        if (n != avail.toLong()) {
            throw RuntimeException("Unexpected number of bytes skipped = $n")
        }

        val skipped: Long = `in`.skip(toskip)
        println("actual number skipped: $skipped")

        if (skipped != expected) {
            throw RuntimeException("Unexpected number of bytes skipped = $skipped")
        }
    }
}
