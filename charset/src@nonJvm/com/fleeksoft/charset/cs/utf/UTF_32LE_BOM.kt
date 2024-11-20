package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode

class UTF_32LE_BOM : Unicode("X-UTF-32LE-BOM") {

    override fun newDecoder(): CharsetDecoder {
        return UTF_32Coder.Decoder(this, UTF_32Coder.LITTLE)
    }

    override fun newEncoder(): CharsetEncoder {
        return UTF_32Coder.Encoder(this, UTF_32Coder.LITTLE, true)
    }

    companion object {
        val INSTANCE = UTF_32LE_BOM()
    }
}
