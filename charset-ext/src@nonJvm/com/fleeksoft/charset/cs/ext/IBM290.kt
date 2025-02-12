package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM290 : Charset("IBM290", null) {
    

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM290)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u005D\uFF71\uFF72\uFF73\uFF74\uFF75\uFF76\uFF77" +  // 0x80 - 0x87
                "\uFF78\uFF79\uFF7A\u0071\uFF7B\uFF7C\uFF7D\uFF7E" +  // 0x88 - 0x8f
                "\uFF7F\uFF80\uFF81\uFF82\uFF83\uFF84\uFF85\uFF86" +  // 0x90 - 0x97
                "\uFF87\uFF88\uFF89\u0072\uFFFD\uFF8A\uFF8B\uFF8C" +  // 0x98 - 0x9f
                "\u007E\u203E\uFF8D\uFF8E\uFF8F\uFF90\uFF91\uFF92" +  // 0xa0 - 0xa7
                "\uFF93\uFF94\uFF95\u0073\uFF96\uFF97\uFF98\uFF99" +  // 0xa8 - 0xaf
                "\u005E\u00A2\\\u0074\u0075\u0076\u0077\u0078" +  // 0xb0 - 0xb7
                "\u0079\u007A\uFF9A\uFF9B\uFF9C\uFF9D\uFF9E\uFF9F" +  // 0xb8 - 0xbf
                "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0xc0 - 0xc7
                "\u0048\u0049\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xc8 - 0xcf
                "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050" +  // 0xd0 - 0xd7
                "\u0051\u0052\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xd8 - 0xdf
                "\u0024\uFFFD\u0053\u0054\u0055\u0056\u0057\u0058" +  // 0xe0 - 0xe7
                "\u0059\u005A\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xe8 - 0xef
                "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +  // 0xf0 - 0xf7
                "\u0038\u0039\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u009F" +  // 0xf8 - 0xff
                "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F" +  // 0x00 - 0x07
                "\u0097\u008D\u008E\u000B\u000c\r\u000E\u000F" +  // 0x08 - 0x0f
                "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087" +  // 0x10 - 0x17
                "\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +  // 0x18 - 0x1f
                "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B" +  // 0x20 - 0x27
                "\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +  // 0x28 - 0x2f
                "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004" +  // 0x30 - 0x37
                "\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +  // 0x38 - 0x3f
                "\u0020\uFF61\uFF62\uFF63\uFF64\uFF65\uFF66\uFF67" +  // 0x40 - 0x47
                "\uFF68\uFF69\u00A3\u002E\u003C\u0028\u002B\u007C" +  // 0x48 - 0x4f
                "\u0026\uFF6A\uFF6B\uFF6C\uFF6D\uFF6E\uFF6F\uFFFD" +  // 0x50 - 0x57
                "\uFF70\uFFFD\u0021\u00A5\u002A\u0029\u003B\u00AC" +  // 0x58 - 0x5f
                "\u002D\u002F\u0061\u0062\u0063\u0064\u0065\u0066" +  // 0x60 - 0x67
                "\u0067\u0068\uFFFD\u002C\u0025\u005F\u003E\u003F" +  // 0x68 - 0x6f
                "\u005B\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +  // 0x70 - 0x77
                "\u0070\u0060\u003A\u0023\u0040\'\u003D\"" // 0x78 - 0x7f


        val b2c = b2cTable.toCharArray()
        val c2b = CharArray(0x300)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            var c2bNR: CharArray? = null
            // non-roundtrip c2b only entries
            c2bNR = CharArray(188)
            c2bNR[0] = 0x4b.toChar()
            c2bNR[1] = 0xff0e.toChar()
            c2bNR[2] = 0x4c.toChar()
            c2bNR[3] = 0xff1c.toChar()
            c2bNR[4] = 0x4d.toChar()
            c2bNR[5] = 0xff08.toChar()
            c2bNR[6] = 0x4e.toChar()
            c2bNR[7] = 0xff0b.toChar()
            c2bNR[8] = 0x4f.toChar()
            c2bNR[9] = 0xff5c.toChar()
            c2bNR[10] = 0x50.toChar()
            c2bNR[11] = 0xff06.toChar()
            c2bNR[12] = 0x5a.toChar()
            c2bNR[13] = 0xff01.toChar()
            c2bNR[14] = 0x5c.toChar()
            c2bNR[15] = 0xff0a.toChar()
            c2bNR[16] = 0x5d.toChar()
            c2bNR[17] = 0xff09.toChar()
            c2bNR[18] = 0x5e.toChar()
            c2bNR[19] = 0xff1b.toChar()
            c2bNR[20] = 0x60.toChar()
            c2bNR[21] = 0xff0d.toChar()
            c2bNR[22] = 0x61.toChar()
            c2bNR[23] = 0xff0f.toChar()
            c2bNR[24] = 0x62.toChar()
            c2bNR[25] = 0xff41.toChar()
            c2bNR[26] = 0x63.toChar()
            c2bNR[27] = 0xff42.toChar()
            c2bNR[28] = 0x64.toChar()
            c2bNR[29] = 0xff43.toChar()
            c2bNR[30] = 0x65.toChar()
            c2bNR[31] = 0xff44.toChar()
            c2bNR[32] = 0x66.toChar()
            c2bNR[33] = 0xff45.toChar()
            c2bNR[34] = 0x67.toChar()
            c2bNR[35] = 0xff46.toChar()
            c2bNR[36] = 0x68.toChar()
            c2bNR[37] = 0xff47.toChar()
            c2bNR[38] = 0x69.toChar()
            c2bNR[39] = 0xff48.toChar()
            c2bNR[40] = 0x6b.toChar()
            c2bNR[41] = 0xff0c.toChar()
            c2bNR[42] = 0x6c.toChar()
            c2bNR[43] = 0xff05.toChar()
            c2bNR[44] = 0x6d.toChar()
            c2bNR[45] = 0xff3f.toChar()
            c2bNR[46] = 0x6e.toChar()
            c2bNR[47] = 0xff1e.toChar()
            c2bNR[48] = 0x6f.toChar()
            c2bNR[49] = 0xff1f.toChar()
            c2bNR[50] = 0x70.toChar()
            c2bNR[51] = 0xff3b.toChar()
            c2bNR[52] = 0x71.toChar()
            c2bNR[53] = 0xff49.toChar()
            c2bNR[54] = 0x72.toChar()
            c2bNR[55] = 0xff4a.toChar()
            c2bNR[56] = 0x73.toChar()
            c2bNR[57] = 0xff4b.toChar()
            c2bNR[58] = 0x74.toChar()
            c2bNR[59] = 0xff4c.toChar()
            c2bNR[60] = 0x75.toChar()
            c2bNR[61] = 0xff4d.toChar()
            c2bNR[62] = 0x76.toChar()
            c2bNR[63] = 0xff4e.toChar()
            c2bNR[64] = 0x77.toChar()
            c2bNR[65] = 0xff4f.toChar()
            c2bNR[66] = 0x78.toChar()
            c2bNR[67] = 0xff50.toChar()
            c2bNR[68] = 0x79.toChar()
            c2bNR[69] = 0xff40.toChar()
            c2bNR[70] = 0x7a.toChar()
            c2bNR[71] = 0xff1a.toChar()
            c2bNR[72] = 0x7b.toChar()
            c2bNR[73] = 0xff03.toChar()
            c2bNR[74] = 0x7c.toChar()
            c2bNR[75] = 0xff20.toChar()
            c2bNR[76] = 0x7d.toChar()
            c2bNR[77] = 0xff07.toChar()
            c2bNR[78] = 0x7e.toChar()
            c2bNR[79] = 0xff1d.toChar()
            c2bNR[80] = 0x7f.toChar()
            c2bNR[81] = 0xff02.toChar()
            c2bNR[82] = 0x80.toChar()
            c2bNR[83] = 0xff3d.toChar()
            c2bNR[84] = 0x8b.toChar()
            c2bNR[85] = 0xff51.toChar()
            c2bNR[86] = 0x9b.toChar()
            c2bNR[87] = 0xff52.toChar()
            c2bNR[88] = 0xa0.toChar()
            c2bNR[89] = 0xff5e.toChar()
            c2bNR[90] = 0xab.toChar()
            c2bNR[91] = 0xff53.toChar()
            c2bNR[92] = 0xb0.toChar()
            c2bNR[93] = 0xff3e.toChar()
            c2bNR[94] = 0xb2.toChar()
            c2bNR[95] = 0xff3c.toChar()
            c2bNR[96] = 0xb3.toChar()
            c2bNR[97] = 0xff54.toChar()
            c2bNR[98] = 0xb4.toChar()
            c2bNR[99] = 0xff55.toChar()
            c2bNR[100] = 0xb5.toChar()
            c2bNR[101] = 0xff56.toChar()
            c2bNR[102] = 0xb6.toChar()
            c2bNR[103] = 0xff57.toChar()
            c2bNR[104] = 0xb7.toChar()
            c2bNR[105] = 0xff58.toChar()
            c2bNR[106] = 0xb8.toChar()
            c2bNR[107] = 0xff59.toChar()
            c2bNR[108] = 0xb9.toChar()
            c2bNR[109] = 0xff5a.toChar()
            c2bNR[110] = 0xc0.toChar()
            c2bNR[111] = 0xff5b.toChar()
            c2bNR[112] = 0xc1.toChar()
            c2bNR[113] = 0xff21.toChar()
            c2bNR[114] = 0xc2.toChar()
            c2bNR[115] = 0xff22.toChar()
            c2bNR[116] = 0xc3.toChar()
            c2bNR[117] = 0xff23.toChar()
            c2bNR[118] = 0xc4.toChar()
            c2bNR[119] = 0xff24.toChar()
            c2bNR[120] = 0xc5.toChar()
            c2bNR[121] = 0xff25.toChar()
            c2bNR[122] = 0xc6.toChar()
            c2bNR[123] = 0xff26.toChar()
            c2bNR[124] = 0xc7.toChar()
            c2bNR[125] = 0xff27.toChar()
            c2bNR[126] = 0xc8.toChar()
            c2bNR[127] = 0xff28.toChar()
            c2bNR[128] = 0xc9.toChar()
            c2bNR[129] = 0xff29.toChar()
            c2bNR[130] = 0xd0.toChar()
            c2bNR[131] = 0xff5d.toChar()
            c2bNR[132] = 0xd1.toChar()
            c2bNR[133] = 0xff2a.toChar()
            c2bNR[134] = 0xd2.toChar()
            c2bNR[135] = 0xff2b.toChar()
            c2bNR[136] = 0xd3.toChar()
            c2bNR[137] = 0xff2c.toChar()
            c2bNR[138] = 0xd4.toChar()
            c2bNR[139] = 0xff2d.toChar()
            c2bNR[140] = 0xd5.toChar()
            c2bNR[141] = 0xff2e.toChar()
            c2bNR[142] = 0xd6.toChar()
            c2bNR[143] = 0xff2f.toChar()
            c2bNR[144] = 0xd7.toChar()
            c2bNR[145] = 0xff30.toChar()
            c2bNR[146] = 0xd8.toChar()
            c2bNR[147] = 0xff31.toChar()
            c2bNR[148] = 0xd9.toChar()
            c2bNR[149] = 0xff32.toChar()
            c2bNR[150] = 0xe0.toChar()
            c2bNR[151] = 0xff04.toChar()
            c2bNR[152] = 0xe2.toChar()
            c2bNR[153] = 0xff33.toChar()
            c2bNR[154] = 0xe3.toChar()
            c2bNR[155] = 0xff34.toChar()
            c2bNR[156] = 0xe4.toChar()
            c2bNR[157] = 0xff35.toChar()
            c2bNR[158] = 0xe5.toChar()
            c2bNR[159] = 0xff36.toChar()
            c2bNR[160] = 0xe6.toChar()
            c2bNR[161] = 0xff37.toChar()
            c2bNR[162] = 0xe7.toChar()
            c2bNR[163] = 0xff38.toChar()
            c2bNR[164] = 0xe8.toChar()
            c2bNR[165] = 0xff39.toChar()
            c2bNR[166] = 0xe9.toChar()
            c2bNR[167] = 0xff3a.toChar()
            c2bNR[168] = 0xf0.toChar()
            c2bNR[169] = 0xff10.toChar()
            c2bNR[170] = 0xf1.toChar()
            c2bNR[171] = 0xff11.toChar()
            c2bNR[172] = 0xf2.toChar()
            c2bNR[173] = 0xff12.toChar()
            c2bNR[174] = 0xf3.toChar()
            c2bNR[175] = 0xff13.toChar()
            c2bNR[176] = 0xf4.toChar()
            c2bNR[177] = 0xff14.toChar()
            c2bNR[178] = 0xf5.toChar()
            c2bNR[179] = 0xff15.toChar()
            c2bNR[180] = 0xf6.toChar()
            c2bNR[181] = 0xff16.toChar()
            c2bNR[182] = 0xf7.toChar()
            c2bNR[183] = 0xff17.toChar()
            c2bNR[184] = 0xf8.toChar()
            c2bNR[185] = 0xff18.toChar()
            c2bNR[186] = 0xf9.toChar()
            c2bNR[187] = 0xff19.toChar()

            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
