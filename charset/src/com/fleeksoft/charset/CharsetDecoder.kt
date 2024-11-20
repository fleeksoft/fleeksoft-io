package com.fleeksoft.charset

import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer

expect abstract class CharsetDecoder {

    fun charset(): Charset

    fun averageCharsPerByte(): Float
    fun maxCharsPerByte(): Float

    fun malformedInputAction(): CodingErrorAction
    fun unmappableCharacterAction(): CodingErrorAction

    fun decode(byteBuffer: ByteBuffer, outCharBuffer: CharBuffer, endOfInput: Boolean): CoderResult

    fun decode(inByteBuffer: ByteBuffer): CharBuffer

    fun flush(out: CharBuffer): CoderResult


    fun onMalformedInput(newAction: CodingErrorAction): CharsetDecoder

    fun onUnmappableCharacter(newAction: CodingErrorAction): CharsetDecoder

    /**
     * Resets this decoder, clearing any internal state.
     *
     * This method resets charset-independent state and also invokes the
     * [implReset] method to perform any charset-specific reset actions.
     *
     * @return This decoder
     */
    fun reset(): CharsetDecoder
}
