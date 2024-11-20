package com.fleeksoft.charset.cs.ms

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte

class MS1257 private constructor() : Charset("windows-1257", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is MS1257))
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, true, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, true)
    }

    private object Holder {
        private const val b2cTable = "\u20AC\uFFFD\u201A\uFFFD\u201E\u2026\u2020\u2021" +  // 0x80 - 0x87
                "\uFFFD\u2030\uFFFD\u2039\uFFFD\u00A8\u02C7\u00B8" +  // 0x88 - 0x8f
                "\uFFFD\u2018\u2019\u201C\u201D\u2022\u2013\u2014" +  // 0x90 - 0x97
                "\uFFFD\u2122\uFFFD\u203A\uFFFD\u00AF\u02DB\uFFFD" +  // 0x98 - 0x9f
                "\u00A0\uFFFD\u00A2\u00A3\u00A4\uFFFD\u00A6\u00A7" +  // 0xa0 - 0xa7
                "\u00D8\u00A9\u0156\u00AB\u00AC\u00AD\u00AE\u00C6" +  // 0xa8 - 0xaf
                "\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7" +  // 0xb0 - 0xb7
                "\u00F8\u00B9\u0157\u00BB\u00BC\u00BD\u00BE\u00E6" +  // 0xb8 - 0xbf
                "\u0104\u012E\u0100\u0106\u00C4\u00C5\u0118\u0112" +  // 0xc0 - 0xc7
                "\u010C\u00C9\u0179\u0116\u0122\u0136\u012A\u013B" +  // 0xc8 - 0xcf
                "\u0160\u0143\u0145\u00D3\u014C\u00D5\u00D6\u00D7" +  // 0xd0 - 0xd7
                "\u0172\u0141\u015A\u016A\u00DC\u017B\u017D\u00DF" +  // 0xd8 - 0xdf
                "\u0105\u012F\u0101\u0107\u00E4\u00E5\u0119\u0113" +  // 0xe0 - 0xe7
                "\u010D\u00E9\u017A\u0117\u0123\u0137\u012B\u013C" +  // 0xe8 - 0xef
                "\u0161\u0144\u0146\u00F3\u014D\u00F5\u00F6\u00F7" +  // 0xf0 - 0xf7
                "\u0173\u0142\u015B\u016B\u00FC\u017C\u017E\u02D9" +  // 0xf8 - 0xff
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


        val b2c: CharArray = b2cTable.toCharArray()
        val c2b = CharArray(0x600)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap: CharArray = b2c
            val c2bNR: CharArray? = null
            SingleByte.initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }

    companion object {
        val INSTANCE = MS1257()
    }
}
