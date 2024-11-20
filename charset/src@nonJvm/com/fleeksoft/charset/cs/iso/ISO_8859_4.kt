package com.fleeksoft.charset.cs.iso

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte

class ISO_8859_4 private constructor() : Charset("ISO-8859-4", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII") || (cs is ISO_8859_4))
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, true, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(
            this,
            Holder.c2b,
            Holder.c2bIndex,
            true
        )
    }

    private object Holder {
        private const val b2cTable = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087" +  // 0x80 - 0x87
                "\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F" +  // 0x88 - 0x8f
                "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097" +  // 0x90 - 0x97
                "\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F" +  // 0x98 - 0x9f
                "\u00A0\u0104\u0138\u0156\u00A4\u0128\u013B\u00A7" +  // 0xa0 - 0xa7
                "\u00A8\u0160\u0112\u0122\u0166\u00AD\u017D\u00AF" +  // 0xa8 - 0xaf
                "\u00B0\u0105\u02DB\u0157\u00B4\u0129\u013C\u02C7" +  // 0xb0 - 0xb7
                "\u00B8\u0161\u0113\u0123\u0167\u014A\u017E\u014B" +  // 0xb8 - 0xbf
                "\u0100\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u012E" +  // 0xc0 - 0xc7
                "\u010C\u00C9\u0118\u00CB\u0116\u00CD\u00CE\u012A" +  // 0xc8 - 0xcf
                "\u0110\u0145\u014C\u0136\u00D4\u00D5\u00D6\u00D7" +  // 0xd0 - 0xd7
                "\u00D8\u0172\u00DA\u00DB\u00DC\u0168\u016A\u00DF" +  // 0xd8 - 0xdf
                "\u0101\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u012F" +  // 0xe0 - 0xe7
                "\u010D\u00E9\u0119\u00EB\u0117\u00ED\u00EE\u012B" +  // 0xe8 - 0xef
                "\u0111\u0146\u014D\u0137\u00F4\u00F5\u00F6\u00F7" +  // 0xf0 - 0xf7
                "\u00F8\u0173\u00FA\u00FB\u00FC\u0169\u016B\u02D9" +  // 0xf8 - 0xff
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
            val c2bNR: CharArray? = null
            SingleByte.initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }


    companion object {
        val INSTANCE: ISO_8859_4 = ISO_8859_4()
    }
}
