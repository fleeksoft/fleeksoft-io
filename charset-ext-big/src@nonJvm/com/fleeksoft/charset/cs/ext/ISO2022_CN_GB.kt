package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.euc.EUC_CN


class ISO2022_CN_GB : ISO2022("x-ISO-2022-CN-GB") {
    override fun contains(cs: Charset): Boolean {
        // overlapping repertoire of EUC_CN, GB2312
        return ((cs is EUC_CN) || (cs.name() == "US-ASCII") || (cs is ISO2022_CN_GB))
    }

    override fun newDecoder(): CharsetDecoder {
        return ISO2022_CN.Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Encoder(cs: Charset) : ISO2022.Encoder(cs) {
        init {
            SODesig = SOD
            ISOEncoder = gb2312.newEncoder()
        }

        /*
         * Since ISO2022-CN-GB possesses a CharsetEncoder
         * without the corresponding CharsetDecoder half the
         * default replacement check needs to be overridden
         * since the parent class version attempts to
         * decode 0x3f (?).
         */
        override fun isLegalReplacement(repl: ByteArray): Boolean {
            // 0x3f is OK as the replacement byte
            return (repl.size == 1 && repl[0] == 0x3f.toByte())
        }

        companion object {
            private val gb2312: Charset = EUC_CN()

            private val SOD = byteArrayOf('$'.code.toByte(), ')'.code.toByte(), 'A'.code.toByte())
        }
    }
}
