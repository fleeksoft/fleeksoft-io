package com.fleeksoft.charset

expect class CodingErrorAction

expect object CodingErrorActionValue {
    val IGNORE: CodingErrorAction
    val REPLACE: CodingErrorAction
    val REPORT: CodingErrorAction
}