package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM280 : Charset("IBM280", null) {
    

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM280)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, true)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u00D8\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +  // 0x80 - 0x87
                "\u0068\u0069\u00AB\u00BB\u00F0\u00FD\u00FE\u00B1" +  // 0x88 - 0x8f
                "\u005B\u006A\u006B\u006C\u006D\u006E\u006F\u0070" +  // 0x90 - 0x97
                "\u0071\u0072\u00AA\u00BA\u00E6\u00B8\u00C6\u00A4" +  // 0x98 - 0x9f
                "\u00B5\u00EC\u0073\u0074\u0075\u0076\u0077\u0078" +  // 0xa0 - 0xa7
                "\u0079\u007A\u00A1\u00BF\u00D0\u00DD\u00DE\u00AE" +  // 0xa8 - 0xaf
                "\u00A2\u0023\u00A5\u00B7\u00A9\u0040\u00B6\u00BC" +  // 0xb0 - 0xb7
                "\u00BD\u00BE\u00AC\u007C\u00AF\u00A8\u00B4\u00D7" +  // 0xb8 - 0xbf
                "\u00E0\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0xc0 - 0xc7
                "\u0048\u0049\u00AD\u00F4\u00F6\u00A6\u00F3\u00F5" +  // 0xc8 - 0xcf
                "\u00E8\u004A\u004B\u004C\u004D\u004E\u004F\u0050" +  // 0xd0 - 0xd7
                "\u0051\u0052\u00B9\u00FB\u00FC\u0060\u00FA\u00FF" +  // 0xd8 - 0xdf
                "\u00E7\u00F7\u0053\u0054\u0055\u0056\u0057\u0058" +  // 0xe0 - 0xe7
                "\u0059\u005A\u00B2\u00D4\u00D6\u00D2\u00D3\u00D5" +  // 0xe8 - 0xef
                "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +  // 0xf0 - 0xf7
                "\u0038\u0039\u00B3\u00DB\u00DC\u00D9\u00DA\u009F" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F" +  // 0x00 - 0x07
                "\u0097\u008D\u008E\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u009D\n\b\u0087" +  // 0x10 - 0x17
                "\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B" +  // 0x20 - 0x27
                "\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +  // 0x28 - 0x2f
                "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004" +  // 0x30 - 0x37
                "\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +  // 0x38 - 0x3f
                "\u0020\u00A0\u00E2\u00E4\u007B\u00E1\u00E3\u00E5" +  // 0x40 - 0x47
                "\\\u00F1\u00B0\u002E\u003C\u0028\u002B\u0021" +  // 0x48 - 0x4f
                "\u0026\u005D\u00EA\u00EB\u007D\u00ED\u00EE\u00EF" +  // 0x50 - 0x57
                "\u007E\u00DF\u00E9\u0024\u002A\u0029\u003B\u005E" +  // 0x58 - 0x5f
                "\u002D\u002F\u00C2\u00C4\u00C0\u00C1\u00C3\u00C5" +  // 0x60 - 0x67
                "\u00C7\u00D1\u00F2\u002C\u0025\u005F\u003E\u003F" +  // 0x68 - 0x6f
                "\u00F8\u00C9\u00CA\u00CB\u00C8\u00CD\u00CE\u00CF" +  // 0x70 - 0x77
                "\u00CC\u00F9\u003A\u00A3\u00A7\'\u003D\"" // 0x78 - 0x7f


        val b2c = b2cTable.toCharArray()
        val c2b = CharArray(0x100)
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
