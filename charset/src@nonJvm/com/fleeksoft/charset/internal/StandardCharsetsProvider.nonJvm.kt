package com.fleeksoft.charset.internal

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.cs.US_ASCII
import com.fleeksoft.charset.cs.euc.EUC_CN
import com.fleeksoft.charset.cs.euc.EUC_JP
import com.fleeksoft.charset.cs.euc.EUC_JP_LINUX
import com.fleeksoft.charset.cs.euc.EUC_JP_Open
import com.fleeksoft.charset.cs.euc.EUC_KR
import com.fleeksoft.charset.spi.CharsetProvider
import com.fleeksoft.charset.cs.ibm.*
import com.fleeksoft.charset.cs.iso.*
import com.fleeksoft.charset.cs.jis.JIS_X_0201
import com.fleeksoft.charset.cs.jis.JIS_X_0208
import com.fleeksoft.charset.cs.jis.JIS_X_0212
import com.fleeksoft.charset.cs.ms.*
import com.fleeksoft.charset.cs.other.CESU_8
import com.fleeksoft.charset.cs.other.GB18030
import com.fleeksoft.charset.cs.other.KOI8_R
import com.fleeksoft.charset.cs.other.KOI8_U
import com.fleeksoft.charset.cs.utf.*

internal class StandardCharsetsProvider : CharsetProvider() {
    // FIXME: synchronized call for this function
    override fun charsetForName(charsetName: String): Charset? {
        return when (charsetName) {
            "UTF-8" -> {
                UTF_8.INSTANCE
            }

            "UTF-16" -> {
                UTF_16.INSTANCE
            }

            "US-ASCII" -> {
                US_ASCII.INSTANCE
            }

            "ISO-8859-1" -> {
                ISO_8859_1.INSTANCE
            }

            // Check cache first
            else -> getCharsetByName(charsetName)
        }
    }

    private val charsetMap: Map<String, () -> Charset> by lazy {
        mapOf(
            "UTF-8" to { UTF_8.INSTANCE },
            "UTF-16" to { UTF_16.INSTANCE },
            "UTF-16LE" to { UTF_16LE.INSTANCE },
            "UTF-16BE" to { UTF_16BE.INSTANCE },
            "x-UTF-16LE-BOM" to { UTF_16LE_BOM.INSTANCE },
            "UTF-32" to { UTF_32.INSTANCE },
            "UTF-32LE" to { UTF_32LE.INSTANCE },
            "X-UTF-32LE-BOM" to { UTF_32LE_BOM.INSTANCE },
            "UTF-32BE" to { UTF_32BE.INSTANCE },
            "X-UTF-32BE-BOM" to { UTF_32BE_BOM.INSTANCE },
            "ISO-8859-1" to { ISO_8859_1.INSTANCE },
            "ISO-8859-2" to { ISO_8859_2.INSTANCE },
            "ISO-8859-4" to { ISO_8859_4.INSTANCE },
            "ISO-8859-5" to { ISO_8859_5.INSTANCE },
            "ISO-8859-7" to { ISO_8859_7.INSTANCE },
            "ISO-8859-9" to { ISO_8859_9.INSTANCE },
            "ISO-8859-13" to { ISO_8859_13.INSTANCE },
            "ISO-8859-15" to { ISO_8859_15.INSTANCE },
            "ISO-8859-16" to { ISO_8859_16.INSTANCE },
            "windows-1250" to { MS1250.INSTANCE },
            "windows-1251" to { MS1251.INSTANCE },
            "windows-1252" to { MS1252.INSTANCE },
            "windows-1253" to { MS1253.INSTANCE },
            "windows-1254" to { MS1254.INSTANCE },
            "windows-1257" to { MS1257.INSTANCE },
            "IBM437" to { IBM437.INSTANCE },
            "x-IBM737" to { IBM737.INSTANCE },
            "IBM775" to { IBM775.INSTANCE },
            "IBM850" to { IBM850.INSTANCE },
            "IBM852" to { IBM852.INSTANCE },
            "IBM855" to { IBM855.INSTANCE },
            "IBM857" to { IBM857.INSTANCE },
            "IBM00858" to { IBM858.INSTANCE },
            "IBM862" to { IBM862.INSTANCE },
            "IBM866" to { IBM866.INSTANCE },
            "x-IBM874" to { IBM874.INSTANCE },
            "US-ASCII" to { US_ASCII.INSTANCE },
            "KOI8-R" to { KOI8_R.INSTANCE },
            "KOI8-U" to { KOI8_U.INSTANCE },
            "CESU-8" to { CESU_8.INSTANCE },
            "GB18030" to { GB18030.INSTANCE },
            "GB2312" to { EUC_CN.INSTANCE },
            "JIS_X0201" to { JIS_X_0201.INSTANCE },
            "x-JIS0208" to { JIS_X_0208.INSTANCE },
            "JIS_X0212-1990" to { JIS_X_0212.INSTANCE },
            "EUC-JP" to { EUC_JP.INSTANCE },
            "x-euc-jp-linux" to { EUC_JP_LINUX.INSTANCE },
            "x-eucJP-Open" to { EUC_JP_Open.INSTANCE },
            "EUC-KR" to { EUC_KR.INSTANCE }
        )
    }

    private fun getCharsetByName(name: String): Charset? {
        return charsetMap[name]?.invoke()
    }
}