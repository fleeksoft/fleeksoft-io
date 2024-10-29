package com.fleeksoft.io.reader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.CharArrayReader
import com.fleeksoft.io.InputStreamReader
import com.fleeksoft.io.PushbackReader
import com.fleeksoft.io.Reader
import com.fleeksoft.io.StringReader
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.reader.OpsAfterCloseTest.OpsAfterClose.entries
import kotlin.test.Test

/**
 * @test
 * @bug 5085148 4143651
 * @summary Test if Reader methods will check if the stream
 * has been closed.
 */
class OpsAfterCloseTest {
    @Test
    fun main() {
        var failed = false

        val br = BufferedReader(
            StringReader("abc def ghi")
        )
        if (testReader(br)) {
            failed = true
        }

        val car = CharArrayReader(CharArray(2))
        if (testReader(car)) {
            failed = true
        }

        val pbr = PushbackReader(
            CharArrayReader(CharArray(2))
        )
        if (testReader(pbr)) {
            failed = true
        }
        if (testPushbackReader(pbr)) {
            failed = true
        }

        val sr = StringReader("abc def ghi")
        if (testReader(sr)) {
            failed = true
        }

        val isr = InputStreamReader(
            ByteArrayInputStream("abc".encodeToByteArray())
        )
        if (testReader(isr)) {
            failed = true
        }

        if (failed) {
            throw Exception(
                "Test failed for some of the operation{s}" + " on some of the reader{s}, check the messages"
            )
        }
    }

    private fun testReader(r: Reader): Boolean {
        r.close()
        var failed = false
        var result: Boolean
        println("Testing reader:$r")
        for (op in entries) {
            result = op.check(r)
            if (!result) {
                failed = true
            }
            println("$op:$result")
        }
        if (failed) {
            println(
                "Test failed for the failed operation{s} above for the Reader:$r"
            )
        }
        return failed
    }

    private fun testPushbackReader(pr: PushbackReader): Boolean {
        var failed = false
        try {
            pr.unread(1)
            println("Test failed for unread(int):$pr")
            failed = true
        } catch (io: IOException) {
            println("UNREAD(int):true")
        }

        val buf = CharArray(2)
        try {
            pr.unread(buf, 0, 2)
            println(
                "Test failed for unread(buf, offset, len):$pr"
            )
            failed = true
        } catch (io: IOException) {
            println("UNREAD(buf, offset, len):true")
        }
        try {
            pr.unread(buf)
            println("Test failed for unread(char[] buf):$pr")
            failed = true
        } catch (io: IOException) {
            println("UNREAD(buf):true")
        }
        return failed
    }

    enum class OpsAfterClose {
        READ {
            override fun check(r: Reader): Boolean {
                try {
                    r.read()
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },

        READ_BUF {
            override fun check(r: Reader): Boolean {
                try {
                    val buf = CharArray(2)
                    val len = 1
                    r.read(buf, 0, len)
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },
        READY {
            override fun check(r: Reader): Boolean {
                try {
                    r.ready()
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },
        MARK {
            override fun check(r: Reader): Boolean {
                try {
                    r.mark(1)
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },
        SKIP {
            override fun check(r: Reader): Boolean {
                try {
                    r.skip(1)
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },
        RESET {
            override fun check(r: Reader): Boolean {
                try {
                    r.reset()
                } catch (io: IOException) {
                    return true
                }
                return false
            }
        },
        CLOSE {
            override fun check(r: Reader): Boolean {
                try {
                    r.close()
                } catch (io: IOException) {
                    return false
                }
                return true
            }
        };

        abstract fun check(r: Reader): Boolean
    }
}
