package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class MacCentralEurope : Charset("x-MacCentralEurope", null) {


    override fun contains(cs: Charset): Boolean {
        return (cs is MacCentralEurope)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u00C4\u0100\u0101\u00C9\u0104\u00D6\u00DC\u00E1" +  // 0x80 - 0x87
                "\u0105\u010C\u00E4\u010D\u0106\u0107\u00E9\u0179" +  // 0x88 - 0x8f
                "\u017A\u010E\u00ED\u010F\u0112\u0113\u0116\u00F3" +  // 0x90 - 0x97
                "\u0117\u00F4\u00F6\u00F5\u00FA\u011A\u011B\u00FC" +  // 0x98 - 0x9f
                "\u2020\u00B0\u0118\u00A3\u00A7\u2022\u00B6\u00DF" +  // 0xa0 - 0xa7
                "\u00AE\u00A9\u2122\u0119\u00A8\u2260\u0123\u012E" +  // 0xa8 - 0xaf
                "\u012F\u012A\u2264\u2265\u012B\u0136\u2202\u2211" +  // 0xb0 - 0xb7
                "\u0142\u013B\u013C\u013D\u013E\u0139\u013A\u0145" +  // 0xb8 - 0xbf
                "\u0146\u0143\u00AC\u221A\u0144\u0147\u2206\u00AB" +  // 0xc0 - 0xc7
                "\u00BB\u2026\u00A0\u0148\u0150\u00D5\u0151\u014C" +  // 0xc8 - 0xcf
                "\u2013\u2014\u201C\u201D\u2018\u2019\u00F7\u25CA" +  // 0xd0 - 0xd7
                "\u014D\u0154\u0155\u0158\u2039\u203A\u0159\u0156" +  // 0xd8 - 0xdf
                "\u0157\u0160\u201A\u201E\u0161\u015A\u015B\u00C1" +  // 0xe0 - 0xe7
                "\u0164\u0165\u00CD\u017D\u017E\u016A\u00D3\u00D4" +  // 0xe8 - 0xef
                "\u016B\u016E\u00DA\u016F\u0170\u0171\u0172\u0173" +  // 0xf0 - 0xf7
                "\u00DD\u00FD\u0137\u017B\u0141\u017C\u0122\u02C7" +  // 0xf8 - 0xff
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
        val c2b = CharArray(0x700)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            val c2bNR: CharArray? = null
            initC2B(
                b2cMap,
                c2bNR,
                c2b,
                c2bIndex
            )
        }
    }
}
