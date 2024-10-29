package com.fleeksoft.io

expect open class FilterInputStream protected constructor(input: InputStream?) : InputStream {
    override fun read(): Int
}