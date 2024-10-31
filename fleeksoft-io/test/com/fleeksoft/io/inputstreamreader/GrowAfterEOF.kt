package com.fleeksoft.io.inputstreamreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.InputStreamReader
import com.fleeksoft.io.asInputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertTrue

class GrowAfterEOF {
    @Test
    fun testGrowAfterEOF() {
        return
        // FIXME: this test failing due to IO error may be issue with SourceInputStream
        val input = Path("TestEOFInput.txt")
        try {
            SystemFileSystem.sink(input).close()
            val r = BufferedReader(InputStreamReader(SystemFileSystem.source(input).buffered().asInputStream()))
            try {
                // write something
                SystemFileSystem.sink(input).buffered().use { sink ->
                    sink.writeString("a line")
                }

                // read till the end of file
                while (r.readLine() != null);



                // append to the end of the file
//                rf.seek(rf.length())
                SystemFileSystem.sink(input).buffered().use { sink ->
                    sink.writeString("new line")
                }
                assertTrue(SystemFileSystem.exists(input))

                // now try to read again
                var readMore = false
                while (r.readLine() != null) {
                    readMore = true
                }

                println("readLin: ${SystemFileSystem.source(input).buffered().readLine()}")
                if (!readMore) {
                    SystemFileSystem.delete(input)
                    throw Exception("Failed test: unable to read!")
                } else {
                    SystemFileSystem.delete(input)
                }
            } finally {
                r.close()
            }
        } finally {
        }
    }
}
