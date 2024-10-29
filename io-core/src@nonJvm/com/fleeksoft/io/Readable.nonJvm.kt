package com.fleeksoft.io

actual interface Readable {
    actual fun read(cb: CharBuffer): Int
}