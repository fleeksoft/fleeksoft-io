package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte

class MSISO2022JP : ISO2022_JP("x-windows-iso2022jp") {

    override fun contains(cs: Charset): Boolean {
        return super.contains(cs) || (cs is MSISO2022JP)
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this, CoderHolder.DEC0208, null)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this, CoderHolder.ENC0208, null, true)
    }

    private object CoderHolder {
        val DEC0208: DoubleByte.Decoder = JIS_X_0208_MS932().newDecoder() as DoubleByte.Decoder
        val ENC0208: DoubleByte.Encoder = JIS_X_0208_MS932().newEncoder() as DoubleByte.Encoder
    }
}
