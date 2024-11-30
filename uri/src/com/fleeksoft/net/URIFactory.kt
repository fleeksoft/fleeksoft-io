package com.fleeksoft.net

expect object URIFactory {
    fun create(str: String): URI
}