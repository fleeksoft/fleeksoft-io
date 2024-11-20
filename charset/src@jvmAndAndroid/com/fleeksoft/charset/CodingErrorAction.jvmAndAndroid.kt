package com.fleeksoft.charset

actual typealias CodingErrorAction = java.nio.charset.CodingErrorAction

actual object CodingErrorActionValue {
    actual val IGNORE: CodingErrorAction = java.nio.charset.CodingErrorAction.IGNORE
    actual val REPLACE: CodingErrorAction = java.nio.charset.CodingErrorAction.REPLACE
    actual val REPORT: CodingErrorAction = java.nio.charset.CodingErrorAction.REPORT
}