package com.fleeksoft.io

object Constants {
    const val DEFAULT_CHAR_BUFFER_SIZE: Int = 8192
    const val DEFAULT_BYTE_BUFFER_SIZE: Int = 8192
    const val SEGMENT_SIZE: Long = 8192
    const val IS_DEFAULT_BYTE_BUFFER_SIZE: Int = 16384
    const val IS_MAX_SKIP_BUFFER_SIZE: Long = 2048
    const val MAX_BUFFER_SIZE = Int.MAX_VALUE - 8

    /** Maximum skip-buffer size  */
    const val maxSkipBufferSize: Int = 8192
    const val MAX_TRANSFER_SIZE = 128 * 1024
}