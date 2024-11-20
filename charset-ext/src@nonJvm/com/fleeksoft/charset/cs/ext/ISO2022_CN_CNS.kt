package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder


class ISO2022_CN_CNS : ISO2022("x-ISO-2022-CN-CNS") {
    override fun contains(cs: Charset): Boolean {
        // overlapping repertoire of EUC_TW, CNS11643
        return ((cs is EUC_TW) || (cs.name() == "US-ASCII") || (cs is ISO2022_CN_CNS))
    }

    

    override fun newDecoder(): CharsetDecoder {
        return ISO2022_CN.Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Encoder(cs: Charset) : ISO2022.Encoder(cs) {
        private val bb = ByteArray(4)

        init {
            SODesig = SOD
            SS2Desig = SS2D
            SS3Desig = SS3D
            ISOEncoder = cns.newEncoder()
        }

        override fun canEncode(c: Char): Boolean {
            var n: Int? = null
            return (c <= '\u007f' || ((ISOEncoder as EUC_TW.Encoder).toEUC(c, bb).also { n = it }) == 2 ||
                    (n == 4 && bb[0] == SS2 && (bb[1] == PLANE2 || bb[1] == PLANE3)))
        }

        /*
         * Since ISO2022-CN-CNS possesses a CharsetEncoder
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
            private val cns: Charset = EUC_TW()

            private val SOD = byteArrayOf('$'.code.toByte(), ')'.code.toByte(), 'G'.code.toByte())
            private val SS2D = byteArrayOf('$'.code.toByte(), '*'.code.toByte(), 'H'.code.toByte())
            private val SS3D = byteArrayOf('$'.code.toByte(), '+'.code.toByte(), 'I'.code.toByte())
        }
    }
}
