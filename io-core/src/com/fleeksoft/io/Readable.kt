package com.fleeksoft.io

expect interface Readable {
    fun read(cb: CharBuffer): Int
}