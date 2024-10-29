package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.InputStreamReader
import com.fleeksoft.io.asInputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.test.Test

class FileReadWriteTest {
    val input = Path("TestEOFInput.txt")

    @Test
    fun bufferedInputStreamFile() {
        println("bufferedInputStreamFile")
        SystemFileSystem.sink(input).buffered().use { sink ->
            repeat(100) {
                sink.writeString("Lin $it")
            }
        }

        val reader = BufferedReader(InputStreamReader(SystemFileSystem.source(input).asInputStream()))
        while (true) {
            val line = reader.readLine()
            println("lin: $line")
            if (line == null) {
                break
            }
        }


    }
}