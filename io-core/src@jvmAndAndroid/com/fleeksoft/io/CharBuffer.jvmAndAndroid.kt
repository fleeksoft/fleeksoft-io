package com.fleeksoft.io

actual typealias CharBuffer = java.nio.CharBuffer

actual fun CharBuffer.duplicateExt(): CharBuffer = this.duplicate()
actual fun CharBuffer.getChar(index: Int): Char = this.get(index)