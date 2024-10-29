package com.fleeksoft.io.internal

fun assert(value: Boolean, lazyMessage: (() -> String)? = null) {
//    java asserts ignored when flag -ea not passed
    /*if (!value) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    }*/
}

fun assert(condition: Boolean, error: String?) {
    if (!condition) {
        throw Exception(error ?: "Assert error!")
    }
}