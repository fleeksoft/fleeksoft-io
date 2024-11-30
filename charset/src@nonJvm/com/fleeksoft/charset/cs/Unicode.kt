package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.cs.iso.ISO_8859_1
import com.fleeksoft.charset.cs.iso.ISO_8859_15
import com.fleeksoft.charset.cs.iso.ISO_8859_16
import com.fleeksoft.charset.cs.ms.MS1252
import com.fleeksoft.charset.cs.utf.UTF_16
import com.fleeksoft.charset.cs.utf.UTF_16BE
import com.fleeksoft.charset.cs.utf.UTF_16LE
import com.fleeksoft.charset.cs.utf.UTF_16LE_BOM
import com.fleeksoft.charset.cs.utf.UTF_32
import com.fleeksoft.charset.cs.utf.UTF_32BE
import com.fleeksoft.charset.cs.utf.UTF_32BE_BOM
import com.fleeksoft.charset.cs.utf.UTF_32LE
import com.fleeksoft.charset.cs.utf.UTF_32LE_BOM
import com.fleeksoft.charset.cs.utf.UTF_8

abstract class Unicode(name: String) : Charset(name, null) {
    override fun contains(cs: Charset): Boolean {
        return (cs is US_ASCII
                || cs is ISO_8859_1
                || cs is ISO_8859_15
                || cs is ISO_8859_16
                || cs is MS1252
                || cs is UTF_8
                || cs is UTF_16
                || cs is UTF_16BE
                || cs is UTF_16LE
                || cs is UTF_16LE_BOM
//                || cs is CESU_8
                || cs is UTF_32
                || cs is UTF_32BE
                || cs is UTF_32BE_BOM
                || cs is UTF_32LE
                || cs is UTF_32LE_BOM
                || cs.name() == "GBK"
                || cs.name() == "GB18030"
                || cs.name() == "ISO-8859-2"
                || cs.name() == "ISO-8859-3"
                || cs.name() == "ISO-8859-4"
                || cs.name() == "ISO-8859-5"
                || cs.name() == "ISO-8859-6"
                || cs.name() == "ISO-8859-7"
                || cs.name() == "ISO-8859-8"
                || cs.name() == "ISO-8859-9"
                || cs.name() == "ISO-8859-13"
                || cs.name() == "JIS_X0201"
                || cs.name() == "x-JIS0208"
                || cs.name() == "JIS_X0212-1990"
                || cs.name() == "GB2312"
                || cs.name() == "EUC-KR"
                || cs.name() == "x-EUC-TW"
                || cs.name() == "EUC-JP"
                || cs.name() == "x-euc-jp-linux"
                || cs.name() == "KOI8-R"
                || cs.name() == "TIS-620"
                || cs.name() == "x-ISCII91"
                || cs.name() == "windows-1251"
                || cs.name() == "windows-1253"
                || cs.name() == "windows-1254"
                || cs.name() == "windows-1255"
                || cs.name() == "windows-1256"
                || cs.name() == "windows-1257"
                || cs.name() == "windows-1258"
                || cs.name() == "windows-932"
                || cs.name() == "x-mswin-936"
                || cs.name() == "x-windows-949"
                || cs.name() == "x-windows-950"
                || cs.name() == "windows-31j"
                || cs.name() == "Big5"
                || cs.name() == "Big5-HKSCS"
                || cs.name() == "x-MS950-HKSCS"
                || cs.name() == "ISO-2022-JP"
                || cs.name() == "ISO-2022-KR"
                || cs.name() == "x-ISO-2022-CN-CNS"
                || cs.name() == "x-ISO-2022-CN-GB"
                || cs.name() == "x-Johab"
                || cs.name() == "Shift_JIS")
    }
}