package com.fleeksoft.io

actual object CharBufferFactory {
    actual fun wrap(charArray: CharArray, off: Int, len: Int): CharBuffer {
        return java.nio.CharBuffer.wrap(charArray, off, len)
    }

    actual fun wrap(charSequence: CharSequence, off: Int, len: Int): CharBuffer {
        return java.nio.CharBuffer.wrap(charSequence, off, len)
    }

    actual fun allocate(capacity: Int): CharBuffer {
        return java.nio.CharBuffer.allocate(capacity)
    }
}