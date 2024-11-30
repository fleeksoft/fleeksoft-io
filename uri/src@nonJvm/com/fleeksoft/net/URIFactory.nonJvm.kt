package com.fleeksoft.net

actual object URIFactory {
    actual fun create(str: String): URI = URI.create(str)
}