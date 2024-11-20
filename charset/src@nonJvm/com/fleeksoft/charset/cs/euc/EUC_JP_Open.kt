package com.fleeksoft.charset.cs.euc

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.jis.JIS_X_0201
import com.fleeksoft.charset.cs.jis.JIS_X_0208_Solaris
import com.fleeksoft.charset.cs.jis.JIS_X_0212_Solaris

class EUC_JP_Open : Charset("x-eucJP-Open", null) {
    companion object {
        val INSTANCE = EUC_JP_Open()
    }

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is JIS_X_0201) || (cs is EUC_JP) || (cs is EUC_JP_Open))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) :
        EUC_JP.Decoder(cs, 0.5f, 1.0f, DEC0201, DEC0208, DEC0212_Solaris) {
        override fun decodeDouble(byte1: Int, byte2: Int): Char {
            val c: Char = super.decodeDouble(byte1, byte2)
            if (c == CharsetMapping.UNMAPPABLE_DECODING) return DEC0208_Solaris.decodeDouble(
                byte1 - 0x80,
                byte2 - 0x80
            )
            return c
        }

        companion object {
            private val DEC0208_Solaris = JIS_X_0208_Solaris().newDecoder() as DoubleByte.Decoder
            private val DEC0212_Solaris = JIS_X_0212_Solaris().newDecoder() as DoubleByte.Decoder?
        }
    }

    private class Encoder(cs: Charset) : EUC_JP.Encoder(cs) {
        override fun encodeDouble(ch: Char): Int {
            var b: Int = super.encodeDouble(ch)
            if (b != CharsetMapping.UNMAPPABLE_ENCODING) return b
            b = ENC0208_Solaris.encodeChar(ch)
            if (b != CharsetMapping.UNMAPPABLE_ENCODING && b > 0x7500) {
                return 0x8F8080 + ENC0212_Solaris.encodeChar(ch)
            }
            return if (b == CharsetMapping.UNMAPPABLE_ENCODING) b else b + 0x8080
        }

        companion object {
            private val ENC0208_Solaris = JIS_X_0208_Solaris().newEncoder() as DoubleByte.Encoder

            private val ENC0212_Solaris = JIS_X_0212_Solaris().newEncoder() as DoubleByte.Encoder
        }
    }
}
