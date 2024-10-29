package com.fleeksoft.io.internal

internal object ObjHelper {
    fun checkFromIndexSize(fromIndex: Int, size: Int, length: Int) {
        if ((length or fromIndex or size) < 0 || (size > length - fromIndex)) {
            throw IndexOutOfBoundsException("Range [$fromIndex, $size) out of bounds for length $length")
        }
    }
}