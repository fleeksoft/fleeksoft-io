package com.fleeksoft.lang

abstract class CharacterData {

    abstract fun getProperties(ch: Int): Int
    abstract fun getType(ch: Int): Int
    abstract fun isDigit(ch: Int): Boolean
    abstract fun isLowerCase(ch: Int): Boolean
    abstract fun isUpperCase(ch: Int): Boolean
    abstract fun isWhitespace(ch: Int): Boolean
    abstract fun isMirrored(ch: Int): Boolean
    abstract fun isJavaIdentifierStart(ch: Int): Boolean
    abstract fun isJavaIdentifierPart(ch: Int): Boolean
    abstract fun isUnicodeIdentifierStart(ch: Int): Boolean
    abstract fun isUnicodeIdentifierPart(ch: Int): Boolean
    abstract fun isIdentifierIgnorable(ch: Int): Boolean
    abstract fun isEmoji(ch: Int): Boolean
    abstract fun isEmojiPresentation(ch: Int): Boolean
    abstract fun isEmojiModifier(ch: Int): Boolean
    abstract fun isEmojiModifierBase(ch: Int): Boolean
    abstract fun isEmojiComponent(ch: Int): Boolean
    abstract fun isExtendedPictographic(ch: Int): Boolean
    abstract fun toLowerCase(ch: Int): Int
    abstract fun toUpperCase(ch: Int): Int
    abstract fun toTitleCase(ch: Int): Int
    abstract fun digit(ch: Int, radix: Int): Int
    abstract fun getNumericValue(ch: Int): Int
    abstract fun getDirectionality(ch: Int): Byte

    // Default implementation for JSR204
    open fun toUpperCaseEx(ch: Int): Int {
        return toUpperCase(ch)
    }

    open fun toUpperCaseCharArray(ch: Int): CharArray? {
        return null
    }

    open fun isOtherAlphabetic(ch: Int): Boolean {
        return false
    }

    open fun isIdeographic(ch: Int): Boolean {
        return false
    }


    companion object {
        fun of(ch: Int): CharacterData {
            return if (ch ushr 8 == 0) { // fast-path
                CharacterDataLatin1.instance
            } else {
                when (ch ushr 16) { // plane 00-16
                    0 -> CharacterData00.instance
                    1 -> CharacterData01.instance
                    2 -> CharacterData02.instance
                    3 -> CharacterData03.instance
                    14 -> CharacterData0E.instance
                    15, 16 -> CharacterDataPrivateUse.instance // Both cases Private Use
                    else -> CharacterDataUndefined.instance
                }
            }
        }
    }
}