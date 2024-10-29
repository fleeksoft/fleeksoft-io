import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.InputStream
import kotlin.test.Test

/**
 * This class tests to see if BufferedInputStream of zero length array
 * invokes the read method or not. Invoking read could block which is
 * incompatible behavior for zero length array.
 */
class ReadZeroBytes {
    @Test
    fun main() {
        val `in` = BufferedInputStream(ThrowingInputStream())
        `in`.read(ByteArray(0), 0, 0)
    }
}

internal class ThrowingInputStream : InputStream() {
    override fun read(): Int {
        return 0
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        throw RuntimeException("Read invoked for len == 0")
    }
}
