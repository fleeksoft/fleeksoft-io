package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM1046 : Charset("x-IBM1046", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM1046)
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
        private const val b2cTable = "\uFE88\u00D7\u00F7\uF8F6\uF8F5\uF8F4\uF8F7\uFE71" +  // 0x80 - 0x87
                "\u0088\u25A0\u2502\u2500\u2510\u250C\u2514\u2518" +  // 0x88 - 0x8f
                "\uFE79\uFE7B\uFE7D\uFE7F\uFE77\uFE8A\uFEF0\uFEF3" +  // 0x90 - 0x97
                "\uFEF2\uFECE\uFECF\uFED0\uFEF6\uFEF8\uFEFA\uFEFC" +  // 0x98 - 0x9f
                "\u00A0\uF8FA\uF8F9\uF8F8\u00A4\uF8FB\uFE8B\uFE91" +  // 0xa0 - 0xa7
                "\uFE97\uFE9B\uFE9F\uFEA3\u060C\u00AD\uFEA7\uFEB3" +  // 0xa8 - 0xaf
                "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667" +  // 0xb0 - 0xb7
                "\u0668\u0669\uFEB7\u061B\uFEBB\uFEBF\uFECA\u061F" +  // 0xb8 - 0xbf
                "\uFECB\uFE80\uFE81\uFE83\uFE85\uFE87\uFE89\uFE8D" +  // 0xc0 - 0xc7
                "\uFE8F\uFE93\uFE95\uFE99\uFE9D\uFEA1\uFEA5\uFEA9" +  // 0xc8 - 0xcf
                "\uFEAB\uFEAD\uFEAF\uFEB1\uFEB5\uFEB9\uFEBD\uFEC3" +  // 0xd0 - 0xd7
                "\uFEC7\uFEC9\uFECD\uFECC\uFE82\uFE84\uFE8E\uFED3" +  // 0xd8 - 0xdf
                "\u0640\uFED1\uFED5\uFED9\uFEDD\uFEE1\uFEE5\uFEEB" +  // 0xe0 - 0xe7
                "\uFEED\uFEEF\uFEF1\uFE70\uFE72\uFE74\uFE76\uFE78" +  // 0xe8 - 0xef
                "\uFE7A\uFE7C\uFE7E\uFED7\uFEDB\uFEDF\uF8FC\uFEF5" +  // 0xf0 - 0xf7
                "\uFEF7\uFEF9\uFEFB\uFEE3\uFEE7\uFEEC\uFEE9\uFFFD" +  // 0xf8 - 0xff
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
        val c2b = CharArray(0x600)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            val c2bNR: CharArray? = null
            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
