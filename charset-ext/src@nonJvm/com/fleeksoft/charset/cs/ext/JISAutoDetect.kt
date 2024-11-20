package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Platform
import com.fleeksoft.charset.isWindows
import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.DelegatableDecoder
import com.fleeksoft.charset.cs.euc.EUC_JP
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory
import kotlin.math.min

class JISAutoDetect : Charset("x-JISAutoDetect", null) {
    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is SJIS) || (cs is EUC_JP) || (cs is ISO2022_JP) || (cs is JISAutoDetect))
    }

    override fun canEncode(): Boolean {
        return false
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        throw UnsupportedOperationException("Encoder not supported for this charset")
    }

    private class Decoder(cs: Charset) : CharsetDecoder(cs, 0.5f, 1.0f) {
        private var detectedDecoder: DelegatableDecoder? = null

        fun decodeLoop(decoder: DelegatableDecoder, src: ByteBuffer, dst: CharBuffer): CoderResult {
            (decoder as CharsetDecoder).reset()
            detectedDecoder = decoder
            return detectedDecoder!!.decodeLoop(src, dst)
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            if (detectedDecoder == null) {
                copyLeadingASCII(src, dst)

                // All ASCII?
                if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                // Overflow only if there is still ascii but no out buffer.
                if (!dst.hasRemaining() && isPlainASCII(src.get(src.position()))) return CoderResultInternal.OVERFLOW

                // We need to perform double, not float, arithmetic; otherwise
                // we lose low order bits when src is larger than 2**24.
                val cbufsiz = (src.limit() * maxCharsPerByte().toDouble()).toInt()
                val sandbox = CharBufferFactory.allocate(cbufsiz)

                // First try ISO-2022-JP, since there is no ambiguity
                val cs2022 = Charsets.forName("ISO-2022-JP")
                val dd2022
                        : DelegatableDecoder = cs2022.newDecoder() as DelegatableDecoder
                val src2022: ByteBuffer = src.asReadOnlyBuffer()
                val res2022: CoderResult = dd2022.decodeLoop(src2022, sandbox)
                if (!res2022.isError()) return decodeLoop(dd2022, src, dst)

                // We must choose between EUC and SJIS
                val csEUCJ = Charsets.forName(EUCJPName)
                val csSJIS = Charsets.forName(SJISName)

                val ddEUCJ: DelegatableDecoder = csEUCJ.newDecoder() as DelegatableDecoder
                val ddSJIS: DelegatableDecoder = csSJIS.newDecoder() as DelegatableDecoder

                val srcEUCJ: ByteBuffer = src.asReadOnlyBuffer()
                sandbox.clear()
                val resEUCJ: CoderResult = ddEUCJ.decodeLoop(srcEUCJ, sandbox)
                // If EUC decoding fails, must be SJIS
                if (resEUCJ.isError()) return decodeLoop(ddSJIS, src, dst)
                val srcSJIS: ByteBuffer = src.asReadOnlyBuffer()
                val sandboxSJIS: CharBuffer = CharBufferFactory.allocate(cbufsiz)
                val resSJIS: CoderResult = ddSJIS.decodeLoop(srcSJIS, sandboxSJIS)
                // If SJIS decoding fails, must be EUC
                if (resSJIS.isError()) return decodeLoop(ddEUCJ, src, dst)

                // From here on, we have some ambiguity, and must guess.

                // We prefer input that does not appear to end mid-character.
                if (srcEUCJ.position() > srcSJIS.position()) return decodeLoop(ddEUCJ, src, dst)

                if (srcEUCJ.position() < srcSJIS.position()) return decodeLoop(ddSJIS, src, dst)

                // end-of-input is after the first byte of the first char?
                if (src.position() == srcEUCJ.position()) return CoderResultInternal.UNDERFLOW

                // Use heuristic knowledge of typical Japanese text
                sandbox.flip()
                return decodeLoop(if (looksLikeJapanese(sandbox)) ddEUCJ else ddSJIS, src, dst)
            }

            return detectedDecoder!!.decodeLoop(src, dst)
        }

        override fun implReset() {
            detectedDecoder = null
        }

        override fun implFlush(out: CharBuffer): CoderResult {
            return if (detectedDecoder != null) detectedDecoder!!.implFlush(out)
            else super.implFlush(out)
        }


        companion object {
            private val SJISName = getSJISName()
            private val EUCJPName = "EUC_JP"
            private fun isPlainASCII(b: Byte): Boolean {
                return b >= 0 && b.toInt() != 0x1b
            }

            private fun copyLeadingASCII(src: ByteBuffer, dst: CharBuffer) {
                val start = src.position()
                val limit: Int = start + min(src.remaining(), dst.remaining())
                var b: Byte? = null
                var p: Int = start
                while (p < limit && isPlainASCII(src.get(p).also { b = it })) {
                    dst.put((b!!.toInt() and 0xff).toChar())
                    p++
                }
                src.position(p)
            }

            /**
             * Returned Shift_JIS Charset name is OS dependent
             */
            private fun getSJISName(): String {
                return if (Platform.isWindows()) ("windows-31J")
                else ("Shift_JIS")
            }
        }
    }

    companion object {
        private const val EUCJP_MASK = 0x01
        private const val SJIS2B_MASK = 0x02
        private const val SJIS1B_MASK = 0x04
        private const val EUCJP_KANA1_MASK = 0x08
        private const val EUCJP_KANA2_MASK = 0x10

        // A heuristic algorithm for guessing if EUC-decoded text really
        // might be Japanese text.  Better heuristics are possible...
        private fun looksLikeJapanese(cb: CharBuffer): Boolean {
            var hiragana = 0 // Fullwidth Hiragana
            var katakana = 0 // Halfwidth Katakana
            while (cb.hasRemaining()) {
                val c = cb.get()
                if (0x3040 <= c.code && c.code <= 0x309f && ++hiragana > 1) return true
                if (0xff65 <= c.code && c.code <= 0xff9f && ++katakana > 1) return true
            }
            return false
        }
    }
}
