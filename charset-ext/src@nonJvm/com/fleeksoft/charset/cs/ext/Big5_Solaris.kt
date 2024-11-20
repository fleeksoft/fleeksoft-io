package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.DoubleByte

class Big5_Solaris : Charset("x-Big5-Solaris", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is Big5)
                || (cs is Big5_Solaris))
    }

    override fun newDecoder(): CharsetDecoder {
        return DoubleByte.Decoder(this, Holder.b2c, Holder.b2cSB, 0x40, 0xfe, true)
    }

    override fun newEncoder(): CharsetEncoder {
        return DoubleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, true)
    }

    private object Holder {
        val b2c: Array<CharArray?> = Big5.DecodeHolder.b2c.copyOf()
        val b2cSB: CharArray
        val c2b: CharArray
        val c2bIndex: CharArray

        init {
            // Big5 Solaris implementation has 7 additional mappings
            var sol = intArrayOf(
                0xF9D6, 0x7881,
                0xF9D7, 0x92B9,
                0xF9D8, 0x88CF,
                0xF9D9, 0x58BB,
                0xF9DA, 0x6052,
                0xF9DB, 0x7CA7,
                0xF9DC, 0x5AFA
            )
            if (b2c[0xf9] == DoubleByte.B2C_UNMAPPABLE) {
                b2c[0xf9] = CharArray(0xfe - 0x40 + 1)
                b2c[0xf9]?.fill(CharsetMapping.UNMAPPABLE_DECODING)
            }

            run {
                var i = 0
                while (i < sol.size) {
                    b2c[0xf9]!![sol[i++] and 0xff - 0x40] = sol[i++].toChar()
                }
            }
            b2cSB = Big5.DecodeHolder.b2cSB

            c2b = Big5.EncodeHolder.c2b.copyOf()
            c2bIndex = Big5.EncodeHolder.c2bIndex.copyOf()
            sol = intArrayOf(
                0x7881, 0xF9D6,
                0x92B9, 0xF9D7,
                0x88CF, 0xF9D8,
                0x58BB, 0xF9D9,
                0x6052, 0xF9DA,
                0x7CA7, 0xF9DB,
                0x5AFA, 0xF9DC
            )

            var i = 0
            while (i < sol.size) {
                val c = sol[i++]
                // no need to check c2bIndex[c >>8], we know it points
                // to the appropriate place.
                c2b[c2bIndex[c shr 8].code + (c and 0xff)] = sol[i++].toChar()
            }
        }
    }
}
