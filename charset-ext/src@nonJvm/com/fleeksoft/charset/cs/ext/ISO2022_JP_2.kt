package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.jis.JIS_X_0212

class ISO2022_JP_2 : ISO2022_JP("ISO-2022-JP-2") {
    

    override fun contains(cs: Charset): Boolean {
        return super.contains(cs) || (cs is JIS_X_0212) || (cs is ISO2022_JP_2)
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this, Decoder.DEC0208, CoderHolder.DEC0212)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this, Encoder.ENC0208, CoderHolder.ENC0212, true)
    }

    private object CoderHolder {
        val DEC0212: DoubleByte.Decoder = JIS_X_0212().newDecoder() as DoubleByte.Decoder
        val ENC0212: DoubleByte.Encoder = JIS_X_0212().newEncoder() as DoubleByte.Encoder
    }
}
