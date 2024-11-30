package com.fleeksoft.io.kotlinx.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.kotlinx.asInputStream
import com.fleeksoft.io.exception.OutOfMemoryError
import com.fleeksoft.io.kotlinx.BuildConfig
import com.fleeksoft.io.reader
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

/* @test
   @summary BufferedReader should throw an OutOfMemoryError when the
            read-ahead limit is very large
   @bug 6350733
   @build BigMark
   @run main/othervm BigMark
*/
class BigMark {
    @Test
    fun main() {
        var line: String?
        var i = 0
        val inputStreamReader =
            SystemFileSystem.source(Path("${BuildConfig.PROJECT_ROOT}/kotlinx-io/test/com/fleeksoft/io/kotlinx/bufferedreader/BigMark.kt")).buffered()
                .asInputStream().reader()
        val br = BufferedReader(inputStreamReader, 100)

        br.mark(200)
        line = br.readLine()
        println("$i: $line")
        i++

        try {
            // BR.fill() call to new char[Int.MAX_VALUE] should succeed
            br.mark(Int.MAX_VALUE)
            line = br.readLine()
        } catch (x: OutOfMemoryError) {
            x.printStackTrace()
            throw x
        }
        println("OutOfMemoryError not thrown as expected")
    }
}
