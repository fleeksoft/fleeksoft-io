package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM864 : Charset("IBM864", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM864)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u00B0\u00B7\u2219\u221A\u2592\u2500\u2502\u253C" +  // 0x80 - 0x87
                "\u2524\u252C\u251C\u2534\u2510\u250C\u2514\u2518" +  // 0x88 - 0x8f
                "\u03B2\u221E\u03C6\u00B1\u00BD\u00BC\u2248\u00AB" +  // 0x90 - 0x97
                "\u00BB\uFEF7\uFEF8\uFFFD\uFFFD\uFEFB\uFEFC\uFFFD" +  // 0x98 - 0x9f
                "\u00A0\u00AD\uFE82\u00A3\u00A4\uFE84\uFFFD\uFFFD" +  // 0xa0 - 0xa7
                "\uFE8E\uFE8F\uFE95\uFE99\u060C\uFE9D\uFEA1\uFEA5" +  // 0xa8 - 0xaf
                "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667" +  // 0xb0 - 0xb7
                "\u0668\u0669\uFED1\u061B\uFEB1\uFEB5\uFEB9\u061F" +  // 0xb8 - 0xbf
                "\u00A2\uFE80\uFE81\uFE83\uFE85\uFECA\uFE8B\uFE8D" +  // 0xc0 - 0xc7
                "\uFE91\uFE93\uFE97\uFE9B\uFE9F\uFEA3\uFEA7\uFEA9" +  // 0xc8 - 0xcf
                "\uFEAB\uFEAD\uFEAF\uFEB3\uFEB7\uFEBB\uFEBF\uFEC1" +  // 0xd0 - 0xd7
                "\uFEC5\uFECB\uFECF\u00A6\u00AC\u00F7\u00D7\uFEC9" +  // 0xd8 - 0xdf
                "\u0640\uFED3\uFED7\uFEDB\uFEDF\uFEE3\uFEE7\uFEEB" +  // 0xe0 - 0xe7
                "\uFEED\uFEEF\uFEF3\uFEBD\uFECC\uFECE\uFECD\uFEE1" +  // 0xe8 - 0xef
                "\uFE7D\u0651\uFEE5\uFEE9\uFEEC\uFEF0\uFEF2\uFED0" +  // 0xf0 - 0xf7
                "\uFED5\uFEF5\uFEF6\uFEDD\uFED9\uFEF1\u25A0\uFFFD" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" +  // 0x00 - 0x07
                "\b\t\n\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" +  // 0x10 - 0x17
                "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0020\u0021\"\u0023\u0024\u066A\u0026\'" +  // 0x20 - 0x27
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
            var c2bNR: CharArray? = null
            // non-roundtrip c2b only entries
            c2bNR = CharArray(2)
            c2bNR[0] = 0x25.toChar()
            c2bNR[1] = 0x25.toChar()

            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
