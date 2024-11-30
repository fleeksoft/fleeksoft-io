package com.fleeksoft.charset.cs.euc

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.jis.JIS_X_0201


class EUC_JP_LINUX : Charset("x-euc-jp-linux", null) {
    companion object {
        val INSTANCE = EUC_JP_LINUX()
    }

    override fun contains(cs: Charset): Boolean {
        return ((cs is JIS_X_0201) || (cs.name() == "US-ASCII") || (cs is EUC_JP_LINUX))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : EUC_JP.Decoder(cs, 1.0f, 1.0f, DEC0201, DEC0208, null)

    private class Encoder(cs: Charset) : EUC_JP.Encoder(cs, 2.0f, 2.0f, ENC0201, ENC0208, null)
}
