package com.fleeksoft.lang

actual fun Character.isSpaceChar(codePoint: Int): Boolean {
    return (((1 shl SPACE_SEPARATOR) or
            (1 shl LINE_SEPARATOR) or
            (1 shl PARAGRAPH_SEPARATOR)) shr Character.getType(codePoint)) and 1 != 0
}


actual fun Character.getType(codePoint: Int): Int = CharacterData.of(codePoint).getType(codePoint)