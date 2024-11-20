package com.fleeksoft.charset.internal

internal fun assert(value: Boolean, lazyMessage: (() -> String)? = null) {
//    java asserts ignored when flag -ea not passed
    /*if (!value) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    }*/
}