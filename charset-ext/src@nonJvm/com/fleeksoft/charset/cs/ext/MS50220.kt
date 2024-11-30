package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.jis.JIS_X_0212

open class MS50220 : ISO2022_JP {
    constructor() : super("x-windows-50220")

    protected constructor(canonicalName: String) : super(canonicalName)

    override fun contains(cs: Charset): Boolean {
        return super.contains(cs) ||
                (cs is JIS_X_0212) ||
                (cs is MS50220)
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this, Holder.DEC0208, Holder.DEC0212)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this, Holder.ENC0208, Holder.ENC0212, doSBKANA())
    }

    private object Holder {
        val DEC0208 = JIS_X_0208_MS5022X().newDecoder() as DoubleByte.Decoder

        val DEC0212 = JIS_X_0212_MS5022X().newDecoder() as DoubleByte.Decoder

        val ENC0208 = JIS_X_0208_MS5022X().newEncoder() as DoubleByte.Encoder

        val ENC0212 = JIS_X_0212_MS5022X().newEncoder() as DoubleByte.Encoder
    }

    override fun doSBKANA(): Boolean {
        return false
    }
}
