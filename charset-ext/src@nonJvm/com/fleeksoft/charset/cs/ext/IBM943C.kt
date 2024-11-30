package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte

class IBM943C : Charset("x-IBM943C", null) {
    

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is IBM943C))
    }

    override fun newDecoder(): CharsetDecoder {
        return DoubleByte.Decoder(
            this,
            IBM943.DecodeHolder.b2c,
            Holder.b2cSB,
            0x40,
            0xfc
        )
    }

    override fun newEncoder(): CharsetEncoder {
        return DoubleByte.Encoder(this, Holder.c2b, Holder.c2bIndex)
    }

    private object Holder {
        val b2cSB: CharArray = CharArray(0x100)
        val c2b: CharArray
        val c2bIndex: CharArray

        init {
            for (i in 0..0x7f) {
                b2cSB[i] = i.toChar()
            }
            for (i in 0x80..0xff) {
                b2cSB[i] = IBM943.DecodeHolder.b2cSB[i]
            }
            c2b = IBM943.EncodeHolder.c2b.copyOf()
            c2bIndex = IBM943.EncodeHolder.c2bIndex.copyOf()
            var c = '\u0000'
            while (c < '\u0080') {
                val index = c2bIndex[c.code shr 8].code
                c2b[index + (c.code and 0xff)] = c
                ++c
            }
        }
    }
}
