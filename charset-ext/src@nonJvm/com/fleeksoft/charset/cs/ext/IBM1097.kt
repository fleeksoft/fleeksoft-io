package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM1097 : Charset("x-IBM1097", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM1097)
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
        private const val b2cTable = "\uFB8A\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +  // 0x80 - 0x87
                "\u0068\u0069\u00AB\u00BB\uFEB1\uFEB3\uFEB5\uFEB7" +  // 0x88 - 0x8f
                "\uFEB9\u006A\u006B\u006C\u006D\u006E\u006F\u0070" +  // 0x90 - 0x97
                "\u0071\u0072\uFEBB\uFEBD\uFEBF\uFEC1\uFEC3\uFEC5" +  // 0x98 - 0x9f
                "\uFEC7\u007E\u0073\u0074\u0075\u0076\u0077\u0078" +  // 0xa0 - 0xa7
                "\u0079\u007A\uFEC9\uFECA\uFECB\uFECC\uFECD\uFECE" +  // 0xa8 - 0xaf
                "\uFECF\uFED0\uFED1\uFED3\uFED5\uFED7\uFB8E\uFEDB" +  // 0xb0 - 0xb7
                "\uFB92\uFB94\u005B\u005D\uFEDD\uFEDF\uFEE1\u00D7" +  // 0xb8 - 0xbf
                "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0xc0 - 0xc7
                "\u0048\u0049\u00AD\uFEE3\uFEE5\uFEE7\uFEED\uFEE9" +  // 0xc8 - 0xcf
                "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050" +  // 0xd0 - 0xd7
                "\u0051\u0052\uFEEB\uFEEC\uFBA4\uFBFC\uFBFD\uFBFE" +  // 0xd8 - 0xdf
                "\\\u061F\u0053\u0054\u0055\u0056\u0057\u0058" +  // 0xe0 - 0xe7
                "\u0059\u005A\u0640\u06F0\u06F1\u06F2\u06F3\u06F4" +  // 0xe8 - 0xef
                "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +  // 0xf0 - 0xf7
                "\u0038\u0039\u06F5\u06F6\u06F7\u06F8\u06F9\u009F" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F" +  // 0x00 - 0x07
                "\u0097\u008D\u008E\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087" +  // 0x10 - 0x17
                "\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B" +  // 0x20 - 0x27
                "\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +  // 0x28 - 0x2f
                "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004" +  // 0x30 - 0x37
                "\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +  // 0x38 - 0x3f
                "\u0020\u00A0\u060C\u064B\uFE81\uFE82\uF8FA\uFE8D" +  // 0x40 - 0x47
                "\uFE8E\uF8FB\u00A4\u002E\u003C\u0028\u002B\u007C" +  // 0x48 - 0x4f
                "\u0026\uFE80\uFE83\uFE84\uF8F9\uFE85\uFE8B\uFE8F" +  // 0x50 - 0x57
                "\uFE91\uFB56\u0021\u0024\u002A\u0029\u003B\u00AC" +  // 0x58 - 0x5f
                "\u002D\u002F\uFB58\uFE95\uFE97\uFE99\uFE9B\uFE9D" +  // 0x60 - 0x67
                "\uFE9F\uFB7A\u061B\u002C\u0025\u005F\u003E\u003F" +  // 0x68 - 0x6f
                "\uFB7C\uFEA1\uFEA3\uFEA5\uFEA7\uFEA9\uFEAB\uFEAD" +  // 0x70 - 0x77
                "\uFEAF\u0060\u003A\u0023\u0040\'\u003D\"" // 0x78 - 0x7f


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