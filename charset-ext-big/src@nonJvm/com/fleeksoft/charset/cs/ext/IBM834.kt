package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.DoubleByte

// EBCDIC DBCS-only Korean
open class IBM834 : Charset("x-IBM834", null) {
    override fun contains(cs: Charset): Boolean {
        return (cs is IBM834)
    }

    override fun newDecoder(): CharsetDecoder {
        return DoubleByte.Decoder_DBCSONLY(
            this, IBM933.DecodeHolder.b2c, null, 0x40, 0xfe
        ) // hardcode the b2min/max
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    protected class Encoder(cs: Charset) : DoubleByte.Encoder_DBCSONLY(
        cs, byteArrayOf(0xfe.toByte(), 0xfe.toByte()),
        IBM933.EncodeHolder.c2b, IBM933.EncodeHolder.c2bIndex, false
    ) {
        override fun encodeChar(ch: Char): Int {
            val bb = super.encodeChar(ch)
            if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                // Cp834 has 6 additional non-roundtrip char->bytes
                // mappings, see#6379808
                if (ch == '\u00b7') {
                    return 0x4143
                } else if (ch == '\u00ad') {
                    return 0x4148
                } else if (ch == '\u2015') {
                    return 0x4149
                } else if (ch == '\u223c') {
                    return 0x42a1
                } else if (ch == '\uff5e') {
                    return 0x4954
                } else if (ch == '\u2299') {
                    return 0x496f
                }
            }
            return bb
        }

        override fun isLegalReplacement(repl: ByteArray): Boolean {
            if (repl.size == 2 && repl[0] == 0xfe.toByte() && repl[1] == 0xfe.toByte()) return true
            return super.isLegalReplacement(repl)
        }
    }
}
