package com.fleeksoft.charset.spi

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.cs.ext.*

class ExtendedCharsetProvider : CharsetProvider() {
    override fun charsetForName(charsetName: String): Charset? {
        return getExtCharsetByName(charsetName)
    }

    private fun getExtCharsetByName(name: String): Charset? {
        return charsetMap[name]?.invoke()
    }

    private val charsetMap = mapOf<String, () -> Charset>(
        "Big5" to { Big5() },
        "x-windows-874" to { MS874() },
        "Big5-HKSCS" to { Big5_HKSCS() },
        "x-Big5-HKSCS-2001" to { Big5_HKSCS_2001() },
        "x-Big5-Solaris" to { Big5_Solaris() },
        "GBK" to { GBK() },
        "Shift_JIS" to { SJIS() },
        "windows-31j" to { MS932() },
//        "x-SJIS_0213" to { SJIS_0213() },
//        "MS932_0213" to { MS932_0213() },
        "x-PCK" to { PCK() },
        "ISO-2022-JP" to { ISO2022_JP() },
        "ISO-2022-JP-2" to { ISO2022_JP_2() },
        "x-windows-50221" to { MS50221() },
        "x-windows-50220" to { MS50220() },
        "x-windows-iso2022jp" to { MSISO2022JP() },
        "x-JISAutoDetect" to { JISAutoDetect() },
        "ISO-2022-KR" to { ISO2022_KR() },
        "x-ISCII91" to { ISCII91() },
        "ISO-8859-3" to { ISO_8859_3() },
        "ISO-8859-6" to { ISO_8859_6() },
        "ISO-8859-8" to { ISO_8859_8() },
        "x-iso-8859-11" to { ISO_8859_11() },
        "TIS-620" to { TIS_620() },
        "windows-1255" to { MS1255() },
        "windows-1256" to { MS1256() },
        "windows-1258" to { MS1258() },
        "x-IBM942" to { IBM942() },
        "x-IBM942C" to { IBM942C() },
        "x-IBM943" to { IBM943() },
        "x-IBM943C" to { IBM943C() },
        "x-IBM930" to { IBM930() },
        "x-IBM935" to { IBM935() },
        "x-IBM856" to { IBM856() },
        "IBM860" to { IBM860() },
        "IBM861" to { IBM861() },
        "IBM863" to { IBM863() },
        "IBM864" to { IBM864() },
        "IBM865" to { IBM865() },
        "IBM868" to { IBM868() },
        "IBM869" to { IBM869() },
        "x-IBM921" to { IBM921() },
        "x-IBM1006" to { IBM1006() },
        "x-IBM1046" to { IBM1046() },
        "IBM1047" to { IBM1047() },
        "x-IBM1098" to { IBM1098() },
        "IBM037" to { IBM037() },
        "x-IBM1025" to { IBM1025() },
        "IBM1026" to { IBM1026() },
        "x-IBM1112" to { IBM1112() },
        "x-IBM1122" to { IBM1122() },
        "x-IBM1123" to { IBM1123() },
        "x-IBM1124" to { IBM1124() },
        "x-IBM1129" to { IBM1129() },
        "IBM273" to { IBM273() },
        "IBM277" to { IBM277() },
        "IBM278" to { IBM278() },
        "IBM280" to { IBM280() },
        "IBM284" to { IBM284() },
        "IBM285" to { IBM285() },
        "IBM297" to { IBM297() },
        "IBM420" to { IBM420() },
        "IBM424" to { IBM424() },
        "IBM500" to { IBM500() },
        "x-IBM833" to { IBM833() },
        "IBM-Thai" to { IBM838() },
        "IBM870" to { IBM870() },
        "IBM871" to { IBM871() },
        "x-IBM875" to { IBM875() },
        "IBM918" to { IBM918() },
        "x-IBM922" to { IBM922() },
        "x-IBM1097" to { IBM1097() },
        "x-IBM949" to { IBM949() },
        "x-IBM949C" to { IBM949C() },
        "x-IBM939" to { IBM939() },
        "x-IBM1381" to { IBM1381() },
        "x-IBM1383" to { IBM1383() },
        "x-IBM970" to { IBM970() },
//        "x-IBM964" to { IBM964() },
        "x-IBM29626C" to { IBM29626C() },
//        "x-IBM33722" to { IBM33722() },
        "IBM01140" to { IBM1140() },
        "IBM01141" to { IBM1141() },
        "IBM01142" to { IBM1142() },
        "IBM01143" to { IBM1143() },
        "IBM01144" to { IBM1144() },
        "IBM01145" to { IBM1145() },
        "IBM01146" to { IBM1146() },
        "IBM01147" to { IBM1147() },
        "IBM01148" to { IBM1148() },
        "IBM01149" to { IBM1149() },
        "IBM290" to { IBM290() },
        "x-IBM1166" to { IBM1166() },
        "x-IBM300" to { IBM300() },
        "x-MacRoman" to { MacRoman() },
        "x-MacCentralEurope" to { MacCentralEurope() },
        "x-MacCroatian" to { MacCroatian() },
        "x-MacGreek" to { MacGreek() },
        "x-MacCyrillic" to { MacCyrillic() },
        "x-MacUkraine" to { MacUkraine() },
        "x-MacTurkish" to { MacTurkish() },
        "x-MacArabic" to { MacArabic() },
        "x-MacHebrew" to { MacHebrew() },
        "x-MacIceland" to { MacIceland() },
        "x-MacRomania" to { MacRomania() },
        "x-MacThai" to { MacThai() },
        "x-MacSymbol" to { MacSymbol() },
        "x-MacDingbat" to { MacDingbat() },
    )
}