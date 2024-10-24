package com.fleeksoft.io.internal

internal object ObjHelper {
    fun checkFromIndexSize(fromIndex: Int, size: Int, length: Int) {
        if (fromIndex < 0 || size < 0 || fromIndex + size > length) {
            throw IndexOutOfBoundsException("Range [$fromIndex, $size) out of bounds for length $length")
        }
    }
}