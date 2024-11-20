package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode

class UTF_32 : Unicode("UTF-32") {
    override fun newDecoder(): CharsetDecoder {
        return UTF_32Coder.Decoder(this, UTF_32Coder.NONE)
    }

    override fun newEncoder(): CharsetEncoder {
        return UTF_32Coder.Encoder(this, UTF_32Coder.BIG, false)
    }


    companion object {
        val INSTANCE: UTF_32 = UTF_32()
    }
}
