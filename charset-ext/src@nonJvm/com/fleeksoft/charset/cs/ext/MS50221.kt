package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.cs.jis.JIS_X_0212

class MS50221 : MS50220("x-windows-50221") {

    override fun contains(cs: Charset): Boolean {
        return super.contains(cs) || (cs is JIS_X_0212) || (cs is MS50221)
    }

    override fun doSBKANA(): Boolean {
        return true
    }
}
