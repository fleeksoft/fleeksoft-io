package com.fleeksoft.io

actual object ByteBufferFactory {
    actual fun wrap(byteArray: ByteArray, off: Int, len: Int): ByteBuffer {
        return HeapByteBuffer(
            byteArray = byteArray,
            mark = -1,
            position = off,
            limit = off + len,
            offset = 0
        )
    }

    actual fun allocate(capacity: Int): ByteBuffer {
        return HeapByteBuffer(
            byteArray = ByteArray(capacity),
            mark = -1,
            position = 0,
            limit = capacity,
            offset = 0
        )
    }
}