package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.HKSCS

class MS950_HKSCS_XP : Charset("x-MS950-HKSCS-XP", null) {
    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is MS950) || (cs is MS950_HKSCS_XP))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    internal class Decoder(cs: Charset) : HKSCS.Decoder(cs, ms950, b2cBmp, null) {
        override fun decodeDoubleEx(b1: Int, b2: Int): Char {
            return CharsetMapping.UNMAPPABLE_DECODING
        }

        companion object {
            private val ms950 = MS950().newDecoder() as DoubleByte.Decoder

            /*
         * Note current decoder decodes 0x8BC2 --> U+F53A
         * ie. maps to Unicode PUA.
         * Unaccounted discrepancy between this mapping
         * inferred from MS950/windows-950 and the published
         * MS HKSCS mappings which maps 0x8BC2 --> U+5C22
         * a character defined with the Unified CJK block
         */
            private val b2cBmp = arrayOfNulls<CharArray>(0x100)

            init {
                initb2c(b2cBmp, HKSCS_XPMapping.b2cBmpStr)
            }
        }
    }

    private class Encoder(cs: Charset) : HKSCS.Encoder(cs, ms950, c2bBmp, null) {
        override fun encodeSupp(cp: Int): Int {
            return CharsetMapping.UNMAPPABLE_ENCODING
        }

        companion object {
            private val ms950 = MS950().newEncoder() as DoubleByte.Encoder

            /*
         * Note current encoder encodes U+F53A --> 0x8BC2
         * Published MS HKSCS mappings show
         * U+5C22 <--> 0x8BC2
         */
            val c2bBmp: Array<CharArray?> = arrayOfNulls<CharArray>(0x100)

            init {
                initc2b(c2bBmp, HKSCS_XPMapping.b2cBmpStr, null)
            }
        }
    }
}
