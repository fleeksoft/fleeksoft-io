package com.fleeksoft.charset.cs.jis

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class JIS_X_0201 : Charset("JIS_X0201", null) {
    companion object {
        val INSTANCE = JIS_X_0201()
    }

    

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is JIS_X_0201))
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, true, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, true)
    }

    private object Holder {
        private const val b2cTable = "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x80 - 0x87
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x88 - 0x8f
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x90 - 0x97
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0x98 - 0x9f
                "\uFFFD\uFF61\uFF62\uFF63\uFF64\uFF65\uFF66\uFF67" +  // 0xa0 - 0xa7
                "\uFF68\uFF69\uFF6A\uFF6B\uFF6C\uFF6D\uFF6E\uFF6F" +  // 0xa8 - 0xaf
                "\uFF70\uFF71\uFF72\uFF73\uFF74\uFF75\uFF76\uFF77" +  // 0xb0 - 0xb7
                "\uFF78\uFF79\uFF7A\uFF7B\uFF7C\uFF7D\uFF7E\uFF7F" +  // 0xb8 - 0xbf
                "\uFF80\uFF81\uFF82\uFF83\uFF84\uFF85\uFF86\uFF87" +  // 0xc0 - 0xc7
                "\uFF88\uFF89\uFF8A\uFF8B\uFF8C\uFF8D\uFF8E\uFF8F" +  // 0xc8 - 0xcf
                "\uFF90\uFF91\uFF92\uFF93\uFF94\uFF95\uFF96\uFF97" +  // 0xd0 - 0xd7
                "\uFF98\uFF99\uFF9A\uFF9B\uFF9C\uFF9D\uFF9E\uFF9F" +  // 0xd8 - 0xdf
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xe0 - 0xe7
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xe8 - 0xef
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xf0 - 0xf7
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xf8 - 0xff
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
        val c2b = CharArray(0x300)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            var c2bNR: CharArray? = null
            // non-roundtrip c2b only entries
            c2bNR = CharArray(4)
            c2bNR[0] = 0x7e.toChar()
            c2bNR[1] = 0x203e.toChar()
            c2bNR[2] = 0x5c.toChar()
            c2bNR[3] = 0xa5.toChar()

            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
