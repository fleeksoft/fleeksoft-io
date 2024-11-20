package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.SingleByte.initC2B


class IBM833 : Charset("x-IBM833", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is IBM833)
    }

    override fun newDecoder(): CharsetDecoder {
        return SingleByte.Decoder(this, Holder.b2c, false, false)
    }

    override fun newEncoder(): CharsetEncoder {
        return SingleByte.Encoder(this, Holder.c2b, Holder.c2bIndex, false)
    }

    private object Holder {
        private const val b2cTable = "\u005D\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +  // 0x80 - 0x87
                "\u0068\u0069\uFFC2\uFFC3\uFFC4\uFFC5\uFFC6\uFFC7" +  // 0x88 - 0x8f
                "\uFFFD\u006A\u006B\u006C\u006D\u006E\u006F\u0070" +  // 0x90 - 0x97
                "\u0071\u0072\uFFCA\uFFCB\uFFCC\uFFCD\uFFCE\uFFCF" +  // 0x98 - 0x9f
                "\u203E\u007E\u0073\u0074\u0075\u0076\u0077\u0078" +  // 0xa0 - 0xa7
                "\u0079\u007A\uFFD2\uFFD3\uFFD4\uFFD5\uFFD6\uFFD7" +  // 0xa8 - 0xaf
                "\u005E\uFFFD\\\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xb0 - 0xb7
                "\uFFFD\uFFFD\uFFDA\uFFDB\uFFDC\uFFFD\uFFFD\uFFFD" +  // 0xb8 - 0xbf
                "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +  // 0xc0 - 0xc7
                "\u0048\u0049\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xc8 - 0xcf
                "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050" +  // 0xd0 - 0xd7
                "\u0051\u0052\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD" +  // 0xd8 - 0xdf
                "\u20A9\uFFFD\u0053\u0054\u0055\u0056\u0057\u0058" +  // 0xe0 - 0xe7
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
                "\u0020\uFFFD\uFFA0\uFFA1\uFFA2\uFFA3\uFFA4\uFFA5" +  // 0x40 - 0x47
                "\uFFA6\uFFA7\u00A2\u002E\u003C\u0028\u002B\u007C" +  // 0x48 - 0x4f
                "\u0026\uFFFD\uFFA8\uFFA9\uFFAA\uFFAB\uFFAC\uFFAD" +  // 0x50 - 0x57
                "\uFFAE\uFFAF\u0021\u0024\u002A\u0029\u003B\u00AC" +  // 0x58 - 0x5f
                "\u002D\u002F\uFFB0\uFFB1\uFFB2\uFFB3\uFFB4\uFFB5" +  // 0x60 - 0x67
                "\uFFB6\uFFB7\u00A6\u002C\u0025\u005F\u003E\u003F" +  // 0x68 - 0x6f
                "\u005B\uFFFD\uFFB8\uFFB9\uFFBA\uFFBB\uFFBC\uFFBD" +  // 0x70 - 0x77
                "\uFFBE\u0060\u003A\u0023\u0040\'\u003D\"" // 0x78 - 0x7f


        val b2c = b2cTable.toCharArray()
        val c2b = CharArray(0x300)
        val c2bIndex = CharArray(0x100)

        init {
            val b2cMap = b2c
            var c2bNR: CharArray? = null
            // non-roundtrip c2b only entries
            c2bNR = CharArray(188)
            c2bNR[0] = 0x5a.toChar()
            c2bNR[1] = 0xff01.toChar()
            c2bNR[2] = 0x7f.toChar()
            c2bNR[3] = 0xff02.toChar()
            c2bNR[4] = 0x7b.toChar()
            c2bNR[5] = 0xff03.toChar()
            c2bNR[6] = 0x5b.toChar()
            c2bNR[7] = 0xff04.toChar()
            c2bNR[8] = 0x6c.toChar()
            c2bNR[9] = 0xff05.toChar()
            c2bNR[10] = 0x50.toChar()
            c2bNR[11] = 0xff06.toChar()
            c2bNR[12] = 0x7d.toChar()
            c2bNR[13] = 0xff07.toChar()
            c2bNR[14] = 0x4d.toChar()
            c2bNR[15] = 0xff08.toChar()
            c2bNR[16] = 0x5d.toChar()
            c2bNR[17] = 0xff09.toChar()
            c2bNR[18] = 0x5c.toChar()
            c2bNR[19] = 0xff0a.toChar()
            c2bNR[20] = 0x4e.toChar()
            c2bNR[21] = 0xff0b.toChar()
            c2bNR[22] = 0x6b.toChar()
            c2bNR[23] = 0xff0c.toChar()
            c2bNR[24] = 0x60.toChar()
            c2bNR[25] = 0xff0d.toChar()
            c2bNR[26] = 0x4b.toChar()
            c2bNR[27] = 0xff0e.toChar()
            c2bNR[28] = 0x61.toChar()
            c2bNR[29] = 0xff0f.toChar()
            c2bNR[30] = 0xf0.toChar()
            c2bNR[31] = 0xff10.toChar()
            c2bNR[32] = 0xf1.toChar()
            c2bNR[33] = 0xff11.toChar()
            c2bNR[34] = 0xf2.toChar()
            c2bNR[35] = 0xff12.toChar()
            c2bNR[36] = 0xf3.toChar()
            c2bNR[37] = 0xff13.toChar()
            c2bNR[38] = 0xf4.toChar()
            c2bNR[39] = 0xff14.toChar()
            c2bNR[40] = 0xf5.toChar()
            c2bNR[41] = 0xff15.toChar()
            c2bNR[42] = 0xf6.toChar()
            c2bNR[43] = 0xff16.toChar()
            c2bNR[44] = 0xf7.toChar()
            c2bNR[45] = 0xff17.toChar()
            c2bNR[46] = 0xf8.toChar()
            c2bNR[47] = 0xff18.toChar()
            c2bNR[48] = 0xf9.toChar()
            c2bNR[49] = 0xff19.toChar()
            c2bNR[50] = 0x7a.toChar()
            c2bNR[51] = 0xff1a.toChar()
            c2bNR[52] = 0x5e.toChar()
            c2bNR[53] = 0xff1b.toChar()
            c2bNR[54] = 0x4c.toChar()
            c2bNR[55] = 0xff1c.toChar()
            c2bNR[56] = 0x7e.toChar()
            c2bNR[57] = 0xff1d.toChar()
            c2bNR[58] = 0x6e.toChar()
            c2bNR[59] = 0xff1e.toChar()
            c2bNR[60] = 0x6f.toChar()
            c2bNR[61] = 0xff1f.toChar()
            c2bNR[62] = 0x7c.toChar()
            c2bNR[63] = 0xff20.toChar()
            c2bNR[64] = 0xc1.toChar()
            c2bNR[65] = 0xff21.toChar()
            c2bNR[66] = 0xc2.toChar()
            c2bNR[67] = 0xff22.toChar()
            c2bNR[68] = 0xc3.toChar()
            c2bNR[69] = 0xff23.toChar()
            c2bNR[70] = 0xc4.toChar()
            c2bNR[71] = 0xff24.toChar()
            c2bNR[72] = 0xc5.toChar()
            c2bNR[73] = 0xff25.toChar()
            c2bNR[74] = 0xc6.toChar()
            c2bNR[75] = 0xff26.toChar()
            c2bNR[76] = 0xc7.toChar()
            c2bNR[77] = 0xff27.toChar()
            c2bNR[78] = 0xc8.toChar()
            c2bNR[79] = 0xff28.toChar()
            c2bNR[80] = 0xc9.toChar()
            c2bNR[81] = 0xff29.toChar()
            c2bNR[82] = 0xd1.toChar()
            c2bNR[83] = 0xff2a.toChar()
            c2bNR[84] = 0xd2.toChar()
            c2bNR[85] = 0xff2b.toChar()
            c2bNR[86] = 0xd3.toChar()
            c2bNR[87] = 0xff2c.toChar()
            c2bNR[88] = 0xd4.toChar()
            c2bNR[89] = 0xff2d.toChar()
            c2bNR[90] = 0xd5.toChar()
            c2bNR[91] = 0xff2e.toChar()
            c2bNR[92] = 0xd6.toChar()
            c2bNR[93] = 0xff2f.toChar()
            c2bNR[94] = 0xd7.toChar()
            c2bNR[95] = 0xff30.toChar()
            c2bNR[96] = 0xd8.toChar()
            c2bNR[97] = 0xff31.toChar()
            c2bNR[98] = 0xd9.toChar()
            c2bNR[99] = 0xff32.toChar()
            c2bNR[100] = 0xe2.toChar()
            c2bNR[101] = 0xff33.toChar()
            c2bNR[102] = 0xe3.toChar()
            c2bNR[103] = 0xff34.toChar()
            c2bNR[104] = 0xe4.toChar()
            c2bNR[105] = 0xff35.toChar()
            c2bNR[106] = 0xe5.toChar()
            c2bNR[107] = 0xff36.toChar()
            c2bNR[108] = 0xe6.toChar()
            c2bNR[109] = 0xff37.toChar()
            c2bNR[110] = 0xe7.toChar()
            c2bNR[111] = 0xff38.toChar()
            c2bNR[112] = 0xe8.toChar()
            c2bNR[113] = 0xff39.toChar()
            c2bNR[114] = 0xe9.toChar()
            c2bNR[115] = 0xff3a.toChar()
            c2bNR[116] = 0x70.toChar()
            c2bNR[117] = 0xff3b.toChar()
            c2bNR[118] = 0xb2.toChar()
            c2bNR[119] = 0xff3c.toChar()
            c2bNR[120] = 0x80.toChar()
            c2bNR[121] = 0xff3d.toChar()
            c2bNR[122] = 0xb0.toChar()
            c2bNR[123] = 0xff3e.toChar()
            c2bNR[124] = 0x6d.toChar()
            c2bNR[125] = 0xff3f.toChar()
            c2bNR[126] = 0x79.toChar()
            c2bNR[127] = 0xff40.toChar()
            c2bNR[128] = 0x81.toChar()
            c2bNR[129] = 0xff41.toChar()
            c2bNR[130] = 0x82.toChar()
            c2bNR[131] = 0xff42.toChar()
            c2bNR[132] = 0x83.toChar()
            c2bNR[133] = 0xff43.toChar()
            c2bNR[134] = 0x84.toChar()
            c2bNR[135] = 0xff44.toChar()
            c2bNR[136] = 0x85.toChar()
            c2bNR[137] = 0xff45.toChar()
            c2bNR[138] = 0x86.toChar()
            c2bNR[139] = 0xff46.toChar()
            c2bNR[140] = 0x87.toChar()
            c2bNR[141] = 0xff47.toChar()
            c2bNR[142] = 0x88.toChar()
            c2bNR[143] = 0xff48.toChar()
            c2bNR[144] = 0x89.toChar()
            c2bNR[145] = 0xff49.toChar()
            c2bNR[146] = 0x91.toChar()
            c2bNR[147] = 0xff4a.toChar()
            c2bNR[148] = 0x92.toChar()
            c2bNR[149] = 0xff4b.toChar()
            c2bNR[150] = 0x93.toChar()
            c2bNR[151] = 0xff4c.toChar()
            c2bNR[152] = 0x94.toChar()
            c2bNR[153] = 0xff4d.toChar()
            c2bNR[154] = 0x95.toChar()
            c2bNR[155] = 0xff4e.toChar()
            c2bNR[156] = 0x96.toChar()
            c2bNR[157] = 0xff4f.toChar()
            c2bNR[158] = 0x97.toChar()
            c2bNR[159] = 0xff50.toChar()
            c2bNR[160] = 0x98.toChar()
            c2bNR[161] = 0xff51.toChar()
            c2bNR[162] = 0x99.toChar()
            c2bNR[163] = 0xff52.toChar()
            c2bNR[164] = 0xa2.toChar()
            c2bNR[165] = 0xff53.toChar()
            c2bNR[166] = 0xa3.toChar()
            c2bNR[167] = 0xff54.toChar()
            c2bNR[168] = 0xa4.toChar()
            c2bNR[169] = 0xff55.toChar()
            c2bNR[170] = 0xa5.toChar()
            c2bNR[171] = 0xff56.toChar()
            c2bNR[172] = 0xa6.toChar()
            c2bNR[173] = 0xff57.toChar()
            c2bNR[174] = 0xa7.toChar()
            c2bNR[175] = 0xff58.toChar()
            c2bNR[176] = 0xa8.toChar()
            c2bNR[177] = 0xff59.toChar()
            c2bNR[178] = 0xa9.toChar()
            c2bNR[179] = 0xff5a.toChar()
            c2bNR[180] = 0xc0.toChar()
            c2bNR[181] = 0xff5b.toChar()
            c2bNR[182] = 0x4f.toChar()
            c2bNR[183] = 0xff5c.toChar()
            c2bNR[184] = 0xd0.toChar()
            c2bNR[185] = 0xff5d.toChar()
            c2bNR[186] = 0xa1.toChar()
            c2bNR[187] = 0xff5e.toChar()

            initC2B(b2cMap, c2bNR, c2b, c2bIndex)
        }
    }
}
