package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode
import com.fleeksoft.charset.cs.UnicodeDecoder
import com.fleeksoft.charset.cs.UnicodeEncoder

internal class UTF_16LE_BOM : Unicode("x-UTF-16LE-BOM") {

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : UnicodeDecoder(cs, NONE, LITTLE)

    private class Encoder(cs: Charset) : UnicodeEncoder(cs, LITTLE, true)


    companion object {
        val INSTANCE = UTF_16LE_BOM()
    }
}
