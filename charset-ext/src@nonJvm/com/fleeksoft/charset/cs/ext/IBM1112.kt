package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM1112 : Charset("x-IBM1112", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM1112)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(
            this,
            Holder.c2b,
            Holder.c2bIndex,
            false
        )
    }

    private object Holder {
        private val b2cTable = "\u00D8\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +  // 0x80 - 0x87
                "\u0068\u0069\u00AB\u00BB\u0101\u017C\u0144\u00B1" +  // 0x88 - 0x8f
                "\u00B0\u006A\u006B\u006C\u006D\u006E\u006F\u0070" +  // 0x90 - 0x97
                "\u0071\u0072\u0156\u0157\u00E6\u0137\u00C6\u00A4" +  // 0x98 - 0x9f
                "\u00B5\u007E\u0073\u0074\u0075\u0076\u0077\u0078" +  // 0xa0 - 0xa7
                "\u0079\u007A\u201D\u017A\u0100\u017B\u0143\u00AE" +  // 0xa8 - 0xaf
                "\u005E\u00A3\u012B\u00B7\u00A9\u00A7\u00B6\u00BC" +  // 0xb0 - 0xb7
                "\u00BD\u00BE\u005B\u005D\u0179\u0136\u013C\u00D7" +  // 0xb8 - 0xbf
                "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0xc0 - 0xc7
                "\u0048\u0049\u00AD\u014D\u00F6\u0146\u00F3\u00F5" +  // 0xc8 - 0xcf
                "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050" +  // 0xd0 - 0xd7
                "\u0051\u0052\u00B9\u0107\u00FC\u0142\u015B\u2019" +  // 0xd8 - 0xdf
                "\\\u00F7\u0053\u0054\u0055\u0056\u0057\u0058" +  // 0xe0 - 0xe7
                "\u0059\u005A\u00B2\u014C\u00D6\u0145\u00D3\u00D5" +  // 0xe8 - 0xef
                "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +  // 0xf0 - 0xf7
                "\u0038\u0039\u00B3\u0106\u00DC\u0141\u015A\u009F" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F" +  // 0x00 - 0x07
                "\u0097\u008D\u008E\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u009D\n\b\u0087" +  // 0x10 - 0x17
                "\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B" +  // 0x20 - 0x27
                "\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +  // 0x28 - 0x2f
                "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004" +  // 0x30 - 0x37
                "\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +  // 0x38 - 0x3f
                "\u0020\u00A0\u0161\u00E4\u0105\u012F\u016B\u00E5" +  // 0x40 - 0x47
                "\u0113\u017E\u00A2\u002E\u003C\u0028\u002B\u007C" +  // 0x48 - 0x4f
                "\u0026\u00E9\u0119\u0117\u010D\u0173\u201E\u201C" +  // 0x50 - 0x57
                "\u0123\u00DF\u0021\u0024\u002A\u0029\u003B\u00AC" +  // 0x58 - 0x5f
                "\u002D\u002F\u0160\u00C4\u0104\u012E\u016A\u00C5" +  // 0x60 - 0x67
                "\u0112\u017D\u00A6\u002C\u0025\u005F\u003E\u003F" +  // 0x68 - 0x6f
                "\u00F8\u00C9\u0118\u0116\u010C\u0172\u012A\u013B" +  // 0x70 - 0x77
                "\u0122\u0060\u003A\u0023\u0040\'\u003D\"" // 0x78 - 0x7f


        val b2c = b2cTable.toCharArray()
        val c2b = CharArray(0x300)
        val c2bIndex = CharArray(0x100)

        init {
            var b2cMap = b2c
            var c2bNR: CharArray? = null
            // remove non-roundtrip entries
            b2cMap = b2cTable.toCharArray()
            b2cMap[165] = CharsetMapping.UNMAPPABLE_DECODING

            // non-roundtrip c2b only entries
            c2bNR = CharArray(2)
            c2bNR[0] = 0x15.toChar()
            c2bNR[1] = 0x85.toChar()

            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
