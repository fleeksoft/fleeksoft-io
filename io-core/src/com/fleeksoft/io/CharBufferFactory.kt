package com.fleeksoft.io

expect object CharBufferFactory {
    fun wrap(charArray: CharArray, off: Int = 0, len: Int = charArray.size): CharBuffer
    fun wrap(charSequence: CharSequence, off: Int = 0, len: Int = charSequence.length): CharBuffer
    fun allocate(capacity: Int): CharBuffer
}