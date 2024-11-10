/*
 * Copyright (c) 2002, 2023, Oracle and/or its affiliates. All rights reserved.
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

object Character {
    const val MIN_SUPPLEMENTARY_CODE_POINT: Int = 0x010000
    const val MAX_CODE_POINT: Int = 0X10FFFF

    fun isSupplementaryCodePoint(codePoint: Int): Boolean {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint < MAX_CODE_POINT + 1
    }

    fun highSurrogate(codePoint: Int): Char {
        return ((codePoint ushr 10) + (Char.MIN_HIGH_SURROGATE.code - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10))).toChar()
    }

    fun lowSurrogate(codePoint: Int): Char {
        return ((codePoint and 0x3ff) + Char.MIN_LOW_SURROGATE.code).toChar()
    }

    fun isSurrogate(char: Char): Boolean = char.isSurrogate()

    fun isHighSurrogate(char: Char): Boolean = char.isHighSurrogate()
    fun isLowSurrogate(char: Char): Boolean = char.isLowSurrogate()

    fun toCodePoint(high: Char, low: Char): Int {
        /*return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT
        - (MIN_HIGH_SURROGATE << 10)
        - MIN_LOW_SURROGATE);*/
        return ((high.code shl 10) + low.code) + (MIN_SUPPLEMENTARY_CODE_POINT - (Char.MIN_HIGH_SURROGATE.code shl 10) - Char.MIN_LOW_SURROGATE.code)
    }

    fun isBmpCodePoint(codePoint: Int): Boolean {
//        return codePoint >>> 16 == 0;
        return codePoint ushr 16 == 0
    }

    fun isValidCodePoint(codePoint: Int): Boolean {
        /*int plane = codePoint >>> 16;
        return plane < ((MAX_CODE_POINT + 1) >>> 16);*/
        return (codePoint ushr 16) < ((MAX_CODE_POINT + 1) ushr 16)
    }

    fun compare(x: Char, y: Char): Int {
        return x - y
    }
}