package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte

class IBM942C : Charset("x-IBM942C", null) {
    

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is IBM942C))
    }

    override fun newDecoder(): CharsetDecoder {
        return DoubleByte.Decoder(
            this,
            IBM942.DecodeHolder.b2c,
            Holder.b2cSB,
            0x40,
            0xfc
        )
    }

    override fun newEncoder(): CharsetEncoder {
        return DoubleByte.Encoder(this, Holder.c2b, Holder.c2bIndex)
    }

    private object Holder {
        // the mappings that need updating are
        //    u+001a  <-> 0x1a
        //    u+001c  <-> 0x1c
        //    u+005c  <-> 0x5c
        //    u+007e  <-> 0x7e
        //    u+007f  <-> 0x7f
        val b2cSB: CharArray = IBM942.DecodeHolder.b2cSB.copyOf()
        val c2b: CharArray
        val c2bIndex: CharArray

        init {
            b2cSB[0x1a] = 0x1a.toChar()
            b2cSB[0x1c] = 0x1c.toChar()
            b2cSB[0x5c] = 0x5c.toChar()
            b2cSB[0x7e] = 0x7e.toChar()
            b2cSB[0x7f] = 0x7f.toChar()

            c2b = IBM942.EncodeHolder.c2b.copyOf()
            c2bIndex = IBM942.EncodeHolder.c2bIndex.copyOf()
            c2b[c2bIndex[0].code + 0x1a] = 0x1a.toChar()
            c2b[c2bIndex[0].code + 0x1c] = 0x1c.toChar()
            c2b[c2bIndex[0].code + 0x5c] = 0x5c.toChar()
            c2b[c2bIndex[0].code + 0x7e] = 0x7e.toChar()
            c2b[c2bIndex[0].code + 0x7f] = 0x7f.toChar()
        }
    }
}
