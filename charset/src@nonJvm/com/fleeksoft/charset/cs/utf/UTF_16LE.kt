package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode
import com.fleeksoft.charset.cs.UnicodeDecoder
import com.fleeksoft.charset.cs.UnicodeEncoder

class UTF_16LE : Unicode("UTF-16LE") {

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : UnicodeDecoder(cs, LITTLE)

    private class Encoder(cs: Charset) : UnicodeEncoder(cs, LITTLE, false)

    companion object {
        val INSTANCE: UTF_16LE = UTF_16LE()
    }
}
