package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM869 : Charset("IBM869", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM869)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u0386\uFFFD" +  // 0x80 - 0x87
                "\u00B7\u00AC\u00A6\u2018\u2019\u0388\u2015\u0389" +  // 0x88 - 0x8f
                "\u038A\u03AA\u038C\uFFFD\uFFFD\u038E\u03AB\u00A9" +  // 0x90 - 0x97
                "\u038F\u00B2\u00B3\u03AC\u00A3\u03AD\u03AE\u03AF" +  // 0x98 - 0x9f
                "\u03CA\u0390\u03CC\u03CD\u0391\u0392\u0393\u0394" +  // 0xa0 - 0xa7
                "\u0395\u0396\u0397\u00BD\u0398\u0399\u00AB\u00BB" +  // 0xa8 - 0xaf
                "\u2591\u2592\u2593\u2502\u2524\u039A\u039B\u039C" +  // 0xb0 - 0xb7
                "\u039D\u2563\u2551\u2557\u255D\u039E\u039F\u2510" +  // 0xb8 - 0xbf
                "\u2514\u2534\u252C\u251C\u2500\u253C\u03A0\u03A1" +  // 0xc0 - 0xc7
                "\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u03A3" +  // 0xc8 - 0xcf
                "\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9\u03B1\u03B2" +  // 0xd0 - 0xd7
                "\u03B3\u2518\u250C\u2588\u2584\u03B4\u03B5\u2580" +  // 0xd8 - 0xdf
                "\u03B6\u03B7\u03B8\u03B9\u03BA\u03BB\u03BC\u03BD" +  // 0xe0 - 0xe7
                "\u03BE\u03BF\u03C0\u03C1\u03C3\u03C2\u03C4\u0384" +  // 0xe8 - 0xef
                "\u00AD\u00B1\u03C5\u03C6\u03C7\u00A7\u03C8\u0385" +  // 0xf0 - 0xf7
                "\u00B0\u00A8\u03C9\u03CB\u03B0\u03CE\u25A0\u00A0" +  // 0xf8 - 0xff
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
