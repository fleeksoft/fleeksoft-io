/*
 * Copyright (c) 2003, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.fleeksoft.lang

/** The CharacterData class encapsulates the large tables found in
 * Java.lang.Character.  */
internal class CharacterDataUndefined private constructor() : CharacterData() {
    override fun getProperties(ch: Int): Int {
        return 0
    }

    override fun getType(ch: Int): Int {
        return Character.UNASSIGNED.toInt()
    }

    override fun isJavaIdentifierStart(ch: Int): Boolean {
        return false
    }

    override fun isJavaIdentifierPart(ch: Int): Boolean {
        return false
    }

    override fun isUnicodeIdentifierStart(ch: Int): Boolean {
        return false
    }

    override fun isUnicodeIdentifierPart(ch: Int): Boolean {
        return false
    }

    override fun isIdentifierIgnorable(ch: Int): Boolean {
        return false
    }

    override fun isEmoji(ch: Int): Boolean {
        return false
    }

    override fun isEmojiPresentation(ch: Int): Boolean {
        return false
    }

    override fun isEmojiModifier(ch: Int): Boolean {
        return false
    }

    override fun isEmojiModifierBase(ch: Int): Boolean {
        return false
    }

    override fun isEmojiComponent(ch: Int): Boolean {
        return false
    }

    override fun isExtendedPictographic(ch: Int): Boolean {
        return false
    }

    override fun toLowerCase(ch: Int): Int {
        return ch
    }

    override fun toUpperCase(ch: Int): Int {
        return ch
    }

    override fun toTitleCase(ch: Int): Int {
        return ch
    }

    override fun digit(ch: Int, radix: Int): Int {
        return -1
    }

    override fun getNumericValue(ch: Int): Int {
        return -1
    }

    override fun isDigit(ch: Int): Boolean {
        return false
    }

    override fun isLowerCase(ch: Int): Boolean {
        return false
    }

    override fun isUpperCase(ch: Int): Boolean {
        return false
    }

    override fun isWhitespace(ch: Int): Boolean {
        return false
    }

    override fun getDirectionality(ch: Int): Byte {
        return Character.DIRECTIONALITY_UNDEFINED.toByte()
    }

    override fun isMirrored(ch: Int): Boolean {
        return false
    }

    companion object {
        val instance: CharacterData = CharacterDataUndefined()
    }
}
