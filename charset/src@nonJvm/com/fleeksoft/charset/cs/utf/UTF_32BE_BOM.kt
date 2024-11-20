package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode

class UTF_32BE_BOM : Unicode("X-UTF-32BE-BOM") {

    override fun newDecoder(): CharsetDecoder {
        return UTF_32Coder.Decoder(this, UTF_32Coder.BIG)
    }

    override fun newEncoder(): CharsetEncoder {
        return UTF_32Coder.Encoder(this, UTF_32Coder.BIG, true)
    }

    companion object {
        val INSTANCE: UTF_32BE_BOM = UTF_32BE_BOM()
    }
}
