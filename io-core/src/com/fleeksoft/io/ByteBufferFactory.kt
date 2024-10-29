package com.fleeksoft.io

expect object ByteBufferFactory {
    fun wrap(byteArray: ByteArray, off: Int = 0, len: Int = byteArray.size): ByteBuffer
    fun allocate(capacity: Int): ByteBuffer
}