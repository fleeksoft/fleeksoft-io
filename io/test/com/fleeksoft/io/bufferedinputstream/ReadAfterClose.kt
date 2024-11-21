import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.IOException
import kotlin.test.Test

class ReadAfterClose {
    fun testRead(`in`: InputStream) {
        `in`.close()
        val buf = ByteArray(2)

        try {
            `in`.read(buf, 0, 1)
            throw Exception("Should not allow read on a closed stream")
        } catch (e: IOException) {
        }

        try {
            `in`.read(buf, 0, 0)
            throw Exception("Should not allow read on a closed stream")
        } catch (e: IOException) {
        }
    }

    @Test
    fun main() {
        val bis = BufferedInputStream(ByteArrayInputStream(ByteArray(32)))
        testRead(bis)
    }
}
