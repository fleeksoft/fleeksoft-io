package com.fleeksoft.io

actual object CharBufferFactory {
    actual fun wrap(charArray: CharArray, off: Int, len: Int): CharBuffer {
        return HeapCharBuffer(charArray = charArray, position = off, offset = 0, limit = off + len)
    }

    actual fun wrap(charSequence: CharSequence, off: Int, len: Int): CharBuffer {
        // FIXME: Create StringCharBuffer like jdk to avoid extra CharArray conversion
        return wrap(charSequence.toString().toCharArray(), off, len)
    }

    actual fun allocate(capacity: Int): CharBuffer {
        require(capacity >= 0) { "capacity must be >= 0" }
        return HeapCharBuffer(charArray = CharArray(capacity), position = 0, offset = 0, limit = capacity)
    }
}

fun test() {

}