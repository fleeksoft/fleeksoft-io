package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM856 : Charset("x-IBM856", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM856)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u05D0\u05D1\u05D2\u05D3\u05D4\u05D5\u05D6\u05D7" +  // 0x80 - 0x87
                "\u05D8\u05D9\u05DA\u05DB\u05DC\u05DD\u05DE\u05DF" +  // 0x88 - 0x8f
                "\u05E0\u05E1\u05E2\u05E3\u05E4\u05E5\u05E6\u05E7" +  // 0x90 - 0x97
                "\u05E8\u05E9\u05EA\uFFFD\u00A3\uFFFD\u00D7\uFFFD" +  // 0x98 - 0x9f
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xa0 - 0xa7
                "\uFFFD\u00AE\u00AC\u00BD\u00BC\uFFFD\u00AB\u00BB" +  // 0xa8 - 0xaf
                "\u2591\u2592\u2593\u2502\u2524\uFFFD\uFFFD\uFFFD" +  // 0xb0 - 0xb7
                "\u00A9\u2563\u2551\u2557\u255D\u00A2\u00A5\u2510" +  // 0xb8 - 0xbf
                "\u2514\u2534\u252C\u251C\u2500\u253C\uFFFD\uFFFD" +  // 0xc0 - 0xc7
                "\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u00A4" +  // 0xc8 - 0xcf
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xd0 - 0xd7
                "\uFFFD\u2518\u250C\u2588\u2584\u00A6\uFFFD\u2580" +  // 0xd8 - 0xdf
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u00B5\uFFFD" +  // 0xe0 - 0xe7
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u203E\u00B4" +  // 0xe8 - 0xef
                "\u00AD\u00B1\u2017\u00BE\u00B6\u00A7\u00F7\u00B8" +  // 0xf0 - 0xf7
                "\u00B0\u00A8\u2022\u00B9\u00B3\u00B2\u25A0\u00A0" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" +  // 0x00 - 0x07
                "\b\t\n\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" +  // 0x10 - 0x17
                "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0020\u0021\"\u0023\u0024\u0025\u0026\'" +  // 0x20 - 0x27
                "\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +  // 0x28 - 0x2f
                "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +  // 0x30 - 0x37
                "\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +  // 0x38 - 0x3f
                "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0x40 - 0x47
                "\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +  // 0x48 - 0x4f
                "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" +  // 0x50 - 0x57
                "\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +  // 0x58 - 0x5f
                "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +  // 0x60 - 0x67
                "\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +  // 0x68 - 0x6f
                "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" +  // 0x70 - 0x77
                "\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F" // 0x78 - 0x7f


        val b2c = b2cTable.toCharArray()
        val c2b = CharArray(0x500)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            val c2bNR: CharArray? = null
            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
