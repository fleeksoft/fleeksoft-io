package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class MacArabic : Charset("x-MacArabic", null) {
    

    override fun contains(cs: Charset): Boolean {
        return (cs is MacArabic)
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
        private const val b2cTable = "\u00C4\u00A0\u00C7\u00C9\u00D1\u00D6\u00DC\u00E1" +  // 0x80 - 0x87
                "\u00E0\u00E2\u00E4\u06BA\u00AB\u00E7\u00E9\u00E8" +  // 0x88 - 0x8f
                "\u00EA\u00EB\u00ED\u2026\u00EE\u00EF\u00F1\u00F3" +  // 0x90 - 0x97
                "\u00BB\u00F4\u00F6\u00F7\u00FA\u00F9\u00FB\u00FC" +  // 0x98 - 0x9f
                "\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u066A\uFFFD\uFFFD" +  // 0xa0 - 0xa7
                "\uFFFD\uFFFD\uFFFD\uFFFD\u060C\uFFFD\uFFFD\uFFFD" +  // 0xa8 - 0xaf
                "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667" +  // 0xb0 - 0xb7
                "\u0668\u0669\uFFFD\u061B\uFFFD\uFFFD\uFFFD\u061F" +  // 0xb8 - 0xbf
                "\u066D\u0621\u0622\u0623\u0624\u0625\u0626\u0627" +  // 0xc0 - 0xc7
                "\u0628\u0629\u062A\u062B\u062C\u062D\u062E\u062F" +  // 0xc8 - 0xcf
                "\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637" +  // 0xd0 - 0xd7
                "\u0638\u0639\u063A\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xd8 - 0xdf
                "\u0640\u0641\u0642\u0643\u0644\u0645\u0646\u0647" +  // 0xe0 - 0xe7
                "\u0648\u0649\u064A\u064B\u064C\u064D\u064E\u064F" +  // 0xe8 - 0xef
                "\u0650\u0651\u0652\u067E\u0679\u0686\u06D5\u06A4" +  // 0xf0 - 0xf7
                "\u06AF\u0688\u0691\uFFFD\uFFFD\uFFFD\u0698\u06D2" +  // 0xf8 - 0xff
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
