package com.fleeksoft.charset

actual data class CodingErrorAction(val name: String) {
    companion object {
        val IGNORE = CodingErrorAction("IGNORE")
        val REPLACE = CodingErrorAction("REPLACE")
        val REPORT = CodingErrorAction("REPORT")
    }
}

actual object CodingErrorActionValue {
    actual val IGNORE: CodingErrorAction = CodingErrorAction.IGNORE
    actual val REPLACE: CodingErrorAction = CodingErrorAction.REPLACE
    actual val REPORT: CodingErrorAction = CodingErrorAction.REPORT
}