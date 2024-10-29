package com.fleeksoft.io

actual object ByteBufferFactory {
    actual fun wrap(byteArray: ByteArray, off: Int, len: Int): ByteBuffer {
        return ByteBuffer.wrap(byteArray, off, len)
    }

    actual fun allocate(capacity: Int): ByteBuffer {
        return ByteBuffer.allocate(capacity)
    }
}