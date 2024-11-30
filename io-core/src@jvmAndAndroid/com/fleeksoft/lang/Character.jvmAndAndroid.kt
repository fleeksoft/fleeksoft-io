package com.fleeksoft.lang

actual fun Character.isSpaceChar(codePoint: Int): Boolean = java.lang.Character.isSpaceChar(codePoint)
actual fun Character.getType(codePoint: Int): Int = java.lang.Character.getType(codePoint)