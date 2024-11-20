package com.fleeksoft.charset.cs.ext


import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

class ISCII91 : Charset("x-ISCII91", null) {


    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is ISCII91))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f) {
        private var contextChar = INVALID_CHAR
        private var needFlushing = false


        override fun implFlush(out: CharBuffer): CoderResult {
            if (needFlushing) {
                if (out.remaining() < 1) {
                    return CoderResultInternal.OVERFLOW
                } else {
                    out.put(contextChar)
                }
            }
            contextChar = INVALID_CHAR
            needFlushing = false
            return CoderResultInternal.UNDERFLOW
        }

        /* Rules:
         * 1) ATR,EXT,following character to be replaced with '\ufffd'
         * 2) Halant + Halant => '\u094d' (Virama) + '\u200c'(ZWNJ)
         * 3) Halant + Nukta => '\u094d' (Virama) + '\u200d'(ZWJ)
         */
        fun decodeArrayLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    var index = sa[sp].toInt()
                    index = if (index < 0) (index + 255) else index
                    val currentChar = directMapTable[index]

                    // if the contextChar is either ATR || EXT
                    // set the output to '\ufffd'
                    if (contextChar == '\ufffd') {
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = '\ufffd'
                        contextChar = INVALID_CHAR
                        needFlushing = false
                        sp++
                        continue
                    }

                    when (currentChar) {
                        '\u0901', '\u0907', '\u0908', '\u090b', '\u093f', '\u0940', '\u0943', '\u0964' -> {
                            if (needFlushing) {
                                if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                                da[dp++] = contextChar
                                contextChar = currentChar
                                sp++
                                continue
                            }
                            contextChar = currentChar
                            needFlushing = true
                            sp++
                            continue
                        }

                        NUKTA_CHAR -> {
                            if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                            when (contextChar) {
                                '\u0901' -> da[dp++] = '\u0950'
                                '\u0907' -> da[dp++] = '\u090c'
                                '\u0908' -> da[dp++] = '\u0961'
                                '\u090b' -> da[dp++] = '\u0960'
                                '\u093f' -> da[dp++] = '\u0962'
                                '\u0940' -> da[dp++] = '\u0963'
                                '\u0943' -> da[dp++] = '\u0944'
                                '\u0964' -> da[dp++] = '\u093d'
                                HALANT_CHAR -> {
                                    if (needFlushing) {
                                        da[dp++] = contextChar
                                        contextChar = currentChar
                                        sp++
                                        continue
                                    }
                                    da[dp++] = ZWJ_CHAR
                                }

                                else -> {
                                    if (needFlushing) {
                                        da[dp++] = contextChar
                                        contextChar = currentChar
                                        sp++
                                        continue
                                    }
                                    da[dp++] = NUKTA_CHAR
                                }
                            }
                        }

                        HALANT_CHAR -> {
                            if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                            if (needFlushing) {
                                da[dp++] = contextChar
                                contextChar = currentChar
                                sp++
                                continue
                            }
                            if (contextChar == HALANT_CHAR) {
                                da[dp++] = ZWNJ_CHAR
                                break
                            }
                            da[dp++] = HALANT_CHAR
                        }

                        INVALID_CHAR -> {
                            if (needFlushing) {
                                if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                                da[dp++] = contextChar
                                contextChar = currentChar
                                sp++
                                continue
                            }
                            return CoderResultInternal.unmappableForLength(1)
                        }

                        else -> {
                            if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                            if (needFlushing) {
                                da[dp++] = contextChar
                                contextChar = currentChar
                                sp++
                                continue
                            }
                            da[dp++] = currentChar
                        }
                    } //end switch

                    contextChar = currentChar
                    needFlushing = false
                    sp++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()

            try {
                while (src.hasRemaining()) {
                    var index = src.getInt()
                    index = if (index < 0) (index + 255) else index
                    val currentChar = directMapTable[index]

                    // if the contextChar is either ATR || EXT
                    // set the output to '\ufffd'
                    if (contextChar == '\ufffd') {
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put('\ufffd')
                        contextChar = INVALID_CHAR
                        needFlushing = false
                        mark++
                        continue
                    }

                    when (currentChar) {
                        '\u0901', '\u0907', '\u0908', '\u090b', '\u093f', '\u0940', '\u0943', '\u0964' -> {
                            if (needFlushing) {
                                if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                                dst.put(contextChar)
                                contextChar = currentChar
                                mark++
                                continue
                            }
                            contextChar = currentChar
                            needFlushing = true
                            mark++
                            continue
                        }

                        NUKTA_CHAR -> {
                            if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                            when (contextChar) {
                                '\u0901' -> dst.put('\u0950')
                                '\u0907' -> dst.put('\u090c')
                                '\u0908' -> dst.put('\u0961')
                                '\u090b' -> dst.put('\u0960')
                                '\u093f' -> dst.put('\u0962')
                                '\u0940' -> dst.put('\u0963')
                                '\u0943' -> dst.put('\u0944')
                                '\u0964' -> dst.put('\u093d')
                                HALANT_CHAR -> {
                                    if (needFlushing) {
                                        dst.put(contextChar)
                                        contextChar = currentChar
                                        mark++
                                        continue
                                    }
                                    dst.put(ZWJ_CHAR)
                                }

                                else -> {
                                    if (needFlushing) {
                                        dst.put(contextChar)
                                        contextChar = currentChar
                                        mark++
                                        continue
                                    }
                                    dst.put(NUKTA_CHAR)
                                }
                            }
                        }

                        HALANT_CHAR -> {
                            if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                            if (needFlushing) {
                                dst.put(contextChar)
                                contextChar = currentChar
                                mark++
                                continue
                            }
                            if (contextChar == HALANT_CHAR) {
                                dst.put(ZWNJ_CHAR)
                                break
                            }
                            dst.put(HALANT_CHAR)
                        }

                        INVALID_CHAR -> {
                            if (needFlushing) {
                                if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                                dst.put(contextChar)
                                contextChar = currentChar
                                mark++
                                continue
                            }
                            return CoderResultInternal.unmappableForLength(1)
                        }

                        else -> {
                            if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                            if (needFlushing) {
                                dst.put(contextChar)
                                contextChar = currentChar
                                mark++
                                continue
                            }
                            dst.put(currentChar)
                        }
                    } //end switch
                    contextChar = currentChar
                    needFlushing = false
                    mark++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                decodeArrayLoop(src, dst)
            } else {
                decodeBufferLoop(src, dst)
            }
        }

        companion object {
            private const val ZWNJ_CHAR = '\u200c'
            private const val ZWJ_CHAR = '\u200d'
            private const val INVALID_CHAR = '\uffff'
        }
    }

    private class Encoder(cs: Charset) : CharsetEncoder(cs, 2.0f, 2.0f) {
        //private static CharToByteISCII91 c2b = new CharToByteISCII91();
        //private static final byte[] directMapTable = c2b.getISCIIEncoderMap();
        private val sgp: Surrogate.Parser = Surrogate.Parser()

        override fun canEncode(ch: Char): Boolean {
            //check for Devanagari range,ZWJ,ZWNJ and ASCII range.
            return ((ch >= '\u0900' && ch <= '\u097f' && encoderMappingTable[2 * (ch.code - '\u0900'.code)] != NO_CHAR) ||
                    (ch == '\u200d') ||
                    (ch == '\u200c') ||
                    (ch <= '\u007f'))
        }


        fun encodeArrayLoop(
            src: CharBuffer,
            dst: ByteBuffer
        ): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                var inputChar: Char
                while (sp < sl) {
                    var index: Int = Int.MIN_VALUE
                    inputChar = sa[sp]

                    if (inputChar.code >= 0x0000 && inputChar.code <= 0x007f) {
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = inputChar.code.toByte()
                        sp++
                        continue
                    }

                    // if inputChar == ZWJ replace it with halant
                    // if inputChar == ZWNJ replace it with Nukta
                    if (inputChar.code == 0x200c) {
                        inputChar = HALANT_CHAR
                    } else if (inputChar.code == 0x200d) {
                        inputChar = NUKTA_CHAR
                    }

                    if (inputChar.code >= 0x0900 && inputChar.code <= 0x097f) {
                        index = ((inputChar).code - 0x0900) * 2
                    }

                    if (Character.isSurrogate(inputChar)) {
                        if (sgp.parse(inputChar, sa, sp, sl) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }

                    if (index == Int.MIN_VALUE ||
                        encoderMappingTable[index] == NO_CHAR
                    ) {
                        return CoderResultInternal.unmappableForLength(1)
                    } else {
                        if (encoderMappingTable[index + 1] == NO_CHAR) {
                            if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                            da[dp++] = encoderMappingTable[index]
                        } else {
                            if (dl - dp < 2) return CoderResultInternal.OVERFLOW
                            da[dp++] = encoderMappingTable[index]
                            da[dp++] = encoderMappingTable[index + 1]
                        }
                        sp++
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark = src.position()

            try {
                var inputChar: Char
                while (src.hasRemaining()) {
                    var index: Int = Int.MIN_VALUE
                    inputChar = src.get()

                    if (inputChar.code >= 0x0000 && inputChar.code <= 0x007f) {
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put(inputChar.code.toByte())
                        mark++
                        continue
                    }

                    // if inputChar == ZWJ replace it with halant
                    // if inputChar == ZWNJ replace it with Nukta
                    if (inputChar.code == 0x200c) {
                        inputChar = HALANT_CHAR
                    } else if (inputChar.code == 0x200d) {
                        inputChar = NUKTA_CHAR
                    }

                    if (inputChar.code >= 0x0900 && inputChar.code <= 0x097f) {
                        index = ((inputChar).code - 0x0900) * 2
                    }

                    if (Character.isSurrogate(inputChar)) {
                        if (sgp.parse(inputChar, src) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }

                    if (index == Int.MIN_VALUE ||
                        encoderMappingTable[index] == NO_CHAR
                    ) {
                        return CoderResultInternal.unmappableForLength(1)
                    } else {
                        if (encoderMappingTable[index + 1] == NO_CHAR) {
                            if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                            dst.put(encoderMappingTable[index])
                        } else {
                            if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                            dst.put(encoderMappingTable[index])
                            dst.put(encoderMappingTable[index + 1])
                        }
                    }
                    mark++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                encodeArrayLoop(src, dst)
            } else {
                encodeBufferLoop(src, dst)
            }
        }

        companion object {
            private val NO_CHAR = 255.toByte()
        }
    }

    companion object {
        private const val NUKTA_CHAR = '\u093c'
        private const val HALANT_CHAR = '\u094d'
        private val NO_CHAR = 255.toByte()

        private val directMapTable = charArrayOf(
            '\u0000',  // ascii character
            '\u0001',  // ascii character
            '\u0002',  // ascii character
            '\u0003',  // ascii character
            '\u0004',  // ascii character
            '\u0005',  // ascii character
            '\u0006',  // ascii character
            '\u0007',  // ascii character
            '\u0008',  // ascii character
            '\u0009',  // ascii character
            '\u000a',  // ascii character
            '\u000b',  // ascii character
            '\u000c',  // ascii character
            '\u000d',  // ascii character
            '\u000e',  // ascii character
            '\u000f',  // ascii character
            '\u0010',  // ascii character
            '\u0011',  // ascii character
            '\u0012',  // ascii character
            '\u0013',  // ascii character
            '\u0014',  // ascii character
            '\u0015',  // ascii character
            '\u0016',  // ascii character
            '\u0017',  // ascii character
            '\u0018',  // ascii character
            '\u0019',  // ascii character
            '\u001a',  // ascii character
            '\u001b',  // ascii character
            '\u001c',  // ascii character
            '\u001d',  // ascii character
            '\u001e',  // ascii character
            '\u001f',  // ascii character
            '\u0020',  // ascii character
            '\u0021',  // ascii character
            '\u0022',  // ascii character
            '\u0023',  // ascii character
            '\u0024',  // ascii character
            '\u0025',  // ascii character
            '\u0026',  // ascii character
            0x0027.toChar(),  // '\u0027' control -- ascii character
            '\u0028',  // ascii character
            '\u0029',  // ascii character
            '\u002a',  // ascii character
            '\u002b',  // ascii character
            '\u002c',  // ascii character
            '\u002d',  // ascii character
            '\u002e',  // ascii character
            '\u002f',  // ascii character
            '\u0030',  // ascii character
            '\u0031',  // ascii character
            '\u0032',  // ascii character
            '\u0033',  // ascii character
            '\u0034',  // ascii character
            '\u0035',  // ascii character
            '\u0036',  // ascii character
            '\u0037',  // ascii character
            '\u0038',  // ascii character
            '\u0039',  // ascii character
            '\u003a',  // ascii character
            '\u003b',  // ascii character
            '\u003c',  // ascii character
            '\u003d',  // ascii character
            '\u003e',  // ascii character
            '\u003f',  // ascii character
            '\u0040',  // ascii character
            '\u0041',  // ascii character
            '\u0042',  // ascii character
            '\u0043',  // ascii character
            '\u0044',  // ascii character
            '\u0045',  // ascii character
            '\u0046',  // ascii character
            '\u0047',  // ascii character
            '\u0048',  // ascii character
            '\u0049',  // ascii character
            '\u004a',  // ascii character
            '\u004b',  // ascii character
            '\u004c',  // ascii character
            '\u004d',  // ascii character
            '\u004e',  // ascii character
            '\u004f',  // ascii character
            '\u0050',  // ascii character
            '\u0051',  // ascii character
            '\u0052',  // ascii character
            '\u0053',  // ascii character
            '\u0054',  // ascii character
            '\u0055',  // ascii character
            '\u0056',  // ascii character
            '\u0057',  // ascii character
            '\u0058',  // ascii character
            '\u0059',  // ascii character
            '\u005a',  // ascii character
            '\u005b',  // ascii character
            '\\',  // '\u005c' -- ascii character
            '\u005d',  // ascii character
            '\u005e',  // ascii character
            '\u005f',  // ascii character
            '\u0060',  // ascii character
            '\u0061',  // ascii character
            '\u0062',  // ascii character
            '\u0063',  // ascii character
            '\u0064',  // ascii character
            '\u0065',  // ascii character
            '\u0066',  // ascii character
            '\u0067',  // ascii character
            '\u0068',  // ascii character
            '\u0069',  // ascii character
            '\u006a',  // ascii character
            '\u006b',  // ascii character
            '\u006c',  // ascii character
            '\u006d',  // ascii character
            '\u006e',  // ascii character
            '\u006f',  // ascii character
            '\u0070',  // ascii character
            '\u0071',  // ascii character
            '\u0072',  // ascii character
            '\u0073',  // ascii character
            '\u0074',  // ascii character
            '\u0075',  // ascii character
            '\u0076',  // ascii character
            '\u0077',  // ascii character
            '\u0078',  // ascii character
            '\u0079',  // ascii character
            '\u007a',  // ascii character
            '\u007b',  // ascii character
            '\u007c',  // ascii character
            '\u007d',  // ascii character
            '\u007e',  // ascii character
            '\u007f',  // ascii character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\uffff',  // unknown character
            '\u0901',  // a1 -- Vowel-modifier CHANDRABINDU
            '\u0902',  // a2 -- Vowel-modifier ANUSWAR
            '\u0903',  // a3 -- Vowel-modifier VISARG

            '\u0905',  // a4 -- Vowel A
            '\u0906',  // a5 -- Vowel AA
            '\u0907',  // a6 -- Vowel I
            '\u0908',  // a7 -- Vowel II
            '\u0909',  // a8 -- Vowel U
            '\u090a',  // a9 -- Vowel UU
            '\u090b',  // aa -- Vowel RI
            '\u090e',  // ab -- Vowel E ( Southern Scripts )
            '\u090f',  // ac -- Vowel EY
            '\u0910',  // ad -- Vowel AI
            '\u090d',  // ae -- Vowel AYE ( Devanagari Script )
            '\u0912',  // af -- Vowel O ( Southern Scripts )
            '\u0913',  // b0 -- Vowel OW
            '\u0914',  // b1 -- Vowel AU
            '\u0911',  // b2 -- Vowel AWE ( Devanagari Script )
            '\u0915',  // b3 -- Consonant KA
            '\u0916',  // b4 -- Consonant KHA
            '\u0917',  // b5 -- Consonant GA
            '\u0918',  // b6 -- Consonant GHA
            '\u0919',  // b7 -- Consonant NGA
            '\u091a',  // b8 -- Consonant CHA
            '\u091b',  // b9 -- Consonant CHHA
            '\u091c',  // ba -- Consonant JA
            '\u091d',  // bb -- Consonant JHA
            '\u091e',  // bc -- Consonant JNA
            '\u091f',  // bd -- Consonant Hard TA
            '\u0920',  // be -- Consonant Hard THA
            '\u0921',  // bf -- Consonant Hard DA
            '\u0922',  // c0 -- Consonant Hard DHA
            '\u0923',  // c1 -- Consonant Hard NA
            '\u0924',  // c2 -- Consonant Soft TA
            '\u0925',  // c3 -- Consonant Soft THA
            '\u0926',  // c4 -- Consonant Soft DA
            '\u0927',  // c5 -- Consonant Soft DHA
            '\u0928',  // c6 -- Consonant Soft NA
            '\u0929',  // c7 -- Consonant NA ( Tamil )
            '\u092a',  // c8 -- Consonant PA
            '\u092b',  // c9 -- Consonant PHA
            '\u092c',  // ca -- Consonant BA
            '\u092d',  // cb -- Consonant BHA
            '\u092e',  // cc -- Consonant MA
            '\u092f',  // cd -- Consonant YA
            '\u095f',  // ce -- Consonant JYA ( Bengali, Assamese & Oriya )
            '\u0930',  // cf -- Consonant RA
            '\u0931',  // d0 -- Consonant Hard RA ( Southern Scripts )
            '\u0932',  // d1 -- Consonant LA
            '\u0933',  // d2 -- Consonant Hard LA
            '\u0934',  // d3 -- Consonant ZHA ( Tamil & Malayalam )
            '\u0935',  // d4 -- Consonant VA
            '\u0936',  // d5 -- Consonant SHA
            '\u0937',  // d6 -- Consonant Hard SHA
            '\u0938',  // d7 -- Consonant SA
            '\u0939',  // d8 -- Consonant HA

            '\u200d',  // d9 -- Consonant INVISIBLE
            '\u093e',  // da -- Vowel Sign AA

            '\u093f',  // db -- Vowel Sign I
            '\u0940',  // dc -- Vowel Sign II
            '\u0941',  // dd -- Vowel Sign U
            '\u0942',  // de -- Vowel Sign UU
            '\u0943',  // df -- Vowel Sign RI
            '\u0946',  // e0 -- Vowel Sign E ( Southern Scripts )
            '\u0947',  // e1 -- Vowel Sign EY
            '\u0948',  // e2 -- Vowel Sign AI
            '\u0945',  // e3 -- Vowel Sign AYE ( Devanagari Script )
            '\u094a',  // e4 -- Vowel Sign O ( Southern Scripts )
            '\u094b',  // e5 -- Vowel Sign OW
            '\u094c',  // e6 -- Vowel Sign AU
            '\u0949',  // e7 -- Vowel Sign AWE ( Devanagari Script )

            '\u094d',  // e8 -- Vowel Omission Sign ( Halant )
            '\u093c',  // e9 -- Diacritic Sign ( Nukta )
            '\u0964',  // ea -- Full Stop ( Viram, Northern Scripts )

            '\uffff',  // eb -- This position shall not be used
            '\uffff',  // ec -- This position shall not be used
            '\uffff',  // ed -- This position shall not be used
            '\uffff',  // ee -- This position shall not be used

            '\ufffd',  // ef -- Attribute Code ( ATR )
            '\ufffd',  // f0 -- Extension Code ( EXT )

            '\u0966',  // f1 -- Digit 0
            '\u0967',  // f2 -- Digit 1
            '\u0968',  // f3 -- Digit 2
            '\u0969',  // f4 -- Digit 3
            '\u096a',  // f5 -- Digit 4
            '\u096b',  // f6 -- Digit 5
            '\u096c',  // f7 -- Digit 6
            '\u096d',  // f8 -- Digit 7
            '\u096e',  // f9 -- Digit 8
            '\u096f',  // fa -- Digit 9

            '\uffff',  // fb -- This position shall not be used
            '\uffff',  // fc -- This position shall not be used
            '\uffff',  // fd -- This position shall not be used
            '\uffff',  // fe -- This position shall not be used
            '\uffff' // ff -- This position shall not be used
        ) //end of table definition

        private val encoderMappingTable = byteArrayOf(
            NO_CHAR, NO_CHAR,  //0900 <reserved>
            161.toByte(), NO_CHAR,  //0901 -- DEVANAGARI SIGN CANDRABINDU = anunasika
            162.toByte(), NO_CHAR,  //0902 -- DEVANAGARI SIGN ANUSVARA = bindu
            163.toByte(), NO_CHAR,  //0903 -- DEVANAGARI SIGN VISARGA
            NO_CHAR, NO_CHAR,  //0904 <reserved>
            164.toByte(), NO_CHAR,  //0905 -- DEVANAGARI LETTER A
            165.toByte(), NO_CHAR,  //0906 -- DEVANAGARI LETTER AA
            166.toByte(), NO_CHAR,  //0907 -- DEVANAGARI LETTER I
            167.toByte(), NO_CHAR,  //0908 -- DEVANAGARI LETTER II
            168.toByte(), NO_CHAR,  //0909 -- DEVANAGARI LETTER U
            169.toByte(), NO_CHAR,  //090a -- DEVANAGARI LETTER UU
            170.toByte(), NO_CHAR,  //090b -- DEVANAGARI LETTER VOCALIC R
            166.toByte(), 233.toByte(),  //090c -- DEVANAGARI LETTER VOVALIC L
            174.toByte(), NO_CHAR,  //090d -- DEVANAGARI LETTER CANDRA E
            171.toByte(), NO_CHAR,  //090e -- DEVANAGARI LETTER SHORT E
            172.toByte(), NO_CHAR,  //090f -- DEVANAGARI LETTER E
            173.toByte(), NO_CHAR,  //0910 -- DEVANAGARI LETTER AI
            178.toByte(), NO_CHAR,  //0911 -- DEVANAGARI LETTER CANDRA O
            175.toByte(), NO_CHAR,  //0912 -- DEVANAGARI LETTER SHORT O
            176.toByte(), NO_CHAR,  //0913 -- DEVANAGARI LETTER O
            177.toByte(), NO_CHAR,  //0914 -- DEVANAGARI LETTER AU
            179.toByte(), NO_CHAR,  //0915 -- DEVANAGARI LETTER KA
            180.toByte(), NO_CHAR,  //0916 -- DEVANAGARI LETTER KHA
            181.toByte(), NO_CHAR,  //0917 -- DEVANAGARI LETTER GA
            182.toByte(), NO_CHAR,  //0918 -- DEVANAGARI LETTER GHA
            183.toByte(), NO_CHAR,  //0919 -- DEVANAGARI LETTER NGA
            184.toByte(), NO_CHAR,  //091a -- DEVANAGARI LETTER CA
            185.toByte(), NO_CHAR,  //091b -- DEVANAGARI LETTER CHA
            186.toByte(), NO_CHAR,  //091c -- DEVANAGARI LETTER JA
            187.toByte(), NO_CHAR,  //091d -- DEVANAGARI LETTER JHA
            188.toByte(), NO_CHAR,  //091e -- DEVANAGARI LETTER NYA
            189.toByte(), NO_CHAR,  //091f -- DEVANAGARI LETTER TTA
            190.toByte(), NO_CHAR,  //0920 -- DEVANAGARI LETTER TTHA
            191.toByte(), NO_CHAR,  //0921 -- DEVANAGARI LETTER DDA
            192.toByte(), NO_CHAR,  //0922 -- DEVANAGARI LETTER DDHA
            193.toByte(), NO_CHAR,  //0923 -- DEVANAGARI LETTER NNA
            194.toByte(), NO_CHAR,  //0924 -- DEVANAGARI LETTER TA
            195.toByte(), NO_CHAR,  //0925 -- DEVANAGARI LETTER THA
            196.toByte(), NO_CHAR,  //0926 -- DEVANAGARI LETTER DA
            197.toByte(), NO_CHAR,  //0927 -- DEVANAGARI LETTER DHA
            198.toByte(), NO_CHAR,  //0928 -- DEVANAGARI LETTER NA
            199.toByte(), NO_CHAR,  //0929 -- DEVANAGARI LETTER NNNA <=> 0928 + 093C
            200.toByte(), NO_CHAR,  //092a -- DEVANAGARI LETTER PA
            201.toByte(), NO_CHAR,  //092b -- DEVANAGARI LETTER PHA
            202.toByte(), NO_CHAR,  //092c -- DEVANAGARI LETTER BA
            203.toByte(), NO_CHAR,  //092d -- DEVANAGARI LETTER BHA
            204.toByte(), NO_CHAR,  //092e -- DEVANAGARI LETTER MA
            205.toByte(), NO_CHAR,  //092f -- DEVANAGARI LETTER YA
            207.toByte(), NO_CHAR,  //0930 -- DEVANAGARI LETTER RA
            208.toByte(), NO_CHAR,  //0931 -- DEVANAGARI LETTER RRA <=> 0930 + 093C
            209.toByte(), NO_CHAR,  //0932 -- DEVANAGARI LETTER LA
            210.toByte(), NO_CHAR,  //0933 -- DEVANAGARI LETTER LLA
            211.toByte(), NO_CHAR,  //0934 -- DEVANAGARI LETTER LLLA <=> 0933 + 093C
            212.toByte(), NO_CHAR,  //0935 -- DEVANAGARI LETTER VA
            213.toByte(), NO_CHAR,  //0936 -- DEVANAGARI LETTER SHA
            214.toByte(), NO_CHAR,  //0937 -- DEVANAGARI LETTER SSA
            215.toByte(), NO_CHAR,  //0938 -- DEVANAGARI LETTER SA
            216.toByte(), NO_CHAR,  //0939 -- DEVANAGARI LETTER HA
            NO_CHAR, NO_CHAR,  //093a <reserved>
            NO_CHAR, NO_CHAR,  //093b <reserved>
            233.toByte(), NO_CHAR,  //093c -- DEVANAGARI SIGN NUKTA
            234.toByte(), 233.toByte(),  //093d -- DEVANAGARI SIGN AVAGRAHA
            218.toByte(), NO_CHAR,  //093e -- DEVANAGARI VOWEL SIGN AA
            219.toByte(), NO_CHAR,  //093f -- DEVANAGARI VOWEL SIGN I
            220.toByte(), NO_CHAR,  //0940 -- DEVANAGARI VOWEL SIGN II
            221.toByte(), NO_CHAR,  //0941 -- DEVANAGARI VOWEL SIGN U
            222.toByte(), NO_CHAR,  //0942 -- DEVANAGARI VOWEL SIGN UU
            223.toByte(), NO_CHAR,  //0943 -- DEVANAGARI VOWEL SIGN VOCALIC R
            223.toByte(), 233.toByte(),  //0944 -- DEVANAGARI VOWEL SIGN VOCALIC RR
            227.toByte(), NO_CHAR,  //0945 -- DEVANAGARI VOWEL SIGN CANDRA E
            224.toByte(), NO_CHAR,  //0946 -- DEVANAGARI VOWEL SIGN SHORT E
            225.toByte(), NO_CHAR,  //0947 -- DEVANAGARI VOWEL SIGN E
            226.toByte(), NO_CHAR,  //0948 -- DEVANAGARI VOWEL SIGN AI
            231.toByte(), NO_CHAR,  //0949 -- DEVANAGARI VOWEL SIGN CANDRA O
            228.toByte(), NO_CHAR,  //094a -- DEVANAGARI VOWEL SIGN SHORT O
            229.toByte(), NO_CHAR,  //094b -- DEVANAGARI VOWEL SIGN O
            230.toByte(), NO_CHAR,  //094c -- DEVANAGARI VOWEL SIGN AU
            232.toByte(), NO_CHAR,  //094d -- DEVANAGARI SIGN VIRAMA ( halant )
            NO_CHAR, NO_CHAR,  //094e <reserved>
            NO_CHAR, NO_CHAR,  //094f <reserved>
            161.toByte(), 233.toByte(),  //0950 -- DEVANAGARI OM
            240.toByte(), 181.toByte(),  //0951 -- DEVANAGARI STRESS SIGN UDATTA
            240.toByte(), 184.toByte(),  //0952 -- DEVANAGARI STRESS SIGN ANUDATTA
            254.toByte(), NO_CHAR,  //0953 -- DEVANAGARI GRAVE ACCENT || MISSING
            254.toByte(), NO_CHAR,  //0954 -- DEVANAGARI ACUTE ACCENT || MISSING
            NO_CHAR, NO_CHAR,  //0955 <reserved>
            NO_CHAR, NO_CHAR,  //0956 <reserved>
            NO_CHAR, NO_CHAR,  //0957 <reserved>
            179.toByte(), 233.toByte(),  //0958 -- DEVANAGARI LETTER QA <=> 0915 + 093C
            180.toByte(), 233.toByte(),  //0959 -- DEVANAGARI LETTER KHHA <=> 0916 + 093C
            181.toByte(), 233.toByte(),  //095a -- DEVANAGARI LETTER GHHA <=> 0917 + 093C
            186.toByte(), 233.toByte(),  //095b -- DEVANAGARI LETTER ZA <=> 091C + 093C
            191.toByte(), 233.toByte(),  //095c -- DEVANAGARI LETTER DDDHA <=> 0921 + 093C
            192.toByte(), 233.toByte(),  //095d -- DEVANAGARI LETTER RHA <=> 0922 + 093C
            201.toByte(), 233.toByte(),  //095e -- DEVANAGARI LETTER FA <=> 092B + 093C
            206.toByte(), NO_CHAR,  //095f -- DEVANAGARI LETTER YYA <=> 092F + 093C
            170.toByte(), 233.toByte(),  //0960 -- DEVANAGARI LETTER VOCALIC RR
            167.toByte(), 233.toByte(),  //0961 -- DEVANAGARI LETTER VOCALIC LL
            219.toByte(), 233.toByte(),  //0962 -- DEVANAGARI VOWEL SIGN VOCALIC L
            220.toByte(), 233.toByte(),  //0963 -- DEVANAGARI VOWEL SIGN VOCALIC LL
            234.toByte(), NO_CHAR,  //0964 -- DEVANAGARI DANDA ( phrase separator )
            234.toByte(), 234.toByte(),  //0965 -- DEVANAGARI DOUBLE DANDA
            241.toByte(), NO_CHAR,  //0966 -- DEVANAGARI DIGIT ZERO
            242.toByte(), NO_CHAR,  //0967 -- DEVANAGARI DIGIT ONE
            243.toByte(), NO_CHAR,  //0968 -- DEVANAGARI DIGIT TWO
            244.toByte(), NO_CHAR,  //0969 -- DEVANAGARI DIGIT THREE
            245.toByte(), NO_CHAR,  //096a -- DEVANAGARI DIGIT FOUR
            246.toByte(), NO_CHAR,  //096b -- DEVANAGARI DIGIT FIVE
            247.toByte(), NO_CHAR,  //096c -- DEVANAGARI DIGIT SIX
            248.toByte(), NO_CHAR,  //096d -- DEVANAGARI DIGIT SEVEN
            249.toByte(), NO_CHAR,  //096e -- DEVANAGARI DIGIT EIGHT
            250.toByte(), NO_CHAR,  //096f -- DEVANAGARI DIGIT NINE
            240.toByte(), 191.toByte(),  //0970 -- DEVANAGARI ABBREVIATION SIGN
            NO_CHAR, NO_CHAR,  //0971 -- reserved
            NO_CHAR, NO_CHAR,  //0972 -- reserved
            NO_CHAR, NO_CHAR,  //0973 -- reserved
            NO_CHAR, NO_CHAR,  //0974 -- reserved
            NO_CHAR, NO_CHAR,  //0975 -- reserved
            NO_CHAR, NO_CHAR,  //0976 -- reserved
            NO_CHAR, NO_CHAR,  //0977 -- reserved
            NO_CHAR, NO_CHAR,  //0978 -- reserved
            NO_CHAR, NO_CHAR,  //0979 -- reserved
            NO_CHAR, NO_CHAR,  //097a -- reserved
            NO_CHAR, NO_CHAR,  //097b -- reserved
            NO_CHAR, NO_CHAR,  //097c -- reserved
            NO_CHAR, NO_CHAR,  //097d -- reserved
            NO_CHAR, NO_CHAR,  //097e -- reserved
            NO_CHAR, NO_CHAR //097f -- reserved
        ) //end of table definition
    }
}
