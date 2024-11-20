package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.HKSCS


class MS950_HKSCS : Charset("x-MS950-HKSCS", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is MS950) || (cs is MS950_HKSCS))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    internal class Decoder(cs: Charset) : HKSCS.Decoder(cs, ms950, b2cBmp, b2cSupp) {
        companion object {
            private val ms950: DoubleByte.Decoder = MS950().newDecoder() as DoubleByte.Decoder

            private val b2cBmp = arrayOfNulls<CharArray>(0x100)
            private val b2cSupp = arrayOfNulls<CharArray>(0x100)

            init {
                initb2c(b2cBmp, HKSCSMapping.b2cBmpStr)
                initb2c(b2cSupp, HKSCSMapping.b2cSuppStr)
            }
        }
    }

    private class Encoder(cs: Charset) : HKSCS.Encoder(cs, ms950, c2bBmp, c2bSupp) {
        companion object {
            private val ms950: DoubleByte.Encoder = MS950().newEncoder() as DoubleByte.Encoder

            val c2bBmp: Array<CharArray?> = arrayOfNulls<CharArray>(0x100)
            val c2bSupp: Array<CharArray?> = arrayOfNulls<CharArray>(0x100)

            init {
                initc2b(c2bBmp, HKSCSMapping.b2cBmpStr, HKSCSMapping.pua)
                initc2b(c2bSupp, HKSCSMapping.b2cSuppStr, null)
            }
        }
    }
}
