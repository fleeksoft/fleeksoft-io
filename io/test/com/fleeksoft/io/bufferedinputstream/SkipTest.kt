import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.InputStream
import kotlin.test.Test

/**
 * This class tests to see if bufferinputstream can be reset
 * to recover data that was skipped over when the buffer did
 * not contain all the bytes to be skipped
 */
class SkipTest {
    @Test
    fun main() {
        var skipped: Long = 0

        // Create a tiny buffered stream so it can be easily
        // set up to contain only some of the bytes to skip
        val source = DataSupplier()
        val `in` = BufferedInputStream(source, 4)

        // Set up data to be skipped and recovered
        // the skip must be longer than the buffer size
        `in`.mark(30)
        while (skipped < 15) {
            skipped += `in`.skip(15 - skipped)
        }
        var nextint: Int = `in`.read()
        `in`.reset()

        // Resume reading and see if data was lost
        nextint = `in`.read()

        if (nextint != 'a'.code) throw RuntimeException("BufferedInputStream skip lost data")
    }
}


internal class DataSupplier : InputStream() {
    private var aposition = 0

    override fun read(): Int {
        return 'x'.code
    }

    override fun skip(n: Long): Long {
        aposition += n.toInt()
        return n
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var len = len
        if (len > buffer.size) len = buffer.size
        buffer.copyInto(b, off, aposition, aposition + len)
        return len
    }

    companion object {
        val buffer: ByteArray = byteArrayOf(
            'a'.code.toByte(), 'b'.code.toByte(), 'c'.code.toByte(),
            'd'.code.toByte(), 'e'.code.toByte(), 'f'.code.toByte(), 'g'.code.toByte(), 'h'.code.toByte(), 'i'.code.toByte(),
            'j'.code.toByte(), 'k'.code.toByte(), 'l'.code.toByte(), 'm'.code.toByte(), 'n'.code.toByte(), 'o'.code.toByte(),
            'p'.code.toByte(), 'q'.code.toByte(), 'r'.code.toByte(), 's'.code.toByte(), 't'.code.toByte(), 'u'.code.toByte(),
            'v'.code.toByte(), 'w'.code.toByte(), 'x'.code.toByte(), 'y'.code.toByte(), 'z'.code.toByte()
        )
    }
}
