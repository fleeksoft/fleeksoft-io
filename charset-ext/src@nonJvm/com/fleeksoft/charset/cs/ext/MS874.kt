package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class MS874 : Charset("x-windows-874", null) {
    

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is MS874))
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, true, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, true)
    }

    private object Holder {
        private const val b2cTable = "\u20AC\uFFFD\uFFFD\uFFFD\uFFFD\u2026\uFFFD\uFFFD" +  // 0x80 - 0x87
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x88 - 0x8f
                "\uFFFD\u2018\u2019\u201C\u201D\u2022\u2013\u2014" +  // 0x90 - 0x97
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x98 - 0x9f
                "\u00A0\u0E01\u0E02\u0E03\u0E04\u0E05\u0E06\u0E07" +  // 0xa0 - 0xa7
                "\u0E08\u0E09\u0E0A\u0E0B\u0E0C\u0E0D\u0E0E\u0E0F" +  // 0xa8 - 0xaf
                "\u0E10\u0E11\u0E12\u0E13\u0E14\u0E15\u0E16\u0E17" +  // 0xb0 - 0xb7
                "\u0E18\u0E19\u0E1A\u0E1B\u0E1C\u0E1D\u0E1E\u0E1F" +  // 0xb8 - 0xbf
                "\u0E20\u0E21\u0E22\u0E23\u0E24\u0E25\u0E26\u0E27" +  // 0xc0 - 0xc7
                "\u0E28\u0E29\u0E2A\u0E2B\u0E2C\u0E2D\u0E2E\u0E2F" +  // 0xc8 - 0xcf
                "\u0E30\u0E31\u0E32\u0E33\u0E34\u0E35\u0E36\u0E37" +  // 0xd0 - 0xd7
                "\u0E38\u0E39\u0E3A\uFFFD\uFFFD\uFFFD\uFFFD\u0E3F" +  // 0xd8 - 0xdf
                "\u0E40\u0E41\u0E42\u0E43\u0E44\u0E45\u0E46\u0E47" +  // 0xe0 - 0xe7
                "\u0E48\u0E49\u0E4A\u0E4B\u0E4C\u0E4D\u0E4E\u0E4F" +  // 0xe8 - 0xef
                "\u0E50\u0E51\u0E52\u0E53\u0E54\u0E55\u0E56\u0E57" +  // 0xf0 - 0xf7
                "\u0E58\u0E59\u0E5A\u0E5B\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xf8 - 0xff
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
        val c2b = CharArray(0x400)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            val c2bNR: CharArray? = null
            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
