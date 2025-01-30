package com.fleeksoft.charset.spi

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.cs.ext.*

class ExtendedBigCharsetProvider : CharsetProvider() {
    override fun charsetForName(charsetName: String): Charset? {
        return getExtCharsetByName(charsetName)
    }

    private fun getExtCharsetByName(name: String): Charset? {
        return charsetMap[name]?.invoke()
    }

    private val charsetMap = mapOf<String, () -> Charset>(
        "x-IBM964" to { IBM964() },
        "x-IBM33722" to { IBM33722() },
        "ISO-2022-CN" to { ISO2022_CN() },
        "x-ISO-2022-CN-CNS" to { ISO2022_CN_CNS() },
        "x-EUC-TW" to { EUC_TW() },
        "x-ISO-2022-CN-GB" to { ISO2022_CN_GB() },

        // can be separate module
        "x-windows-949" to { MS949() },
        "x-Johab" to { Johab() },
        "x-IBM948" to { IBM948() },
        "x-IBM950" to { IBM950() },
        "x-IBM937" to { IBM937() },
        "x-IBM1364" to { IBM1364() },
        "x-IBM933" to { IBM933() },
        "x-windows-950" to { MS950() },
        "x-mswin-936" to { MS936() },
        "x-MS950-HKSCS-XP" to { MS950_HKSCS_XP() },
        "x-MS950-HKSCS" to { MS950_HKSCS() },
        "x-IBM834" to { IBM834() },
        "GB18030" to { GB18030.INSTANCE },
    )
}