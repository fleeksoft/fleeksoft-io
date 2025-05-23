package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.HKSCS

class Big5_HKSCS_2001 : Charset("x-Big5-HKSCS-2001", null) {
    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is Big5)
                || (cs is Big5_HKSCS_2001))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : HKSCS.Decoder(cs, big5, b2cBmp, b2cSupp) {
        companion object {
            private val big5 = Big5().newDecoder() as DoubleByte.Decoder

            private val b2cBmp = arrayOfNulls<CharArray>(0x100)
            private val b2cSupp = arrayOfNulls<CharArray>(0x100)

            init {
                initb2c(
                    b2cBmp,
                    HKSCS2001Mapping.b2cBmpStr
                )
                initb2c(
                    b2cSupp,
                    HKSCS2001Mapping.b2cSuppStr
                )
            }
        }
    }

    private class Encoder(cs: Charset) : HKSCS.Encoder(cs, big5, c2bBmp, c2bSupp) {
        companion object {
            private val big5 = Big5().newEncoder() as DoubleByte.Encoder

            val c2bBmp: Array<CharArray?> = arrayOfNulls<CharArray>(0x100)
            val c2bSupp: Array<CharArray?> = arrayOfNulls<CharArray>(0x100)

            init {
                initc2b(
                    c2bBmp, HKSCS2001Mapping.b2cBmpStr,
                    HKSCS2001Mapping.pua
                )
                initc2b(c2bSupp, HKSCS2001Mapping.b2cSuppStr, null)
            }
        }
    }
}
