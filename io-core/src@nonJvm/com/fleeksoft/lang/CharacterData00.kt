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

import com.fleeksoft.io.internal.assert

internal class CharacterData00 private constructor() : CharacterData() {
    override fun getProperties(ch: Int): Int {
        val offset = ch.toChar()
        val props = A!![Y!![X[offset.code shr 5].code or ((offset.code shr 1) and 0xF)].code or (offset.code and 0x1)]
        return props
    }

    fun getPropertiesEx(ch: Int): Int {
        val offset = ch.toChar()
        val props = B!![Y!![X[offset.code shr 5].code or ((offset.code shr 1) and 0xF)].code or (offset.code and 0x1)].code
        return props
    }

    override fun getType(ch: Int): Int {
        val props = getProperties(ch)
        return (props and 0x1F)
    }

    override fun isOtherAlphabetic(ch: Int): Boolean {
        val props = getPropertiesEx(ch)
        return (props and 0x0004) != 0
    }

    override fun isIdeographic(ch: Int): Boolean {
        val props = getPropertiesEx(ch)
        return (props and 0x0008) != 0
    }

    override fun isJavaIdentifierStart(ch: Int): Boolean {
        val props = getProperties(ch)
        return ((props and 0x00007000) >= 0x00005000)
    }

    override fun isJavaIdentifierPart(ch: Int): Boolean {
        val props = getProperties(ch)
        return ((props and 0x00003000) != 0)
    }

    override fun isUnicodeIdentifierStart(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0010) != 0 ||
                ch == 0x2E2F
    }

    override fun isUnicodeIdentifierPart(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0020) != 0 ||
                isIdentifierIgnorable(ch) || ch == 0x2E2F
    }

    override fun isIdentifierIgnorable(ch: Int): Boolean {
        val props = getProperties(ch)
        return ((props and 0x00007000) == 0x00001000)
    }

    override fun isEmoji(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0040) != 0
    }

    override fun isEmojiPresentation(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0080) != 0
    }

    override fun isEmojiModifier(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0100) != 0
    }

    override fun isEmojiModifierBase(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0200) != 0
    }

    override fun isEmojiComponent(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0400) != 0
    }

    override fun isExtendedPictographic(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0800) != 0
    }

    override fun toLowerCase(ch: Int): Int {
        var mapChar = ch
        val `val` = getProperties(ch)

        if ((`val` and 0x00020000) != 0) {
            if ((`val` and 0x07FC0000) == 0x07FC0000) {
                when (ch) {
                    0x0130 -> mapChar = 0x0069
                    0x023A -> mapChar = 0x2C65
                    0x023E -> mapChar = 0x2C66
                    0x0412 -> mapChar = 0x1C80
                    0x0414 -> mapChar = 0x1C81
                    0x041E -> mapChar = 0x1C82
                    0x0421 -> mapChar = 0x1C83
                    0x042A -> mapChar = 0x1C86
                    0x0462 -> mapChar = 0x1C87
                    0x10A0 -> mapChar = 0x2D00
                    0x10A1 -> mapChar = 0x2D01
                    0x10A2 -> mapChar = 0x2D02
                    0x10A3 -> mapChar = 0x2D03
                    0x10A4 -> mapChar = 0x2D04
                    0x10A5 -> mapChar = 0x2D05
                    0x10A6 -> mapChar = 0x2D06
                    0x10A7 -> mapChar = 0x2D07
                    0x10A8 -> mapChar = 0x2D08
                    0x10A9 -> mapChar = 0x2D09
                    0x10AA -> mapChar = 0x2D0A
                    0x10AB -> mapChar = 0x2D0B
                    0x10AC -> mapChar = 0x2D0C
                    0x10AD -> mapChar = 0x2D0D
                    0x10AE -> mapChar = 0x2D0E
                    0x10AF -> mapChar = 0x2D0F
                    0x10B0 -> mapChar = 0x2D10
                    0x10B1 -> mapChar = 0x2D11
                    0x10B2 -> mapChar = 0x2D12
                    0x10B3 -> mapChar = 0x2D13
                    0x10B4 -> mapChar = 0x2D14
                    0x10B5 -> mapChar = 0x2D15
                    0x10B6 -> mapChar = 0x2D16
                    0x10B7 -> mapChar = 0x2D17
                    0x10B8 -> mapChar = 0x2D18
                    0x10B9 -> mapChar = 0x2D19
                    0x10BA -> mapChar = 0x2D1A
                    0x10BB -> mapChar = 0x2D1B
                    0x10BC -> mapChar = 0x2D1C
                    0x10BD -> mapChar = 0x2D1D
                    0x10BE -> mapChar = 0x2D1E
                    0x10BF -> mapChar = 0x2D1F
                    0x10C0 -> mapChar = 0x2D20
                    0x10C1 -> mapChar = 0x2D21
                    0x10C2 -> mapChar = 0x2D22
                    0x10C3 -> mapChar = 0x2D23
                    0x10C4 -> mapChar = 0x2D24
                    0x10C5 -> mapChar = 0x2D25
                    0x10C7 -> mapChar = 0x2D27
                    0x10CD -> mapChar = 0x2D2D
                    0x13A0 -> mapChar = 0xAB70
                    0x13A1 -> mapChar = 0xAB71
                    0x13A2 -> mapChar = 0xAB72
                    0x13A3 -> mapChar = 0xAB73
                    0x13A4 -> mapChar = 0xAB74
                    0x13A5 -> mapChar = 0xAB75
                    0x13A6 -> mapChar = 0xAB76
                    0x13A7 -> mapChar = 0xAB77
                    0x13A8 -> mapChar = 0xAB78
                    0x13A9 -> mapChar = 0xAB79
                    0x13AA -> mapChar = 0xAB7A
                    0x13AB -> mapChar = 0xAB7B
                    0x13AC -> mapChar = 0xAB7C
                    0x13AD -> mapChar = 0xAB7D
                    0x13AE -> mapChar = 0xAB7E
                    0x13AF -> mapChar = 0xAB7F
                    0x13B0 -> mapChar = 0xAB80
                    0x13B1 -> mapChar = 0xAB81
                    0x13B2 -> mapChar = 0xAB82
                    0x13B3 -> mapChar = 0xAB83
                    0x13B4 -> mapChar = 0xAB84
                    0x13B5 -> mapChar = 0xAB85
                    0x13B6 -> mapChar = 0xAB86
                    0x13B7 -> mapChar = 0xAB87
                    0x13B8 -> mapChar = 0xAB88
                    0x13B9 -> mapChar = 0xAB89
                    0x13BA -> mapChar = 0xAB8A
                    0x13BB -> mapChar = 0xAB8B
                    0x13BC -> mapChar = 0xAB8C
                    0x13BD -> mapChar = 0xAB8D
                    0x13BE -> mapChar = 0xAB8E
                    0x13BF -> mapChar = 0xAB8F
                    0x13C0 -> mapChar = 0xAB90
                    0x13C1 -> mapChar = 0xAB91
                    0x13C2 -> mapChar = 0xAB92
                    0x13C3 -> mapChar = 0xAB93
                    0x13C4 -> mapChar = 0xAB94
                    0x13C5 -> mapChar = 0xAB95
                    0x13C6 -> mapChar = 0xAB96
                    0x13C7 -> mapChar = 0xAB97
                    0x13C8 -> mapChar = 0xAB98
                    0x13C9 -> mapChar = 0xAB99
                    0x13CA -> mapChar = 0xAB9A
                    0x13CB -> mapChar = 0xAB9B
                    0x13CC -> mapChar = 0xAB9C
                    0x13CD -> mapChar = 0xAB9D
                    0x13CE -> mapChar = 0xAB9E
                    0x13CF -> mapChar = 0xAB9F
                    0x13D0 -> mapChar = 0xABA0
                    0x13D1 -> mapChar = 0xABA1
                    0x13D2 -> mapChar = 0xABA2
                    0x13D3 -> mapChar = 0xABA3
                    0x13D4 -> mapChar = 0xABA4
                    0x13D5 -> mapChar = 0xABA5
                    0x13D6 -> mapChar = 0xABA6
                    0x13D7 -> mapChar = 0xABA7
                    0x13D8 -> mapChar = 0xABA8
                    0x13D9 -> mapChar = 0xABA9
                    0x13DA -> mapChar = 0xABAA
                    0x13DB -> mapChar = 0xABAB
                    0x13DC -> mapChar = 0xABAC
                    0x13DD -> mapChar = 0xABAD
                    0x13DE -> mapChar = 0xABAE
                    0x13DF -> mapChar = 0xABAF
                    0x13E0 -> mapChar = 0xABB0
                    0x13E1 -> mapChar = 0xABB1
                    0x13E2 -> mapChar = 0xABB2
                    0x13E3 -> mapChar = 0xABB3
                    0x13E4 -> mapChar = 0xABB4
                    0x13E5 -> mapChar = 0xABB5
                    0x13E6 -> mapChar = 0xABB6
                    0x13E7 -> mapChar = 0xABB7
                    0x13E8 -> mapChar = 0xABB8
                    0x13E9 -> mapChar = 0xABB9
                    0x13EA -> mapChar = 0xABBA
                    0x13EB -> mapChar = 0xABBB
                    0x13EC -> mapChar = 0xABBC
                    0x13ED -> mapChar = 0xABBD
                    0x13EE -> mapChar = 0xABBE
                    0x13EF -> mapChar = 0xABBF
                    0x1C90 -> mapChar = 0x10D0
                    0x1C91 -> mapChar = 0x10D1
                    0x1C92 -> mapChar = 0x10D2
                    0x1C93 -> mapChar = 0x10D3
                    0x1C94 -> mapChar = 0x10D4
                    0x1C95 -> mapChar = 0x10D5
                    0x1C96 -> mapChar = 0x10D6
                    0x1C97 -> mapChar = 0x10D7
                    0x1C98 -> mapChar = 0x10D8
                    0x1C99 -> mapChar = 0x10D9
                    0x1C9A -> mapChar = 0x10DA
                    0x1C9B -> mapChar = 0x10DB
                    0x1C9C -> mapChar = 0x10DC
                    0x1C9D -> mapChar = 0x10DD
                    0x1C9E -> mapChar = 0x10DE
                    0x1C9F -> mapChar = 0x10DF
                    0x1CA0 -> mapChar = 0x10E0
                    0x1CA1 -> mapChar = 0x10E1
                    0x1CA2 -> mapChar = 0x10E2
                    0x1CA3 -> mapChar = 0x10E3
                    0x1CA4 -> mapChar = 0x10E4
                    0x1CA5 -> mapChar = 0x10E5
                    0x1CA6 -> mapChar = 0x10E6
                    0x1CA7 -> mapChar = 0x10E7
                    0x1CA8 -> mapChar = 0x10E8
                    0x1CA9 -> mapChar = 0x10E9
                    0x1CAA -> mapChar = 0x10EA
                    0x1CAB -> mapChar = 0x10EB
                    0x1CAC -> mapChar = 0x10EC
                    0x1CAD -> mapChar = 0x10ED
                    0x1CAE -> mapChar = 0x10EE
                    0x1CAF -> mapChar = 0x10EF
                    0x1CB0 -> mapChar = 0x10F0
                    0x1CB1 -> mapChar = 0x10F1
                    0x1CB2 -> mapChar = 0x10F2
                    0x1CB3 -> mapChar = 0x10F3
                    0x1CB4 -> mapChar = 0x10F4
                    0x1CB5 -> mapChar = 0x10F5
                    0x1CB6 -> mapChar = 0x10F6
                    0x1CB7 -> mapChar = 0x10F7
                    0x1CB8 -> mapChar = 0x10F8
                    0x1CB9 -> mapChar = 0x10F9
                    0x1CBA -> mapChar = 0x10FA
                    0x1CBB -> mapChar = 0x10FB
                    0x1CBC -> mapChar = 0x10FC
                    0x1CBD -> mapChar = 0x10FD
                    0x1CBE -> mapChar = 0x10FE
                    0x1CBF -> mapChar = 0x10FF
                    0x1E9E -> mapChar = 0x00DF
                    0x1F88 -> mapChar = 0x1F80
                    0x1F89 -> mapChar = 0x1F81
                    0x1F8A -> mapChar = 0x1F82
                    0x1F8B -> mapChar = 0x1F83
                    0x1F8C -> mapChar = 0x1F84
                    0x1F8D -> mapChar = 0x1F85
                    0x1F8E -> mapChar = 0x1F86
                    0x1F8F -> mapChar = 0x1F87
                    0x1F98 -> mapChar = 0x1F90
                    0x1F99 -> mapChar = 0x1F91
                    0x1F9A -> mapChar = 0x1F92
                    0x1F9B -> mapChar = 0x1F93
                    0x1F9C -> mapChar = 0x1F94
                    0x1F9D -> mapChar = 0x1F95
                    0x1F9E -> mapChar = 0x1F96
                    0x1F9F -> mapChar = 0x1F97
                    0x1FA8 -> mapChar = 0x1FA0
                    0x1FA9 -> mapChar = 0x1FA1
                    0x1FAA -> mapChar = 0x1FA2
                    0x1FAB -> mapChar = 0x1FA3
                    0x1FAC -> mapChar = 0x1FA4
                    0x1FAD -> mapChar = 0x1FA5
                    0x1FAE -> mapChar = 0x1FA6
                    0x1FAF -> mapChar = 0x1FA7
                    0x1FBC -> mapChar = 0x1FB3
                    0x1FCC -> mapChar = 0x1FC3
                    0x1FFC -> mapChar = 0x1FF3
                    0x2126 -> mapChar = 0x03C9
                    0x212A -> mapChar = 0x006B
                    0x212B -> mapChar = 0x00E5
                    0x2C62 -> mapChar = 0x026B
                    0x2C63 -> mapChar = 0x1D7D
                    0x2C64 -> mapChar = 0x027D
                    0x2C6D -> mapChar = 0x0251
                    0x2C6E -> mapChar = 0x0271
                    0x2C6F -> mapChar = 0x0250
                    0x2C70 -> mapChar = 0x0252
                    0x2C7E -> mapChar = 0x023F
                    0x2C7F -> mapChar = 0x0240
                    0xA64A -> mapChar = 0x1C88
                    0xA77D -> mapChar = 0x1D79
                    0xA78D -> mapChar = 0x0265
                    0xA7AA -> mapChar = 0x0266
                    0xA7AB -> mapChar = 0x025C
                    0xA7AC -> mapChar = 0x0261
                    0xA7AD -> mapChar = 0x026C
                    0xA7AE -> mapChar = 0x026A
                    0xA7B0 -> mapChar = 0x029E
                    0xA7B1 -> mapChar = 0x0287
                    0xA7B2 -> mapChar = 0x029D
                    0xA7B3 -> mapChar = 0xAB53
                    0xA7C5 -> mapChar = 0x0282
                    0xA7C6 -> mapChar = 0x1D8E
                }
            } else {
                val offset = `val` shl 5 shr (5 + 18)
                mapChar = ch + offset
            }
        }
        return mapChar
    }

    override fun toUpperCase(ch: Int): Int {
        var mapChar = ch
        val `val` = getProperties(ch)

        if ((`val` and 0x00010000) != 0) {
            if ((`val` and 0x07FC0000) == 0x07FC0000) {
                when (ch) {
                    0x017F -> mapChar = 0x0053
                    0x023F -> mapChar = 0x2C7E
                    0x0240 -> mapChar = 0x2C7F
                    0x0250 -> mapChar = 0x2C6F
                    0x0251 -> mapChar = 0x2C6D
                    0x0252 -> mapChar = 0x2C70
                    0x025C -> mapChar = 0xA7AB
                    0x0261 -> mapChar = 0xA7AC
                    0x0265 -> mapChar = 0xA78D
                    0x0266 -> mapChar = 0xA7AA
                    0x026A -> mapChar = 0xA7AE
                    0x026B -> mapChar = 0x2C62
                    0x026C -> mapChar = 0xA7AD
                    0x0271 -> mapChar = 0x2C6E
                    0x027D -> mapChar = 0x2C64
                    0x0282 -> mapChar = 0xA7C5
                    0x0287 -> mapChar = 0xA7B1
                    0x029D -> mapChar = 0xA7B2
                    0x029E -> mapChar = 0xA7B0
                    0x10D0 -> mapChar = 0x1C90
                    0x10D1 -> mapChar = 0x1C91
                    0x10D2 -> mapChar = 0x1C92
                    0x10D3 -> mapChar = 0x1C93
                    0x10D4 -> mapChar = 0x1C94
                    0x10D5 -> mapChar = 0x1C95
                    0x10D6 -> mapChar = 0x1C96
                    0x10D7 -> mapChar = 0x1C97
                    0x10D8 -> mapChar = 0x1C98
                    0x10D9 -> mapChar = 0x1C99
                    0x10DA -> mapChar = 0x1C9A
                    0x10DB -> mapChar = 0x1C9B
                    0x10DC -> mapChar = 0x1C9C
                    0x10DD -> mapChar = 0x1C9D
                    0x10DE -> mapChar = 0x1C9E
                    0x10DF -> mapChar = 0x1C9F
                    0x10E0 -> mapChar = 0x1CA0
                    0x10E1 -> mapChar = 0x1CA1
                    0x10E2 -> mapChar = 0x1CA2
                    0x10E3 -> mapChar = 0x1CA3
                    0x10E4 -> mapChar = 0x1CA4
                    0x10E5 -> mapChar = 0x1CA5
                    0x10E6 -> mapChar = 0x1CA6
                    0x10E7 -> mapChar = 0x1CA7
                    0x10E8 -> mapChar = 0x1CA8
                    0x10E9 -> mapChar = 0x1CA9
                    0x10EA -> mapChar = 0x1CAA
                    0x10EB -> mapChar = 0x1CAB
                    0x10EC -> mapChar = 0x1CAC
                    0x10ED -> mapChar = 0x1CAD
                    0x10EE -> mapChar = 0x1CAE
                    0x10EF -> mapChar = 0x1CAF
                    0x10F0 -> mapChar = 0x1CB0
                    0x10F1 -> mapChar = 0x1CB1
                    0x10F2 -> mapChar = 0x1CB2
                    0x10F3 -> mapChar = 0x1CB3
                    0x10F4 -> mapChar = 0x1CB4
                    0x10F5 -> mapChar = 0x1CB5
                    0x10F6 -> mapChar = 0x1CB6
                    0x10F7 -> mapChar = 0x1CB7
                    0x10F8 -> mapChar = 0x1CB8
                    0x10F9 -> mapChar = 0x1CB9
                    0x10FA -> mapChar = 0x1CBA
                    0x10FD -> mapChar = 0x1CBD
                    0x10FE -> mapChar = 0x1CBE
                    0x10FF -> mapChar = 0x1CBF
                    0x1C80 -> mapChar = 0x0412
                    0x1C81 -> mapChar = 0x0414
                    0x1C82 -> mapChar = 0x041E
                    0x1C83 -> mapChar = 0x0421
                    0x1C84 -> mapChar = 0x0422
                    0x1C85 -> mapChar = 0x0422
                    0x1C86 -> mapChar = 0x042A
                    0x1C87 -> mapChar = 0x0462
                    0x1C88 -> mapChar = 0xA64A
                    0x1D79 -> mapChar = 0xA77D
                    0x1D7D -> mapChar = 0x2C63
                    0x1D8E -> mapChar = 0xA7C6
                    0x1F80 -> mapChar = 0x1F88
                    0x1F81 -> mapChar = 0x1F89
                    0x1F82 -> mapChar = 0x1F8A
                    0x1F83 -> mapChar = 0x1F8B
                    0x1F84 -> mapChar = 0x1F8C
                    0x1F85 -> mapChar = 0x1F8D
                    0x1F86 -> mapChar = 0x1F8E
                    0x1F87 -> mapChar = 0x1F8F
                    0x1F90 -> mapChar = 0x1F98
                    0x1F91 -> mapChar = 0x1F99
                    0x1F92 -> mapChar = 0x1F9A
                    0x1F93 -> mapChar = 0x1F9B
                    0x1F94 -> mapChar = 0x1F9C
                    0x1F95 -> mapChar = 0x1F9D
                    0x1F96 -> mapChar = 0x1F9E
                    0x1F97 -> mapChar = 0x1F9F
                    0x1FA0 -> mapChar = 0x1FA8
                    0x1FA1 -> mapChar = 0x1FA9
                    0x1FA2 -> mapChar = 0x1FAA
                    0x1FA3 -> mapChar = 0x1FAB
                    0x1FA4 -> mapChar = 0x1FAC
                    0x1FA5 -> mapChar = 0x1FAD
                    0x1FA6 -> mapChar = 0x1FAE
                    0x1FA7 -> mapChar = 0x1FAF
                    0x1FB3 -> mapChar = 0x1FBC
                    0x1FBE -> mapChar = 0x0399
                    0x1FC3 -> mapChar = 0x1FCC
                    0x1FF3 -> mapChar = 0x1FFC
                    0x2C65 -> mapChar = 0x023A
                    0x2C66 -> mapChar = 0x023E
                    0x2D00 -> mapChar = 0x10A0
                    0x2D01 -> mapChar = 0x10A1
                    0x2D02 -> mapChar = 0x10A2
                    0x2D03 -> mapChar = 0x10A3
                    0x2D04 -> mapChar = 0x10A4
                    0x2D05 -> mapChar = 0x10A5
                    0x2D06 -> mapChar = 0x10A6
                    0x2D07 -> mapChar = 0x10A7
                    0x2D08 -> mapChar = 0x10A8
                    0x2D09 -> mapChar = 0x10A9
                    0x2D0A -> mapChar = 0x10AA
                    0x2D0B -> mapChar = 0x10AB
                    0x2D0C -> mapChar = 0x10AC
                    0x2D0D -> mapChar = 0x10AD
                    0x2D0E -> mapChar = 0x10AE
                    0x2D0F -> mapChar = 0x10AF
                    0x2D10 -> mapChar = 0x10B0
                    0x2D11 -> mapChar = 0x10B1
                    0x2D12 -> mapChar = 0x10B2
                    0x2D13 -> mapChar = 0x10B3
                    0x2D14 -> mapChar = 0x10B4
                    0x2D15 -> mapChar = 0x10B5
                    0x2D16 -> mapChar = 0x10B6
                    0x2D17 -> mapChar = 0x10B7
                    0x2D18 -> mapChar = 0x10B8
                    0x2D19 -> mapChar = 0x10B9
                    0x2D1A -> mapChar = 0x10BA
                    0x2D1B -> mapChar = 0x10BB
                    0x2D1C -> mapChar = 0x10BC
                    0x2D1D -> mapChar = 0x10BD
                    0x2D1E -> mapChar = 0x10BE
                    0x2D1F -> mapChar = 0x10BF
                    0x2D20 -> mapChar = 0x10C0
                    0x2D21 -> mapChar = 0x10C1
                    0x2D22 -> mapChar = 0x10C2
                    0x2D23 -> mapChar = 0x10C3
                    0x2D24 -> mapChar = 0x10C4
                    0x2D25 -> mapChar = 0x10C5
                    0x2D27 -> mapChar = 0x10C7
                    0x2D2D -> mapChar = 0x10CD
                    0xAB53 -> mapChar = 0xA7B3
                    0xAB70 -> mapChar = 0x13A0
                    0xAB71 -> mapChar = 0x13A1
                    0xAB72 -> mapChar = 0x13A2
                    0xAB73 -> mapChar = 0x13A3
                    0xAB74 -> mapChar = 0x13A4
                    0xAB75 -> mapChar = 0x13A5
                    0xAB76 -> mapChar = 0x13A6
                    0xAB77 -> mapChar = 0x13A7
                    0xAB78 -> mapChar = 0x13A8
                    0xAB79 -> mapChar = 0x13A9
                    0xAB7A -> mapChar = 0x13AA
                    0xAB7B -> mapChar = 0x13AB
                    0xAB7C -> mapChar = 0x13AC
                    0xAB7D -> mapChar = 0x13AD
                    0xAB7E -> mapChar = 0x13AE
                    0xAB7F -> mapChar = 0x13AF
                    0xAB80 -> mapChar = 0x13B0
                    0xAB81 -> mapChar = 0x13B1
                    0xAB82 -> mapChar = 0x13B2
                    0xAB83 -> mapChar = 0x13B3
                    0xAB84 -> mapChar = 0x13B4
                    0xAB85 -> mapChar = 0x13B5
                    0xAB86 -> mapChar = 0x13B6
                    0xAB87 -> mapChar = 0x13B7
                    0xAB88 -> mapChar = 0x13B8
                    0xAB89 -> mapChar = 0x13B9
                    0xAB8A -> mapChar = 0x13BA
                    0xAB8B -> mapChar = 0x13BB
                    0xAB8C -> mapChar = 0x13BC
                    0xAB8D -> mapChar = 0x13BD
                    0xAB8E -> mapChar = 0x13BE
                    0xAB8F -> mapChar = 0x13BF
                    0xAB90 -> mapChar = 0x13C0
                    0xAB91 -> mapChar = 0x13C1
                    0xAB92 -> mapChar = 0x13C2
                    0xAB93 -> mapChar = 0x13C3
                    0xAB94 -> mapChar = 0x13C4
                    0xAB95 -> mapChar = 0x13C5
                    0xAB96 -> mapChar = 0x13C6
                    0xAB97 -> mapChar = 0x13C7
                    0xAB98 -> mapChar = 0x13C8
                    0xAB99 -> mapChar = 0x13C9
                    0xAB9A -> mapChar = 0x13CA
                    0xAB9B -> mapChar = 0x13CB
                    0xAB9C -> mapChar = 0x13CC
                    0xAB9D -> mapChar = 0x13CD
                    0xAB9E -> mapChar = 0x13CE
                    0xAB9F -> mapChar = 0x13CF
                    0xABA0 -> mapChar = 0x13D0
                    0xABA1 -> mapChar = 0x13D1
                    0xABA2 -> mapChar = 0x13D2
                    0xABA3 -> mapChar = 0x13D3
                    0xABA4 -> mapChar = 0x13D4
                    0xABA5 -> mapChar = 0x13D5
                    0xABA6 -> mapChar = 0x13D6
                    0xABA7 -> mapChar = 0x13D7
                    0xABA8 -> mapChar = 0x13D8
                    0xABA9 -> mapChar = 0x13D9
                    0xABAA -> mapChar = 0x13DA
                    0xABAB -> mapChar = 0x13DB
                    0xABAC -> mapChar = 0x13DC
                    0xABAD -> mapChar = 0x13DD
                    0xABAE -> mapChar = 0x13DE
                    0xABAF -> mapChar = 0x13DF
                    0xABB0 -> mapChar = 0x13E0
                    0xABB1 -> mapChar = 0x13E1
                    0xABB2 -> mapChar = 0x13E2
                    0xABB3 -> mapChar = 0x13E3
                    0xABB4 -> mapChar = 0x13E4
                    0xABB5 -> mapChar = 0x13E5
                    0xABB6 -> mapChar = 0x13E6
                    0xABB7 -> mapChar = 0x13E7
                    0xABB8 -> mapChar = 0x13E8
                    0xABB9 -> mapChar = 0x13E9
                    0xABBA -> mapChar = 0x13EA
                    0xABBB -> mapChar = 0x13EB
                    0xABBC -> mapChar = 0x13EC
                    0xABBD -> mapChar = 0x13ED
                    0xABBE -> mapChar = 0x13EE
                    0xABBF -> mapChar = 0x13EF
                }
            } else {
                val offset = `val` shl 5 shr (5 + 18)
                mapChar = ch - offset
            }
        }
        return mapChar
    }

    override fun toTitleCase(ch: Int): Int {
        var mapChar = ch
        val `val` = getProperties(ch)

        if ((`val` and 0x00008000) != 0) {
            // There is a titlecase equivalent.  Perform further checks:
            if ((`val` and 0x00010000) == 0) {
                // The character does not have an uppercase equivalent, so it must
                // already be uppercase; so add 1 to get the titlecase form.
                mapChar = ch + 1
            } else if ((`val` and 0x00020000) == 0) {
                // For some Georgian letters, titlecase form is
                // same as this character.
                mapChar = if (ch >= 0x10D0 && ch <= 0x10FF) {
                    ch
                } else {
                    // The character does not have a lowercase equivalent, so it must
                    // already be lowercase; so subtract 1 to get the titlecase form.
                    ch - 1
                }
            }
            // else {
            // The character has both an uppercase equivalent and a lowercase
            // equivalent, so it must itself be a titlecase form; return it.
            // return ch;
            //}
        } else if ((`val` and 0x00010000) != 0) {
            // This character has no titlecase equivalent but it does have an
            // uppercase equivalent, so use that (subtract the signed case offset).
            mapChar = toUpperCase(ch)
        }
        return mapChar
    }

    override fun digit(ch: Int, radix: Int): Int {
        var value = -1
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
            val `val` = getProperties(ch)
            val kind = `val` and 0x1F
            if (kind == Character.DECIMAL_DIGIT_NUMBER) {
                value = ch + ((`val` and 0x3E0) shr 5) and 0x1F
            } else if ((`val` and 0xC00) == 0x00000C00) {
                // Java supradecimal digit
                value = (ch + ((`val` and 0x3E0) shr 5) and 0x1F) + 10
            }
        }
        return if (value < radix) value else -1
    }

    override fun getNumericValue(ch: Int): Int {
        val `val` = getProperties(ch)
        var retval = -1

        when (`val` and 0xC00) {
            (0x00000000) -> retval = -1
            (0x00000400) -> retval = ch + ((`val` and 0x3E0) shr 5) and 0x1F
            (0x00000800) -> when (ch) {
                0x0BF1 -> retval = 100
                0x0BF2 -> retval = 1000
                0x0D71 -> retval = 100
                0x0D72 -> retval = 1000
                0x1375 -> retval = 40
                0x1376 -> retval = 50
                0x1377 -> retval = 60
                0x1378 -> retval = 70
                0x1379 -> retval = 80
                0x137A -> retval = 90
                0x137B -> retval = 100
                0x137C -> retval = 10000
                0x215F -> retval = 1
                0x216C -> retval = 50
                0x216D -> retval = 100
                0x216E -> retval = 500
                0x216F -> retval = 1000
                0x217C -> retval = 50
                0x217D -> retval = 100
                0x217E -> retval = 500
                0x217F -> retval = 1000
                0x2180 -> retval = 1000
                0x2181 -> retval = 5000
                0x2182 -> retval = 10000
                0x2186 -> retval = 50
                0x2187 -> retval = 50000
                0x2188 -> retval = 100000
                0x324B -> retval = 40
                0x324C -> retval = 50
                0x324D -> retval = 60
                0x324E -> retval = 70
                0x324F -> retval = 80
                0x325C -> retval = 32
                0x325D -> retval = 33
                0x325E -> retval = 34
                0x325F -> retval = 35
                0x32B1 -> retval = 36
                0x32B2 -> retval = 37
                0x32B3 -> retval = 38
                0x32B4 -> retval = 39
                0x32B5 -> retval = 40
                0x32B6 -> retval = 41
                0x32B7 -> retval = 42
                0x32B8 -> retval = 43
                0x32B9 -> retval = 44
                0x32BA -> retval = 45
                0x32BB -> retval = 46
                0x32BC -> retval = 47
                0x32BD -> retval = 48
                0x32BE -> retval = 49
                0x32BF -> retval = 50
                else -> retval = -2
            }

            (0x00000C00) -> retval = (ch + ((`val` and 0x3E0) shr 5) and 0x1F) + 10
            else -> retval = -1
        }
        return retval
    }

    override fun isDigit(ch: Int): Boolean {
        val props = getProperties(ch)
        return (props and 0x1F) == Character.DECIMAL_DIGIT_NUMBER
    }

    override fun isLowerCase(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0001) != 0
    }

    override fun isUpperCase(ch: Int): Boolean {
        return (getPropertiesEx(ch) and 0x0002) != 0
    }

    override fun isWhitespace(ch: Int): Boolean {
        val props = getProperties(ch)
        return ((props and 0x00007000) == 0x00004000)
    }

    override fun getDirectionality(ch: Int): Byte {
        val `val` = getProperties(ch)
        var directionality = ((`val` and 0x78000000) shr 27)
        if (directionality == 0xF) {
            when (ch) {
                0x202A ->                     // This is the only char with LRE
                    directionality = Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING

                0x202B ->                     // This is the only char with RLE
                    directionality = Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING

                0x202C ->                     // This is the only char with PDF
                    directionality = Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT

                0x202D ->                     // This is the only char with LRO
                    directionality = Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE

                0x202E ->                     // This is the only char with RLO
                    directionality = Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE

                0x2066 ->                     // This is the only char with LRI
                    directionality = Character.DIRECTIONALITY_LEFT_TO_RIGHT_ISOLATE

                0x2067 ->                     // This is the only char with RLI
                    directionality = Character.DIRECTIONALITY_RIGHT_TO_LEFT_ISOLATE

                0x2068 ->                     // This is the only char with FSI
                    directionality = Character.DIRECTIONALITY_FIRST_STRONG_ISOLATE

                0x2069 ->                     // This is the only char with PDI
                    directionality = Character.DIRECTIONALITY_POP_DIRECTIONAL_ISOLATE

                else -> directionality = Character.DIRECTIONALITY_UNDEFINED
            }
        }
        return directionality.toByte()
    }

    override fun isMirrored(ch: Int): Boolean {
        val props = getProperties(ch)
        return ((props and -0x80000000) != 0)
    }

    override fun toUpperCaseEx(ch: Int): Int {
        var mapChar = ch
        val `val` = getProperties(ch)

        if ((`val` and 0x00010000) != 0) {
            if ((`val` and 0x07FC0000) != 0x07FC0000) {
                val offset = `val` shl 5 shr (5 + 18)
                mapChar = ch - offset
            } else {
                when (ch) {
                    0x017F -> mapChar = 0x0053
                    0x023F -> mapChar = 0x2C7E
                    0x0240 -> mapChar = 0x2C7F
                    0x0250 -> mapChar = 0x2C6F
                    0x0251 -> mapChar = 0x2C6D
                    0x0252 -> mapChar = 0x2C70
                    0x025C -> mapChar = 0xA7AB
                    0x0261 -> mapChar = 0xA7AC
                    0x0265 -> mapChar = 0xA78D
                    0x0266 -> mapChar = 0xA7AA
                    0x026A -> mapChar = 0xA7AE
                    0x026B -> mapChar = 0x2C62
                    0x026C -> mapChar = 0xA7AD
                    0x0271 -> mapChar = 0x2C6E
                    0x027D -> mapChar = 0x2C64
                    0x0282 -> mapChar = 0xA7C5
                    0x0287 -> mapChar = 0xA7B1
                    0x029D -> mapChar = 0xA7B2
                    0x029E -> mapChar = 0xA7B0
                    0x10D0 -> mapChar = 0x1C90
                    0x10D1 -> mapChar = 0x1C91
                    0x10D2 -> mapChar = 0x1C92
                    0x10D3 -> mapChar = 0x1C93
                    0x10D4 -> mapChar = 0x1C94
                    0x10D5 -> mapChar = 0x1C95
                    0x10D6 -> mapChar = 0x1C96
                    0x10D7 -> mapChar = 0x1C97
                    0x10D8 -> mapChar = 0x1C98
                    0x10D9 -> mapChar = 0x1C99
                    0x10DA -> mapChar = 0x1C9A
                    0x10DB -> mapChar = 0x1C9B
                    0x10DC -> mapChar = 0x1C9C
                    0x10DD -> mapChar = 0x1C9D
                    0x10DE -> mapChar = 0x1C9E
                    0x10DF -> mapChar = 0x1C9F
                    0x10E0 -> mapChar = 0x1CA0
                    0x10E1 -> mapChar = 0x1CA1
                    0x10E2 -> mapChar = 0x1CA2
                    0x10E3 -> mapChar = 0x1CA3
                    0x10E4 -> mapChar = 0x1CA4
                    0x10E5 -> mapChar = 0x1CA5
                    0x10E6 -> mapChar = 0x1CA6
                    0x10E7 -> mapChar = 0x1CA7
                    0x10E8 -> mapChar = 0x1CA8
                    0x10E9 -> mapChar = 0x1CA9
                    0x10EA -> mapChar = 0x1CAA
                    0x10EB -> mapChar = 0x1CAB
                    0x10EC -> mapChar = 0x1CAC
                    0x10ED -> mapChar = 0x1CAD
                    0x10EE -> mapChar = 0x1CAE
                    0x10EF -> mapChar = 0x1CAF
                    0x10F0 -> mapChar = 0x1CB0
                    0x10F1 -> mapChar = 0x1CB1
                    0x10F2 -> mapChar = 0x1CB2
                    0x10F3 -> mapChar = 0x1CB3
                    0x10F4 -> mapChar = 0x1CB4
                    0x10F5 -> mapChar = 0x1CB5
                    0x10F6 -> mapChar = 0x1CB6
                    0x10F7 -> mapChar = 0x1CB7
                    0x10F8 -> mapChar = 0x1CB8
                    0x10F9 -> mapChar = 0x1CB9
                    0x10FA -> mapChar = 0x1CBA
                    0x10FD -> mapChar = 0x1CBD
                    0x10FE -> mapChar = 0x1CBE
                    0x10FF -> mapChar = 0x1CBF
                    0x1C80 -> mapChar = 0x0412
                    0x1C81 -> mapChar = 0x0414
                    0x1C82 -> mapChar = 0x041E
                    0x1C83 -> mapChar = 0x0421
                    0x1C84 -> mapChar = 0x0422
                    0x1C85 -> mapChar = 0x0422
                    0x1C86 -> mapChar = 0x042A
                    0x1C87 -> mapChar = 0x0462
                    0x1C88 -> mapChar = 0xA64A
                    0x1D79 -> mapChar = 0xA77D
                    0x1D7D -> mapChar = 0x2C63
                    0x1D8E -> mapChar = 0xA7C6
                    0x1FBE -> mapChar = 0x0399
                    0x2C65 -> mapChar = 0x023A
                    0x2C66 -> mapChar = 0x023E
                    0x2D00 -> mapChar = 0x10A0
                    0x2D01 -> mapChar = 0x10A1
                    0x2D02 -> mapChar = 0x10A2
                    0x2D03 -> mapChar = 0x10A3
                    0x2D04 -> mapChar = 0x10A4
                    0x2D05 -> mapChar = 0x10A5
                    0x2D06 -> mapChar = 0x10A6
                    0x2D07 -> mapChar = 0x10A7
                    0x2D08 -> mapChar = 0x10A8
                    0x2D09 -> mapChar = 0x10A9
                    0x2D0A -> mapChar = 0x10AA
                    0x2D0B -> mapChar = 0x10AB
                    0x2D0C -> mapChar = 0x10AC
                    0x2D0D -> mapChar = 0x10AD
                    0x2D0E -> mapChar = 0x10AE
                    0x2D0F -> mapChar = 0x10AF
                    0x2D10 -> mapChar = 0x10B0
                    0x2D11 -> mapChar = 0x10B1
                    0x2D12 -> mapChar = 0x10B2
                    0x2D13 -> mapChar = 0x10B3
                    0x2D14 -> mapChar = 0x10B4
                    0x2D15 -> mapChar = 0x10B5
                    0x2D16 -> mapChar = 0x10B6
                    0x2D17 -> mapChar = 0x10B7
                    0x2D18 -> mapChar = 0x10B8
                    0x2D19 -> mapChar = 0x10B9
                    0x2D1A -> mapChar = 0x10BA
                    0x2D1B -> mapChar = 0x10BB
                    0x2D1C -> mapChar = 0x10BC
                    0x2D1D -> mapChar = 0x10BD
                    0x2D1E -> mapChar = 0x10BE
                    0x2D1F -> mapChar = 0x10BF
                    0x2D20 -> mapChar = 0x10C0
                    0x2D21 -> mapChar = 0x10C1
                    0x2D22 -> mapChar = 0x10C2
                    0x2D23 -> mapChar = 0x10C3
                    0x2D24 -> mapChar = 0x10C4
                    0x2D25 -> mapChar = 0x10C5
                    0x2D27 -> mapChar = 0x10C7
                    0x2D2D -> mapChar = 0x10CD
                    0xAB53 -> mapChar = 0xA7B3
                    0xAB70 -> mapChar = 0x13A0
                    0xAB71 -> mapChar = 0x13A1
                    0xAB72 -> mapChar = 0x13A2
                    0xAB73 -> mapChar = 0x13A3
                    0xAB74 -> mapChar = 0x13A4
                    0xAB75 -> mapChar = 0x13A5
                    0xAB76 -> mapChar = 0x13A6
                    0xAB77 -> mapChar = 0x13A7
                    0xAB78 -> mapChar = 0x13A8
                    0xAB79 -> mapChar = 0x13A9
                    0xAB7A -> mapChar = 0x13AA
                    0xAB7B -> mapChar = 0x13AB
                    0xAB7C -> mapChar = 0x13AC
                    0xAB7D -> mapChar = 0x13AD
                    0xAB7E -> mapChar = 0x13AE
                    0xAB7F -> mapChar = 0x13AF
                    0xAB80 -> mapChar = 0x13B0
                    0xAB81 -> mapChar = 0x13B1
                    0xAB82 -> mapChar = 0x13B2
                    0xAB83 -> mapChar = 0x13B3
                    0xAB84 -> mapChar = 0x13B4
                    0xAB85 -> mapChar = 0x13B5
                    0xAB86 -> mapChar = 0x13B6
                    0xAB87 -> mapChar = 0x13B7
                    0xAB88 -> mapChar = 0x13B8
                    0xAB89 -> mapChar = 0x13B9
                    0xAB8A -> mapChar = 0x13BA
                    0xAB8B -> mapChar = 0x13BB
                    0xAB8C -> mapChar = 0x13BC
                    0xAB8D -> mapChar = 0x13BD
                    0xAB8E -> mapChar = 0x13BE
                    0xAB8F -> mapChar = 0x13BF
                    0xAB90 -> mapChar = 0x13C0
                    0xAB91 -> mapChar = 0x13C1
                    0xAB92 -> mapChar = 0x13C2
                    0xAB93 -> mapChar = 0x13C3
                    0xAB94 -> mapChar = 0x13C4
                    0xAB95 -> mapChar = 0x13C5
                    0xAB96 -> mapChar = 0x13C6
                    0xAB97 -> mapChar = 0x13C7
                    0xAB98 -> mapChar = 0x13C8
                    0xAB99 -> mapChar = 0x13C9
                    0xAB9A -> mapChar = 0x13CA
                    0xAB9B -> mapChar = 0x13CB
                    0xAB9C -> mapChar = 0x13CC
                    0xAB9D -> mapChar = 0x13CD
                    0xAB9E -> mapChar = 0x13CE
                    0xAB9F -> mapChar = 0x13CF
                    0xABA0 -> mapChar = 0x13D0
                    0xABA1 -> mapChar = 0x13D1
                    0xABA2 -> mapChar = 0x13D2
                    0xABA3 -> mapChar = 0x13D3
                    0xABA4 -> mapChar = 0x13D4
                    0xABA5 -> mapChar = 0x13D5
                    0xABA6 -> mapChar = 0x13D6
                    0xABA7 -> mapChar = 0x13D7
                    0xABA8 -> mapChar = 0x13D8
                    0xABA9 -> mapChar = 0x13D9
                    0xABAA -> mapChar = 0x13DA
                    0xABAB -> mapChar = 0x13DB
                    0xABAC -> mapChar = 0x13DC
                    0xABAD -> mapChar = 0x13DD
                    0xABAE -> mapChar = 0x13DE
                    0xABAF -> mapChar = 0x13DF
                    0xABB0 -> mapChar = 0x13E0
                    0xABB1 -> mapChar = 0x13E1
                    0xABB2 -> mapChar = 0x13E2
                    0xABB3 -> mapChar = 0x13E3
                    0xABB4 -> mapChar = 0x13E4
                    0xABB5 -> mapChar = 0x13E5
                    0xABB6 -> mapChar = 0x13E6
                    0xABB7 -> mapChar = 0x13E7
                    0xABB8 -> mapChar = 0x13E8
                    0xABB9 -> mapChar = 0x13E9
                    0xABBA -> mapChar = 0x13EA
                    0xABBB -> mapChar = 0x13EB
                    0xABBC -> mapChar = 0x13EC
                    0xABBD -> mapChar = 0x13ED
                    0xABBE -> mapChar = 0x13EE
                    0xABBF -> mapChar = 0x13EF
                    else -> mapChar = Character.ERROR
                }
            }
        }
        return mapChar
    }

    override fun toUpperCaseCharArray(ch: Int): CharArray? {
        var upperMap: CharArray? = charArrayOf(ch.toChar())
        val location = findInCharMap(ch)
        if (location != -1) {
            upperMap = charMap[location][1]
        }
        return upperMap
    }


    /**
     * Finds the character in the uppercase mapping table.
     *
     * @param ch the `char` to search
     * @return the index location ch in the table or -1 if not found
     * @since 1.4
     */
    fun findInCharMap(ch: Int): Int {
        if (charMap.isEmpty()) {
            return -1
        }
        var top: Int
        var bottom: Int
        var current: Int
        bottom = 0
        top = charMap.size
        current = top / 2
        // invariant: top > current >= bottom && ch >= CharacterData.charMap[bottom][0]
        while (top - bottom > 1) {
            if (ch >= charMap[current][0][0].code) {
                bottom = current
            } else {
                top = current
            }
            current = (top + bottom) / 2
        }
        return if (ch == charMap[current][0][0].code) current
        else -1
    }

    companion object {
        val instance: CharacterData00 = CharacterData00()
        val charMap: Array<Array<CharArray>> = arrayOf<Array<CharArray>>(
            arrayOf<CharArray>(charArrayOf('\u00DF'), charArrayOf('\u0053', '\u0053')),
            arrayOf<CharArray>(charArrayOf('\u0130'), charArrayOf('\u0130')),
            arrayOf<CharArray>(charArrayOf('\u0149'), charArrayOf('\u02BC', '\u004E')),
            arrayOf<CharArray>(charArrayOf('\u01F0'), charArrayOf('\u004A', '\u030C')),
            arrayOf<CharArray>(charArrayOf('\u0390'), charArrayOf('\u0399', '\u0308', '\u0301')),
            arrayOf<CharArray>(charArrayOf('\u03B0'), charArrayOf('\u03A5', '\u0308', '\u0301')),
            arrayOf<CharArray>(charArrayOf('\u0587'), charArrayOf('\u0535', '\u0552')),
            arrayOf<CharArray>(charArrayOf('\u1E96'), charArrayOf('\u0048', '\u0331')),
            arrayOf<CharArray>(charArrayOf('\u1E97'), charArrayOf('\u0054', '\u0308')),
            arrayOf<CharArray>(charArrayOf('\u1E98'), charArrayOf('\u0057', '\u030A')),
            arrayOf<CharArray>(charArrayOf('\u1E99'), charArrayOf('\u0059', '\u030A')),
            arrayOf<CharArray>(charArrayOf('\u1E9A'), charArrayOf('\u0041', '\u02BE')),
            arrayOf<CharArray>(charArrayOf('\u1F50'), charArrayOf('\u03A5', '\u0313')),
            arrayOf<CharArray>(charArrayOf('\u1F52'), charArrayOf('\u03A5', '\u0313', '\u0300')),
            arrayOf<CharArray>(charArrayOf('\u1F54'), charArrayOf('\u03A5', '\u0313', '\u0301')),
            arrayOf<CharArray>(charArrayOf('\u1F56'), charArrayOf('\u03A5', '\u0313', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1F80'), charArrayOf('\u1F08', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F81'), charArrayOf('\u1F09', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F82'), charArrayOf('\u1F0A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F83'), charArrayOf('\u1F0B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F84'), charArrayOf('\u1F0C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F85'), charArrayOf('\u1F0D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F86'), charArrayOf('\u1F0E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F87'), charArrayOf('\u1F0F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F88'), charArrayOf('\u1F08', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F89'), charArrayOf('\u1F09', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8A'), charArrayOf('\u1F0A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8B'), charArrayOf('\u1F0B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8C'), charArrayOf('\u1F0C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8D'), charArrayOf('\u1F0D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8E'), charArrayOf('\u1F0E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F8F'), charArrayOf('\u1F0F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F90'), charArrayOf('\u1F28', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F91'), charArrayOf('\u1F29', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F92'), charArrayOf('\u1F2A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F93'), charArrayOf('\u1F2B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F94'), charArrayOf('\u1F2C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F95'), charArrayOf('\u1F2D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F96'), charArrayOf('\u1F2E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F97'), charArrayOf('\u1F2F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F98'), charArrayOf('\u1F28', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F99'), charArrayOf('\u1F29', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9A'), charArrayOf('\u1F2A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9B'), charArrayOf('\u1F2B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9C'), charArrayOf('\u1F2C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9D'), charArrayOf('\u1F2D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9E'), charArrayOf('\u1F2E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1F9F'), charArrayOf('\u1F2F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA0'), charArrayOf('\u1F68', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA1'), charArrayOf('\u1F69', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA2'), charArrayOf('\u1F6A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA3'), charArrayOf('\u1F6B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA4'), charArrayOf('\u1F6C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA5'), charArrayOf('\u1F6D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA6'), charArrayOf('\u1F6E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA7'), charArrayOf('\u1F6F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA8'), charArrayOf('\u1F68', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FA9'), charArrayOf('\u1F69', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAA'), charArrayOf('\u1F6A', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAB'), charArrayOf('\u1F6B', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAC'), charArrayOf('\u1F6C', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAD'), charArrayOf('\u1F6D', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAE'), charArrayOf('\u1F6E', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FAF'), charArrayOf('\u1F6F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FB2'), charArrayOf('\u1FBA', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FB3'), charArrayOf('\u0391', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FB4'), charArrayOf('\u0386', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FB6'), charArrayOf('\u0391', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FB7'), charArrayOf('\u0391', '\u0342', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FBC'), charArrayOf('\u0391', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FC2'), charArrayOf('\u1FCA', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FC3'), charArrayOf('\u0397', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FC4'), charArrayOf('\u0389', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FC6'), charArrayOf('\u0397', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FC7'), charArrayOf('\u0397', '\u0342', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FCC'), charArrayOf('\u0397', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FD2'), charArrayOf('\u0399', '\u0308', '\u0300')),
            arrayOf<CharArray>(charArrayOf('\u1FD3'), charArrayOf('\u0399', '\u0308', '\u0301')),
            arrayOf<CharArray>(charArrayOf('\u1FD6'), charArrayOf('\u0399', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FD7'), charArrayOf('\u0399', '\u0308', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FE2'), charArrayOf('\u03A5', '\u0308', '\u0300')),
            arrayOf<CharArray>(charArrayOf('\u1FE3'), charArrayOf('\u03A5', '\u0308', '\u0301')),
            arrayOf<CharArray>(charArrayOf('\u1FE4'), charArrayOf('\u03A1', '\u0313')),
            arrayOf<CharArray>(charArrayOf('\u1FE6'), charArrayOf('\u03A5', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FE7'), charArrayOf('\u03A5', '\u0308', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FF2'), charArrayOf('\u1FFA', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FF3'), charArrayOf('\u03A9', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FF4'), charArrayOf('\u038F', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FF6'), charArrayOf('\u03A9', '\u0342')),
            arrayOf<CharArray>(charArrayOf('\u1FF7'), charArrayOf('\u03A9', '\u0342', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\u1FFC'), charArrayOf('\u03A9', '\u0399')),
            arrayOf<CharArray>(charArrayOf('\uFB00'), charArrayOf('\u0046', '\u0046')),
            arrayOf<CharArray>(charArrayOf('\uFB01'), charArrayOf('\u0046', '\u0049')),
            arrayOf<CharArray>(charArrayOf('\uFB02'), charArrayOf('\u0046', '\u004C')),
            arrayOf<CharArray>(charArrayOf('\uFB03'), charArrayOf('\u0046', '\u0046', '\u0049')),
            arrayOf<CharArray>(charArrayOf('\uFB04'), charArrayOf('\u0046', '\u0046', '\u004C')),
            arrayOf<CharArray>(charArrayOf('\uFB05'), charArrayOf('\u0053', '\u0054')),
            arrayOf<CharArray>(charArrayOf('\uFB06'), charArrayOf('\u0053', '\u0054')),
            arrayOf<CharArray>(charArrayOf('\uFB13'), charArrayOf('\u0544', '\u0546')),
            arrayOf<CharArray>(charArrayOf('\uFB14'), charArrayOf('\u0544', '\u0535')),
            arrayOf<CharArray>(charArrayOf('\uFB15'), charArrayOf('\u0544', '\u053B')),
            arrayOf<CharArray>(charArrayOf('\uFB16'), charArrayOf('\u054E', '\u0546')),
            arrayOf<CharArray>(charArrayOf('\uFB17'), charArrayOf('\u0544', '\u053D')),
        )

        // The X table has 2048 entries for a total of 4096 bytes.
        val X: CharArray = ("\u0000\u0010\u0020\u0030\u0040\u0050\u0060\u0070\u0080\u0090\u00a0\u00b0\u00c0\u00d0\u00e0\u00f0\u0080\u0100" +
                "\u0110\u0120\u0130\u0140\u0150\u0160\u0170\u0170\u0180\u0190\u01A0\u01B0\u01C0" +
                "\u01D0\u01E0\u01F0\u0200\u0080\u0210\u0080\u0220\u0080\u0080\u0230\u0240\u0250\u0260" +
                "\u0270\u0280\u0290\u02A0\u02B0\u02C0\u02D0\u02B0\u02B0\u02E0\u02F0\u0300\u0310" +
                "\u0320\u02B0\u02B0\u0330\u0340\u0350\u0360\u0370\u0380\u0390\u03A0\u02B0\u03B0" +
                "\u03C0\u03D0\u03E0\u03F0\u0400\u0410\u0420\u0430\u0440\u0450\u0460\u0470\u0480" +
                "\u0490\u04A0\u04B0\u04C0\u04D0\u04E0\u04F0\u0500\u0510\u0520\u0530\u0540\u0550" +
                "\u0560\u0570\u0580\u0590\u05A0\u05B0\u05C0\u05D0\u05E0\u05F0\u0600\u0610\u0620" +
                "\u0630\u0640\u0650\u0660\u0670\u0680\u0690\u06A0\u06B0\u0680\u06C0\u06D0\u06E0" +
                "\u06F0\u0700\u0710\u0720\u0680\u0730\u0740\u0750\u0760\u0770\u0780\u0790\u07A0" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u07B0\u0730\u07C0" +
                "\u07D0\u07E0\u0730\u07F0\u0730\u0800\u0810\u0820\u0780\u0780\u0830\u0840\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0850\u0860\u0730\u0730\u0870\u0880\u0890\u08A0\u08B0" +
                "\u0730\u08C0\u08D0\u08E0\u08F0\u0730\u0900\u0910\u0920\u0930\u0730\u0940\u0950" +
                "\u0960\u0970\u0980\u0730\u0990\u09A0\u09B0\u09C0\u0730\u09D0\u09E0\u09F0\u0A00" +
                "\u0A10\u0680\u0A20\u0A30\u0A40\u0A50\u0A60\u0A70\u0730\u0A80\u0730\u0A90\u0AA0" +
                "\u0AB0\u0AC0\u0AD0\u0AE0\u0AF0\u0B00\u0B10\u0B20\u0B30\u0B40\u0B20\u0170\u0B50" +
                "\u0080\u0080\u0080\u0080\u0B60\u0080\u0080\u0080\u0B70\u0B80\u0B90\u0BA0\u0BB0\u0BC0\u0BD0" +
                "\u0BE0\u0BF0\u0C00\u0C10\u0C20\u0C30\u0C40\u0C50\u0C60\u0C70\u0C80\u0C90\u0CA0" +
                "\u0CB0\u0CC0\u0CD0\u0CE0\u0CF0\u0D00\u0D10\u0D20\u0D30\u0D40\u0D50\u0D60\u0D70" +
                "\u0D80\u0D90\u0DA0\u0DB0\u0DC0\u0DD0\u0DE0\u09B0\u0DF0\u0E00\u0E10\u0E20\u0E30" +
                "\u0E40\u0E50\u09B0\u09B0\u09B0\u09B0\u09B0\u0E60\u0E70\u0E80\u0E90\u0EA0\u0EB0" +
                "\u0EC0\u0ED0\u0EE0\u0EF0\u0F00\u0F10\u0F20\u0F30\u0F40\u0F50\u0F60\u0F70\u0F80" +
                "\u0D90\u0D90\u0D90\u0D90\u0D90\u0D90\u0D90\u0D90\u0F90\u0FA0\u0F90\u0F90\u0FB0" +
                "\u0FC0\u0FD0\u0FE0\u0FF0\u1000\u1010\u1020\u1030\u1040\u1050\u1060\u1070\u1080" +
                "\u1090\u10A0\u10B0\u09B0\u09B0\u10C0\u10D0\u10E0\u10F0\u1100\u0080\u0080\u0080\u1110" +
                "\u1120\u1130\u0730\u1140\u1150\u1160\u1160\u1170\u1180\u1190\u11A0\u0680\u11B0" +
                "\u09B0\u09B0\u11C0\u09B0\u09B0\u09B0\u09B0\u09B0\u09B0\u11D0\u11E0\u11F0\u1200" +
                "\u0650\u0730\u1210\u0840\u0730\u1220\u1230\u1240\u0730\u0730\u1250\u0730\u09B0" +
                "\u1260\u1270\u1280\u1290\u12A0\u12B0\u12C0\u12D0\u0D90\u0D90\u0D90\u0D90\u12E0" +
                "\u0D90\u0D90\u12F0\u1300\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u09B0\u09B0\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310" +
                "\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1310\u1320\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u1330\u09B0\u1340\u0AB0\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u1350\u1360\u0080\u1370\u1380\u0730\u0730" +
                "\u1390\u13A0\u13B0\u0080\u13C0\u13D0\u13E0\u13F0\u1400\u1410\u1420\u0730\u1430" +
                "\u1440\u1450\u1460\u1470\u1480\u1490\u14A0\u14B0\u03D0\u14C0\u14D0\u14E0\u0730" +
                "\u14F0\u1500\u1510\u0730\u1520\u1530\u1540\u1550\u1560\u1570\u1580\u1120\u1120" +
                "\u0730\u1590\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730\u0730" +
                "\u15A0\u15B0\u15C0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0" +
                "\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0" +
                "\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0" +
                "\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0" +
                "\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0\u15D0" +
                "\u15D0\u15D0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0" +
                "\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u15E0\u1310\u1310\u1310\u15F0\u1310\u1600" +
                "\u1610\u1620\u1310\u1310\u1310\u1630\u1310\u1310\u1640\u0680\u1650\u1660\u1670" +
                "\u02B0\u02B0\u1680\u1690\u02B0\u02B0\u02B0\u02B0\u02B0\u02B0\u02B0\u02B0\u02B0" +
                "\u02B0\u16A0\u16B0\u02B0\u16C0\u02B0\u16D0\u16E0\u16F0\u1700\u1710\u1720\u02B0" +
                "\u02B0\u02B0\u1730\u1740\u0020\u1750\u1760\u1770\u0950\u1780\u1790").toCharArray()

        // The Y table has 6048 entries for a total of 12096 bytes.
        val Y: CharArray = ("\u0000\u0000\u0000\u0000\u0002\u0004\u0006\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0008\u0004\u000a\u000c\u000e" +
                "\u0010\u0012\u0014\u0016\u0018\u001a\u001a\u001a\u001a\u001a\u001c\u001e\u0020\u0022\u0024\u0024\u0024\u0024\u0024" +
                "\u0024\u0024\u0024\u0024\u0024\u0024\u0024\u0026\u0028\u002a\u002c\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u002e" +
                "\u002e\u002e\u002e\u002e\u0030\u0032\u0034\u0000\u0000\u0036\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0038\u003a\u003a\u003c\u003e\u0040\u0042\u0044\u0046\u0048\u004a\u004c\u004e\u0050\u0052" +
                "\u0054\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0058\u0056\u0056\u0056\u005a\u005c\u005c" +
                "\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005e\u005c\u005c\u005c\u0060\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0064\u0062\u0062\u0062\u0066\u0068\u0068\u0068\u0068\u0068\u0068\u0068\u006a\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u006c\u0068" +
                "\u0068\u006a\u006e\u0062\u0062\u0070\u0072\u0074\u0076\u0078\u007a\u0072\u007c\u007e\u0062\u0080\u0082\u0084\u0062" +
                "\u0062\u0062\u0086\u0088\u008a\u0062\u0086\u008c\u008e\u0068\u0090\u0062\u0092\u0062\u0094\u0096\u0096\u0098\u009a" +
                "\u009c\u0098\u009e\u0068\u0068\u0068\u0068\u0068\u0068\u0068\u00a0\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u00a2\u009c\u0062\u00a4\u0062\u0062\u0062\u0062\u00a6\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u008a\u008a\u008a\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u0062\u0062\u0062\u0062\u0062\u00b4\u00b6\u00b8\u00ba\u00bc" +
                "\u00be\u00c0\u008a\u00c2\u00c4\u00c6\u00c0\u00c8\u00b4\u00c0\u00ca\u00c6\u00cc\u00ce\u008a\u008a\u008a\u00c6\u008a" +
                "\u00d0\u00d2\u008a\u00c6\u00d4\u00d6\u00d8\u008a\u008a\u00da\u00dc\u008a\u008a\u008a\u00c6\u00c0\u008a\u008a\u008a" +
                "\u008a\u008a\u008a\u008a\u008a\u00de\u00de\u00de\u00de\u00e0\u00e2\u00e4\u00e4\u00de\u00e6\u00e6\u00e8\u00e8\u00e8" +
                "\u00e8\u00e8\u00e4\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00de\u00de\u00ea\u00e6\u00e6\u00e6\u00ec\u00ee\u00e6" +
                "\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0" +
                "\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f2\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0" +
                "\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u0062\u0062\u00ec\u0062\u00f4\u00f6\u00f8\u00fa\u00f4\u00f4" +
                "\u00e6\u00fc\u00fe\u0100\u0102\u0104\u0106\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0108" +
                "\u0056\u0056\u0056\u0056\u010A\u010C\u010E\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u0110\u005c" +
                "\u005c\u005c\u005c\u0112\u0114\u0116\u0118\u011A\u011C\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0062\u0062\u0062\u0062\u011E\u0120\u0122\u0124\u0126\u0062\u0128\u012A\u012C\u012C" +
                "\u012C\u012C\u012C\u012C\u012C\u012C\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056\u0056" +
                "\u0056\u0056\u0056\u0056\u0056\u0056\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c" +
                "\u005c\u005c\u005c\u012E\u012E\u012E\u012E\u012E\u012E\u012E\u012E\u0062\u0130\u00f0" +
                "\u00f0\u0132\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0134\u0068\u0068\u0068\u0068" +
                "\u0068\u0068\u0136\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0138\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A" +
                "\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013C\u013E\u0140\u0140\u0140\u0142" +
                "\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144" +
                "\u0144\u0144\u0144\u0144\u0144\u0146\u0148\u014A\u014C\u014E\u0150\u00f0\u00f0" +
                "\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u0152\u0154\u0156\u0158\u0152\u0156\u00f4\u00f4\u00f4\u00f4\u015A\u015A" +
                "\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015C\u00f4" +
                "\u015E\u015A\u0160\u0162\u00f4\u00f4\u00f4\u00f4\u00f4\u0164\u0164\u0164\u0166\u0168" +
                "\u016A\u016C\u016E\u0152\u0152\u0152\u0152\u0152\u0170\u0172\u0174\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0178\u0176\u0176\u0176\u0176\u017A\u0152\u0152\u0152\u0152\u0152\u0152" +
                "\u017C\u0152\u0152\u0152\u017E\u017E\u017E\u017E\u017E\u0180\u0182\u0176\u0184" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0186\u0152\u0152\u0152\u0188\u018A\u017C\u0152\u018C" +
                "\u018E\u0190\u00f0\u017C\u0176\u0192\u0192\u0192\u0192\u0192\u0176\u0194\u0196" +
                "\u0174\u0174\u0174\u0174\u0174\u0174\u0174\u0198\u017A\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0152\u0152" +
                "\u0152\u0152\u0152\u0152\u0152\u0152\u00f0\u00f0\u00f0\u00f0\u00f0\u019A\u019C\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0152\u0152" +
                "\u0152\u0152\u0152\u0184\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u019E\u019E\u019E\u019E" +
                "\u019E\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A" +
                "\u015A\u015A\u015A\u015A\u01A0\u00f0\u00f0\u00f0\u00f0\u01A2\u003c\u0010\u01A4\u0150\u01A6" +
                "\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u015A\u0152\u00f0" +
                "\u01A8\u0152\u0152\u0152\u0152\u01A8\u0152\u01A8\u0152\u01AA\u00f4\u01AC\u01AC" +
                "\u01AC\u01AC\u01AC\u01AC\u01AC\u0162\u015A\u015A\u015A\u015A\u015A\u015A\u015A" +
                "\u015A\u015A\u015A\u015A\u015A\u01A0\u00f0\u00f4\u0162\u0176\u0176\u0176\u0176" +
                "\u0176\u01AE\u00f4\u00f4\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u01B0\u0176\u0176\u01AE\u0164\u00f4\u00f4\u00f4\u00f0\u00f0\u00f0\u00f0" +
                "\u0176\u0176\u0176\u0176\u01B2\u00f0\u00f0\u00f0\u00f0\u00f0\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u00f0\u01B4\u0152\u0152\u0152\u00f0\u00f0\u00f0\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u0152\u0152\u0152\u01B6\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01B6" +
                "\u01B8\u01BA\u01BC\u0152\u0152\u0152\u01B6\u01BA\u01BE\u01BA\u01C0\u00f0\u017C" +
                "\u0152\u0096\u0096\u0096\u0096\u0096\u0152\u0140\u01C2\u01C2\u01C2\u01C2\u01C2\u01C4" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01C6\u01BA\u01C8\u0096\u0096\u0096\u01CA\u01C8\u01CA" +
                "\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA\u01CA" +
                "\u00f4\u0096\u0096\u00f4\u01B8\u01BA\u01BC\u0152\u01CC\u01CE\u01D0\u01CE\u01BE\u01CA" +
                "\u00f4\u00f4\u00f4\u01CE\u00f4\u00f4\u0096\u01C8\u0096\u0152\u00f4\u01C2\u01C2\u01C2\u01C2" +
                "\u01C2\u0096\u003a\u01D2\u01D2\u01D4\u01D6\u01D8\u019A\u01DA\u01B6\u01C8\u0096\u0096" +
                "\u01CA\u00f4\u01C8\u01CA\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA" +
                "\u0096\u0096\u0096\u01CA\u0096\u01C8\u01CA\u0096\u00f4\u019A\u01BA\u01BC\u01CC\u00f4\u01DA" +
                "\u01CC\u01DA\u01AA\u00f4\u01DA\u00f4\u00f4\u00f4\u01C8\u0096\u01CA\u01CA\u00f4\u00f4\u00f4" +
                "\u01C2\u01C2\u01C2\u01C2\u01C2\u0152\u0096\u01C6\u01DC\u00f4\u00f4\u00f4\u00f4\u01DA" +
                "\u01B6\u01C8\u0096\u0096\u0096\u0096\u01C8\u0096\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA\u0096\u01C8\u0096\u0096\u00f4\u01B8\u01BA\u01BC\u0152" +
                "\u0152\u01DA\u01B6\u01CE\u01BE\u00f4\u01CA\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0096\u0152" +
                "\u00f4\u01C2\u01C2\u01C2\u01C2\u01C2\u01DE\u00f4\u00f4\u00f4\u01C8\u0152\u01AA\u00f0" +
                "\u01DA\u01BA\u01C8\u0096\u0096\u0096\u01CA\u01C8\u01CA\u01C8\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA\u0096\u01C8\u0096\u0096\u00f4\u01B8\u01BC" +
                "\u01BC\u0152\u01CC\u01CE\u01D0\u01CE\u01BE\u00f4\u00f4\u00f4\u0150\u01B6\u00f4\u00f4" +
                "\u0096\u01C8\u0096\u0152\u00f4\u01C2\u01C2\u01C2\u01C2\u01C2\u01E0\u01D2\u01D2\u01D2" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u01E2\u01C8\u0096\u0096\u01CA\u00f4\u0096\u01CA\u0096\u0096\u00f4\u01C8" +
                "\u01CA\u01CA\u0096\u00f4\u01C8\u01CA\u00f4\u0096\u01CA\u00f4\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u00f4\u00f4\u01BA\u01B6\u01D0\u00f4\u01BA\u01D0\u01BA\u01BE\u00f4\u01CA\u00f4\u00f4\u01CE" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u01C2\u01C2\u01C2\u01C2\u01C2\u01E4\u01E6\u016E" +
                "\u016E\u014E\u01E8\u00f4\u00f4\u01B6\u01BA\u01E2\u0096\u0096\u0096\u01CA\u0096\u01CA\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u00f4\u01B8\u0152\u01B6\u01BA\u01D0\u0152\u01CC\u0152\u01AA\u00f4\u00f4\u00f4" +
                "\u01DA\u01CC\u0096\u01CA\u01C8\u00f4\u0096\u0152\u00f4\u01C2\u01C2\u01C2\u01C2\u01C2" +
                "\u00f4\u00f4\u00f4\u01EA\u01EC\u01EC\u01EE\u01F0\u01C6\u01BA\u01F2\u0096\u0096\u0096\u01CA" +
                "\u0096\u01CA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u0096" +
                "\u0096\u01C8\u0096\u0096\u00f4\u01B8\u01F4\u01BA\u01BA\u01D0\u01F6\u01D0\u01BA\u01AA" +
                "\u00f4\u00f4\u00f4\u01CE\u01D0\u00f4\u00f4\u01C8\u01CA\u0096\u0152\u00f4\u01C2\u01C2\u01C2" +
                "\u01C2\u01C2\u01C8\u01F8\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0152\u01BA\u0096\u0096\u0096\u0096" +
                "\u01CA\u0096\u01CA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u01C0\u01B8\u01BA\u01BC\u0152\u01CC\u01BA\u01D0\u01BA" +
                "\u01BE\u01FA\u00f4\u00f4\u0096\u01F8\u01D2\u01D2\u01D2\u01FC\u0096\u0152\u00f4\u01C2" +
                "\u01C2\u01C2\u01C2\u01C2\u01E4\u01D2\u01D2\u01D2\u01FE\u0096\u0096\u0096\u01DA\u01BA" +
                "\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u01C8\u0096\u0096\u0096\u0096\u01C8\u00f4\u0096\u0096\u0096\u01CA\u00f4" +
                "\u019A\u00f4\u01CE\u01BA\u0152\u01CC\u01CC\u01BA\u01BA\u01BA\u01BA\u00f4\u00f4\u00f4" +
                "\u01C2\u01C2\u01C2\u01C2\u01C2\u00f4\u01BA\u01DC\u00f4\u00f4\u00f4\u00f4\u00f4\u01C8\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u01C6\u0096\u0152\u0152\u0152\u01CC\u00f4\u0200\u0096\u0096\u0096\u0202" +
                "\u00f0\u00f0\u017C\u0204\u0206\u0206\u0206\u0206\u0206\u0140\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u01C8\u01CA\u01CA\u0096" +
                "\u0096\u01CA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01C8\u01C8\u0096" +
                "\u0096\u0096\u0096\u01C6\u0096\u0152\u0152\u0152\u017C\u01E2\u00f4\u0096\u0096\u01CA\u0208" +
                "\u00f0\u00f0\u017C\u019A\u0206\u0206\u0206\u0206\u0206\u00f4\u0096\u0096\u01FA\u020A" +
                "\u0140\u0140\u0140\u0140\u0140\u0140\u0140\u020C\u020C\u020A\u00f0\u020A\u020A" +
                "\u020A\u020E\u020E\u020E\u020E\u020E\u01D2\u01D2\u01D2\u01D2\u01D2\u0130\u0130" +
                "\u0130\u0012\u0012\u0210\u0096\u0096\u0096\u0096\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u00f4\u01DA\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u01B6\u0152\u0152\u0204\u00f0\u0096\u0096\u01C6\u0152\u0152\u0152\u0152" +
                "\u0152\u01DA\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u0152\u0152\u0152\u0152\u01CC\u020A\u020A\u020A\u020A\u0212\u020A" +
                "\u020A\u0214\u020A\u0140\u0140\u020C\u020A\u0216\u01DC\u00f4\u00f4\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01F8" +
                "\u01BC\u0152\u01B6\u0152\u0152\u01AA\u01BE\u0218\u01BC\u01E2\u020E\u020E\u020E" +
                "\u020E\u020E\u0140\u0140\u0140\u0096\u0096\u0096\u01BA\u0152\u0096\u0096\u0152\u01E2" +
                "\u01BA\u021A\u01F8\u01BA\u01BA\u01BA\u0096\u01C6\u0152\u01E2\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u01B6\u01BC\u01B6\u01BA\u01BA\u01BC\u01F8\u0206\u0206\u0206\u0206" +
                "\u0206\u01BA\u01BC\u020A\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C" +
                "\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021E\u00f4\u00f4" +
                "\u021E\u00f4\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220" +
                "\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0220\u0222\u0224\u0220" +
                "\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u00f4\u0096\u0096\u0096\u01CA\u01CA\u0096\u0096\u00f4\u0096" +
                "\u0096\u0096\u0096\u01CA\u0096\u0096\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u00f4\u0096\u0096\u0096\u01CA\u01CA\u0096\u0096\u00f4" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u01CA\u0096\u0096\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u01CA\u0150\u00f0\u0140\u0140\u0140\u0140\u0226\u0228\u0228" +
                "\u0228\u0228\u022A\u022C\u01D2\u01D2\u01D2\u022E\u00f4\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u016E\u016E\u016E\u016E\u016E\u00f4\u00f4\u00f4\u021C\u021C\u021C\u021C" +
                "\u021C\u021C\u021C\u021C\u0230\u0230\u0230\u00f4\u0232\u0232\u0232\u00f4\u0234" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u01FA\u01F2\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0236\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0238\u023A\u00f4\u0096\u0096\u0096\u0096\u0096\u01D8" +
                "\u0140\u023C\u023E\u0096\u0096\u0096\u01CA\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0152\u0240\u00f4\u00f4\u00f4\u00f4\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0152\u0242\u01DC\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0152\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0096\u01CA\u0152" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00f0\u01BC" +
                "\u0152\u0152\u0152\u01BA\u01BA\u01BA\u01BA\u01B6\u01BE\u00f0\u00f0\u00f0\u00f0\u00f0" +
                "\u0140\u01C4\u0140\u01DE\u01C0\u00f4\u020E\u020E\u020E\u020E\u020E\u00f4\u00f4\u00f4" +
                "\u0244\u0244\u0244\u0244\u0244\u00f4\u00f4\u00f4\u0010\u0010\u0010\u0246\u0010\u0248\u00f0" +
                "\u024A\u0206\u0206\u0206\u0206\u0206\u00f4\u00f4\u00f4\u0096\u024C\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u01CA\u00f4\u00f4\u00f4\u0096\u0096\u024E\u0250\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01C6\u01CA\u00f4\u00f4\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0152" +
                "\u01B6\u01BA\u01BC\u01B6\u01BA\u00f4\u00f4\u01BA\u01B6\u01BA\u01BA\u01BE\u00f0\u00f4" +
                "\u00f4\u01E8\u00f4\u0010\u01C2\u01C2\u01C2\u01C2\u01C2\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00f4\u0096\u0096\u01CA\u00f4\u00f4\u00f4\u00f4\u00f4\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u00f4\u00f4\u00f4\u0206\u0206\u0206\u0206\u0206\u0252\u00f4\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01C6\u01B6\u01BC\u00f4\u0140" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01F8\u01B6\u0152\u0152\u0152\u01CC" +
                "\u0218\u01B6\u01BC\u0152\u0152\u0152\u01B6\u01BA\u01BA\u01BC\u01AA\u00f0\u00f0" +
                "\u00f0\u019A\u0150\u020E\u020E\u020E\u020E\u020E\u00f4\u00f4\u00f4\u0206\u0206\u0206" +
                "\u0206\u0206\u00f4\u00f4\u00f4\u0140\u0140\u0140\u01C4\u0140\u0140\u0140\u00f4\u00f0" +
                "\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u0254\u01AA\u00f0\u00f0\u00f0\u00f0\u00f0\u0152\u01CC\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0152\u0152\u021A\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0218\u0152\u0152" +
                "\u01B6\u01B6\u01BA\u01BA\u01B6\u0256\u0096\u0096\u0096\u01CA\u00f4\u0206\u0206\u0206" +
                "\u0206\u0206\u0140\u0140\u0140\u020C\u020A\u020A\u020A\u020A\u0130\u00f0\u00f0" +
                "\u00f0\u00f0\u020A\u020A\u020A\u020A\u0216\u01DC\u0152\u021A\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01F8\u0152\u0152\u01BA\u0152\u0258\u0152" +
                "\u0096\u0206\u0206\u0206\u0206\u0206\u0096\u0096\u0096\u0096\u0096\u0096\u0218\u0152\u01BA" +
                "\u01BC\u01BC\u0152\u0210\u00f4\u00f4\u00f4\u00f4\u0140\u0140\u0096\u0096\u01BA\u01BA\u01BA" +
                "\u01BA\u0152\u0152\u0152\u0152\u01BA\u01AA\u00f4\u01EA\u0140\u0140\u020E\u020E" +
                "\u020E\u020E\u020E\u00f4\u01C8\u0096\u0206\u0206\u0206\u0206\u0206\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00e4\u00e4\u00e4\u0140\u00b4\u00b4" +
                "\u00b4\u00b4\u025A\u00f4\u00f4\u00f4\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C" +
                "\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C\u021C" +
                "\u025C\u021E\u021C\u0140\u0140\u0140\u0140\u00f4\u00f4\u00f4\u00f4\u00f0\u0204\u00f0\u00f0" +
                "\u00f0\u00f0\u00f0\u00f0\u0240\u00f0\u00f0\u00f0\u01B8\u0096\u01C0\u0096\u0096\u0096\u01B8\u025E" +
                "\u00f0\u01CA\u00f4\u00f4\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a" +
                "\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de" +
                "\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de\u00de" +
                "\u00de\u0260\u008a\u008a\u008a\u008a\u008a\u008a\u0262\u008a\u00c6\u008a\u008a\u008a\u008a\u008a\u008a\u008a" +
                "\u008a\u00c0\u008a\u008a\u008a\u008a\u008a\u0264\u00de\u00de\u00f0\u00f0\u00f0\u017C\u0152\u0152\u0152" +
                "\u0152\u0152\u0152\u01AA\u00f0\u00f0\u00f0\u00f0\u00f0\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0062\u0062\u00b4\u00b4\u0266\u008a\u0268\u026A\u026A\u026A\u026A\u026C\u026C\u026C" +
                "\u026C\u026A\u026A\u026A\u00f4\u026C\u026C\u026C\u00f4\u026A\u026A\u026A\u026A" +
                "\u026C\u026C\u026C\u026C\u026A\u026A\u026A\u026A\u026C\u026C\u026C\u026C\u026A" +
                "\u026A\u026A\u00f4\u026C\u026C\u026C\u00f4\u026E\u026E\u026E\u026E\u0270\u0270" +
                "\u0270\u0270\u026A\u026A\u026A\u026A\u026C\u026C\u026C\u026C\u0272\u0274\u0274" +
                "\u0276\u0278\u027A\u027C\u00f4\u00b4\u00b4\u00b4\u00b4\u027E\u027E\u027E\u027E\u00b4\u00b4" +
                "\u00b4\u00b4\u027E\u027E\u027E\u027E\u00b4\u00b4\u00b4\u00b4\u027E\u027E\u027E\u027E\u026A" +
                "\u00b4\u025A\u00b4\u026C\u0280\u0282\u0284\u00e6\u00b4\u025A\u00b4\u0286\u0286\u0282" +
                "\u00e6\u026A\u00b4\u00f4\u00b4\u026C\u0288\u028A\u00e6\u026A\u00b4\u028C\u00b4\u026C\u028E" +
                "\u0290\u00e6\u00f4\u00b4\u025A\u00b4\u0292\u0294\u0282\u0296\u0298\u0298\u0298\u029A" +
                "\u0298\u029C\u029E\u02A0\u02A2\u02A2\u02A2\u0010\u02A4\u02A6\u02A4\u02A6\u0010" +
                "\u0010\u0010\u0010\u02A8\u02AA\u02AA\u02AC\u02AE\u02AE\u02B0\u0010\u02B2\u02B4\u02B6" +
                "\u02B8\u02BA\u0010\u02BC\u02BE\u02C0\u0010\u0010\u0010\u0010\u02C2\u02BA\u0010\u0010\u0010" +
                "\u0010\u02C4\u02C6\u02C6\u02C8\u02AA\u02AA\u02C6\u02C6\u02C6\u02CA\u00f4\u0048\u0048" +
                "\u0048\u02CC\u02CE\u02D0\u02D2\u02D2\u02D2\u02D2\u02D2\u02CC\u02CE\u023A\u00de" +
                "\u00de\u00de\u00de\u00de\u00de\u02D4\u00f4\u003a\u003a\u003a\u003a\u003a\u003a\u003a\u003a\u003a\u003a\u003a" +
                "\u003a\u003a\u003a\u003a\u003a\u02D6\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f0\u00f0\u00f0\u00f0\u00f0" +
                "\u00f0\u02D8\u0132\u02DA\u02DC\u02DA\u00f0\u00f0\u00f0\u00f0\u00f0\u019A\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u016E\u02DE\u016E\u02E0\u016E\u02E2\u0118\u008a\u0118\u02E4\u02E0" +
                "\u016E\u02E6\u0118\u0118\u016E\u016E\u02E8\u02DE\u02EA\u02DE\u021C\u0118\u02EC" +
                "\u0118\u02EE\u0092\u0096\u02F0\u016E\u008a\u0118\u001e\u0166\u02F2\u008a\u008a\u02F4\u016E" +
                "\u02F6\u0052\u0052\u0052\u0052\u0052\u0052\u0052\u0052\u02F8\u02F8\u02F8\u02F8\u02F8\u02F8" +
                "\u02FA\u02FA\u02FC\u02FC\u02FC\u02FC\u02FC\u02FC\u02FE\u02FE\u0300\u0302\u0304" +
                "\u0300\u0306\u016E\u00f4\u00f4\u0166\u0166\u0308\u030A\u030A\u0166\u016E\u016E" +
                "\u030C\u02F4\u016E\u030C\u030E\u02E8\u016E\u030C\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u0166\u016E\u030C" +
                "\u030C\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u0166\u0166\u0166\u0166\u0166\u0166\u0310\u0312\u001e\u0166" +
                "\u0312\u0312\u0312\u0166\u0310\u0314\u0310\u001e\u0166\u0312\u0312\u0310\u0312" +
                "\u001e\u001e\u001e\u0166\u0310\u0312\u0312\u0312\u0312\u0166\u0166\u0310\u0310\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u001e\u0166\u0166\u0312\u0312\u0166" +
                "\u0166\u0166\u0166\u0310\u001e\u001e\u0312\u0312\u0312\u0312\u0166\u0312\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312" +
                "\u001e\u0310\u0312\u001e\u0166\u0166\u001e\u0166\u0166\u0166\u0166\u0312\u0166\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u001e\u0166\u0166\u0312\u0166" +
                "\u0166\u0166\u0166\u0310\u0312\u0312\u0166\u0312\u0166\u0166\u0312\u0312\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0166\u0312\u0312\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u016E\u016E\u016E\u016E\u0012\u0012\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u0316\u016E\u016E\u0312\u016E\u016E\u016E\u0318" +
                "\u031A\u016E\u016E\u016E\u016E\u016E\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u031C\u030C\u016E\u016E\u016E\u016E\u016E\u031E\u016E\u016E\u016E\u016E" +
                "\u016E\u0320\u016E\u016E\u02F4\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u0166\u0166\u0166\u0166\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u030E\u016E\u016E\u016E\u016E\u016E\u016E\u0166\u0166" +
                "\u0166\u016E\u016E\u016E\u0322\u0316\u0324\u030A\u0324\u0326\u016E\u016E\u030A" +
                "\u02E8\u016E\u016E\u016E\u016E\u016E\u01E8\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u016E\u016E\u016E\u016E\u016E\u01E8\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u0328\u0328\u0328\u0328\u0328\u0328\u0328\u0328\u0328\u0328" +
                "\u032A\u032A\u032A\u032A\u032A\u032A\u032A\u032A\u032A\u032A\u032C\u032C\u032C" +
                "\u032C\u032C\u032C\u032C\u032C\u032C\u032C\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u032E\u032E\u032E\u032E\u032E\u032E" +
                "\u0330\u032E\u032E\u032E\u032E\u032E\u032E\u0332\u0332\u0332\u0332\u0332\u0332" +
                "\u0332\u0332\u0332\u0332\u0332\u0332\u0332\u0334\u0336\u0336\u0336\u0336\u0338" +
                "\u033A\u033A\u033A\u033A\u033C\u016E\u016E\u016E\u016E\u016E\u030A\u016E\u016E" +
                "\u016E\u016E\u016E\u033E\u016E\u016E\u016E\u016E\u033E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u0166\u0340\u0342" +
                "\u0344\u030A\u030A\u0346\u0348\u034A\u034A\u034A\u0346\u034C\u031E\u0316\u034A" +
                "\u0346\u034A\u034E\u034A\u0346\u030A\u034A\u0346\u034A\u0346\u034A\u030A\u034A" +
                "\u034A\u034A\u034A\u030A\u0346\u034A\u034A\u0346\u0346\u034A\u034A\u0316\u0316" +
                "\u0316\u0316\u0316\u0316\u034A\u034A\u034A\u034A\u034A\u034C\u0346\u034C\u034C" +
                "\u0346\u0346\u034A\u034A\u0350\u034A\u034A\u034A\u034A\u034A\u034C\u034A\u0326" +
                "\u034A\u034A\u034A\u016E\u016E\u016E\u016E\u016E\u034A\u0326\u030A\u030A\u034C" +
                "\u034C\u0346\u034A\u0326\u034A\u034A\u034C\u034A\u0316\u0352\u034A\u030A\u034A" +
                "\u034A\u034A\u034A\u034A\u0354\u0356\u034A\u034A\u0316\u034A\u0346\u034A\u034A" +
                "\u0324\u034C\u034C\u0356\u034A\u034A\u034A\u034A\u034A\u034A\u034A\u034A\u034A" +
                "\u034C\u0356\u034A\u034A\u030A\u0316\u0326\u034C\u0358\u0356\u0354\u034A\u034A" +
                "\u0346\u0354\u016E\u030A\u035A\u035C\u034C\u034A\u02E8\u02E8\u02E8\u016E\u016E" +
                "\u030E\u016E\u030E\u016E\u016E\u016E\u035E\u016E\u016E\u016E\u016E\u030E\u02E8" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u02E8\u030E\u016E\u016E\u035E\u035E" +
                "\u016E\u0322\u0316\u0322\u016E\u016E\u016E\u016E\u016E\u030E\u0346\u034A\u0012" +
                "\u0012\u0012\u0012\u0012\u0012\u0012\u0360\u0360\u0360\u0360\u0360\u0328\u0328\u0328\u0328" +
                "\u0328\u0362\u0362\u0362\u0362\u0362\u0322\u0316\u016E\u016E\u016E\u016E\u030E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u035E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u0322\u001e\u0310\u0364\u0366\u0312\u0310\u0312\u0166\u0166\u0310\u0312" +
                "\u001e\u0166\u0166\u0312\u001e\u0166\u0312\u0312\u0012\u0012\u0012\u0012\u0012\u0166\u0166" +
                "\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u0166\u0166\u0166\u0166\u0166\u0166\u0368\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u02CE\u036A\u036A\u036A\u036A\u036A\u036A\u036A\u036A\u036A\u036A\u0366\u0310" +
                "\u0312\u0312\u001e\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0166\u0166\u0166" +
                "\u0166\u001e\u0166\u0166\u0166\u0312\u0312\u0312\u0166\u0310\u0166\u0166\u0312" +
                "\u0312\u001e\u0312\u0166\u0012\u0012\u001e\u0166\u0310\u0310\u0312\u0166\u0312\u0166" +
                "\u0166\u0166\u0166\u0166\u0312\u0312\u0312\u0166\u0012\u0166\u0166\u0166\u0166" +
                "\u0166\u0166\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u001e\u0312" +
                "\u0312\u0166\u001e\u001e\u0310\u0310\u0312\u001e\u0166\u0166\u0312\u0166\u0166\u0166" +
                "\u0312\u001e\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u0310\u001e\u0166\u0166\u0166\u0166\u0166\u0312\u0166\u0166\u0312\u0312\u0310" +
                "\u001e\u0310\u001e\u0166\u0310\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312" +
                "\u0166\u0312\u0312\u0312\u0312\u0310\u0312\u0312\u0312\u0312\u0312\u0312\u0312" +
                "\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u0312\u001e" +
                "\u0166\u0166\u001e\u001e\u0166\u0312\u0312\u001e\u0166\u0166\u0312\u001e\u0166\u0310" +
                "\u0166\u0310\u0312\u0312\u0310\u0166\u016E\u016E\u030E\u030A\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u0322\u035E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166\u0166" +
                "\u0166\u030C\u02F4\u0166\u0166\u030C\u016E\u035E\u016E\u0322\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u00f4" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u014C\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u036C\u013A\u013A" +
                "\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A" +
                "\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u013A\u0144\u0144\u0144\u0144" +
                "\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0144" +
                "\u0144\u0144\u0144\u0144\u0144\u0144\u0144\u0062\u021C\u00ac\u00ae\u0068\u0068\u036E" +
                "\u021C\u0268\u0062\u0066\u0076\u008a\u008a\u00de\u021C\u0062\u0062\u0370\u016E\u016E\u0372" +
                "\u0068\u0374\u00f0\u0062\u00f4\u00f4\u0376\u0010\u0378\u0010\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4" +
                "\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u00b4\u037A\u00f4\u00f4\u037A\u00f4\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00f4\u00f4\u00f4\u013E\u01DC\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u0150\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u00f4" +
                "\u00f4\u00f4\u00f4\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA\u0096\u0096" +
                "\u0096\u01CA\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152\u0152" +
                "\u0152\u0152\u0152\u0152\u0152\u0010\u037C\u037C\u0010\u02B2\u02B4\u037C\u0010\u0010" +
                "\u0010\u0010\u037E\u0010\u0246\u037C\u0010\u037C\u0012\u0012\u0012\u0012\u0010\u0010\u0380\u0010" +
                "\u0010\u0010\u0010\u0010\u02A2\u0010\u0010\u0246\u0382\u0010\u0010\u0010\u0010\u0010\u0010\u016E\u0010" +
                "\u0384\u036A\u036A\u036A\u0386\u00f4\u016E\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u014C\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u016E\u000a" +
                "\u0010\u0388\u038A\u0012\u0012\u0012\u0012\u0012\u016E\u0012\u0012\u0012\u0012\u038C\u038E\u0390" +
                "\u0392\u0392\u0392\u0392\u00f0\u00f0\u0210\u0394\u00e4\u00e4\u016E\u0396\u0398\u039A" +
                "\u016E\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u0150\u039C\u039E" +
                "\u03A0\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u03A2\u00e4\u03A0" +
                "\u00f4\u00f4\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u01CA\u020A\u03A4\u03A4\u020A\u020A\u020A\u020A\u020A\u016E\u016E" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u014C\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u031C\u01E8" +
                "\u03A6\u03A6\u03A6\u03A6\u03A6\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u03A8\u03AA\u01D2\u01D2\u03AC\u03AE" +
                "\u03AE\u03AE\u03AE\u03AE\u0052\u0052\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u016E\u0320\u03A6\u03A6\u03A6\u03A6" +
                "\u03A6\u020A\u020A\u020A\u020A\u020A\u020A\u03B0\u03B0\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u03B2\u0052\u0052\u0052\u0052\u0052\u0052\u0052" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u016E\u016E\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u031C\u016E\u0320\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u016E\u020A\u020A\u020A\u020A" +
                "\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u020A\u031C\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u024C\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u00f4\u016E\u016E\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u016E\u01E8\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u03B6\u0010\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u020E\u020E\u020E\u020E\u020E\u0096\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u01C0\u0132\u03B8\u0152\u0152\u0152\u0152\u00f0" +
                "\u03BA\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u00de\u0152\u0096" +
                "\u0096\u0096\u03BC\u03BC\u03BC\u03BC\u03BE\u00f0\u0140\u0140\u0140\u00f4\u00f4\u00f4\u00f4" +
                "\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u00e6\u03C0\u00e8\u00e8\u00e8\u00e8\u00e6\u0062" +
                "\u0062\u0062\u0062\u0062\u0062\u0062\u008a\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062" +
                "\u0062\u0062\u0062\u0260\u008a\u008a\u008a\u0066\u0068\u036E\u0062\u0062\u0062\u0062\u0062\u03C2\u03C4" +
                "\u036E\u0092\u0062\u0062\u03C6\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u021C\u021C" +
                "\u0268\u021C\u021C\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u0062\u03C8\u00a8\u0068\u03CA\u00f4" +
                "\u00f4\u0062\u03CC\u03CC\u0062\u0062\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00de\u03CE\u03D0\u00de\u0092\u0096\u0096\u0096\u01E2\u0096\u01B8\u0096\u01C6\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01F8\u01BC\u01B6\u016E\u016E\u019A\u00f4\u01D2" +
                "\u01D2\u01D2\u020A\u03D2\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0010\u0010\u00f4\u00f4\u00f4\u00f4\u01BA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01BA\u01BA\u01BA" +
                "\u01BA\u01BA\u01BA\u01BA\u01BA\u017C\u00f4\u00f4\u00f4\u00f4\u0140\u0206\u0206\u0206" +
                "\u0206\u0206\u00f4\u00f4\u00f4\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u0096\u0096\u0096\u0140" +
                "\u01F2\u01F2\u01C6\u020E\u020E\u020E\u020E\u020E\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0152\u0152\u01AA\u00f0\u0140\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u01C6\u0152\u0152\u0152\u0152\u0152\u03D4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u01EA\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u01CA\u00f4\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01C0\u01BA\u0152\u0152\u01BA" +
                "\u0152\u01BA\u0242\u0140\u0140\u0140\u0140\u0140\u0140\u013E\u0206\u0206\u0206" +
                "\u0206\u0206\u00f4\u00f4\u0140\u0096\u0096\u01C6\u03A0\u0096\u0096\u0096\u0096\u0206\u0206" +
                "\u0206\u0206\u0206\u0096\u0096\u01CA\u0096\u0096\u0096\u0096\u01C6\u0152\u0152\u01B6\u01BC" +
                "\u01B6\u01BC\u01CC\u00f4\u00f4\u00f4\u00f4\u0096\u01C6\u0096\u0096\u0096\u0096\u01B6\u00f4\u0206" +
                "\u0206\u0206\u0206\u0206\u00f4\u0140\u0140\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u03A0" +
                "\u0096\u0096\u01FA\u020A\u01F8\u01B6\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u01E2" +
                "\u0152\u01E2\u01C6\u01E2\u0096\u0096\u01AA\u01C0\u01CA\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u01C8\u024C\u0140\u0096\u0096\u0096\u0096\u0096\u01F8\u0152\u01BA" +
                "\u0140\u024C\u03D6\u019A\u00f4\u00f4\u00f4\u00f4\u01C8\u0096\u0096\u01CA\u01C8\u0096\u0096" +
                "\u01CA\u01C8\u0096\u0096\u01CA\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u01CA\u0096\u0096\u0096\u01CA" +
                "\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u008a\u00c6\u008a" +
                "\u008a\u008a\u03D8\u00de\u00de\u008a\u008a\u008a\u008a\u0264\u00e6\u00f4\u00f4\u00b4\u00b4\u00b4\u00b4\u00b4" +
                "\u00b4\u00b4\u00b4\u0096\u01F8\u01BC\u01BA\u01B6\u03DA\u0258\u00f4\u0206\u0206\u0206" +
                "\u0206\u0206\u00f4\u00f4\u00f4\u0096\u0096\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u01CA\u00f4\u01C8\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00f4\u00f4\u03DC" +
                "\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC\u03DC" +
                "\u03DC\u03DC\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE\u03DE" +
                "\u03DE\u03DE\u03DE\u03DE\u03DE\u03B4\u03B4\u03B4\u03B4\u03B4\u03E0\u03B4\u03B4" +
                "\u03B4\u03E2\u03B4\u03B4\u03E4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03E6\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03E8\u03EA\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u03EC\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u00f4\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4" +
                "\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u03B4\u00f4\u00f4\u00f4\u00b4\u00b4\u00b4\u025A\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u037A\u00b4\u00b4\u00f4\u00f4\u015E\u03EE\u015A\u015A\u015A\u015A" +
                "\u03F0\u015A\u015A\u015A\u015A\u015A\u015A\u015C\u015A\u015A\u015C\u015C\u015A" +
                "\u015E\u015C\u015A\u015A\u015A\u015A\u015A\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u03F2\u03F2" +
                "\u03F2\u03F2\u03F2\u03F2\u03F2\u03F2\u03F4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u019C" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u03F6\u016E\u016E\u016E\u016E" +
                "\u016E\u016E\u016E\u016E\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u00f4\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u00f4\u00f4\u00f4\u014C\u00f4\u00f4\u00f4\u00f4\u00f4" +
                "\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u00f4\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u03F8\u016E\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u03FA\u0010\u0010\u0010\u03FC\u03FE" +
                "\u00f4\u00f4\u00f4\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u00f0\u037E\u0400\u0402\u03F6\u03F6" +
                "\u03F6\u03F6\u03F6\u03F6\u03F6\u03FE\u03FC\u03FE\u0010\u02B8\u0404\u001c\u0406" +
                "\u0408\u0010\u040A\u036A\u036A\u040C\u0010\u040E\u0312\u0410\u0412\u02B0\u00f4\u00f4" +
                "\u0176\u0176\u01AE\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176" +
                "\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u0176\u01AE\u0414\u0376\u0416" +
                "\u000e\u0010\u0012\u0418\u0016\u0018\u0192\u0192\u0192\u0192\u0192\u001c\u001e\u0020\u002c\u002e" +
                "\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u002e\u0030\u0032\u02CE\u02BE\u0012\u004c\u0096" +
                "\u0096\u0096\u0096\u0096\u03A0\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096" +
                "\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u0096\u00e4\u00f4\u0096\u0096\u0096\u00f4\u0096\u0096\u0096\u00f4" +
                "\u0096\u0096\u0096\u00f4\u0096\u01CA\u00f4\u003a\u041A\u014E\u02D6\u02F4\u0166\u030C\u01E8" +
                "\u00f4\u00f4\u00f4\u00f4\u041C\u041E\u016E\u00f4").toCharArray()

        // The A table has 1056 entries for a total of 4224 bytes.
        val A: IntArray = IntArray(1056)
        const val A_DATA: String = "\u4800\u100F\u4800\u100F\u4800\u100F\u5800\u400F\u5000\u400F\u5800\u400F\u6000" +
                "\u400F\u5000\u400F\u5000\u400F\u5000\u400F\u6000\u400C\u6800\u0018\u6800\u0018" +
                "\u2800\u0018\u2800\u601A\u2800\u0018\u6800\u0018\u6800\u0018\uE800\u0015\uE800\u0016\u6800" +
                "\u0018\u2000\u0019\u3800\u0018\u2000\u0014\u3800\u0018\u3800\u0018\u1800\u3609\u1800\u3609" +
                "\u3800\u0018\u6800\u0018\uE800\u0019\u6800\u0019\uE800\u0019\u6800\u0018\u6800\u0018\u0082" +
                "\u7FE1\u0082\u7FE1\u0082\u7FE1\u0082\u7FE1\uE800\u0015\u6800\u0018\uE800\u0016\u6800\u001b" +
                "\u6800\u5017\u6800\u001b\u0081\u7FE2\u0081\u7FE2\u0081\u7FE2\u0081\u7FE2\uE800\u0015\u6800" +
                "\u0019\uE800\u0016\u6800\u0019\u4800\u100F\u4800\u100F\u5000\u100F\u3800\u000c\u6800" +
                "\u0018\u2800\u601A\u2800\u601A\u6800\u001c\u6800\u0018\u6800\u001b\u6800\u001c\u0000\u7005" +
                "\uE800\u001d\u6800\u0019\u4800\u1010\u6800\u001c\u6800\u001b\u2800\u001c\u2800\u0019\u1800" +
                "\u060B\u1800\u060B\u6800\u001b\u07FD\u7002\u6800\u0018\u6800\u0018\u6800\u001b\u1800" +
                "\u050B\u0000\u7005\uE800\u001e\u6800\u080B\u6800\u080B\u6800\u080B\u6800\u0018\u0082" +
                "\u7001\u0082\u7001\u0082\u7001\u6800\u0019\u0082\u7001\u07FD\u7002\u0081\u7002\u0081\u7002" +
                "\u0081\u7002\u6800\u0019\u0081\u7002\u061D\u7002\u0006\u7001\u0005\u7002\u07FF\uF001" +
                "\u03A1\u7002\u0000\u7002\u0006\u7001\u0005\u7002\u0006\u7001\u0005\u7002\u07FD\u7002" +
                "\u061E\u7001\u0006\u7001\u04F5\u7002\u034A\u7001\u033A\u7001\u0006\u7001\u0005\u7002" +
                "\u0336\u7001\u0336\u7001\u0006\u7001\u0005\u7002\u0000\u7002\u013E\u7001\u032A\u7001" +
                "\u032E\u7001\u0006\u7001\u033E\u7001\u067D\u7002\u034E\u7001\u0346\u7001\u0575" +
                "\u7002\u0000\u7002\u034E\u7001\u0356\u7001\u05F9\u7002\u035A\u7001\u036A\u7001" +
                "\u0006\u7001\u0005\u7002\u036A\u7001\u0000\u7002\u0000\u7002\u0005\u7002\u0366\u7001" +
                "\u0366\u7001\u0006\u7001\u0005\u7002\u036E\u7001\u0000\u7002\u0000\u7005\u0000\u7002" +
                "\u0721\u7002\u0000\u7005\u0000\u7005\u000a\uF001\u0007\uF003\u0009\uF002\u000a\uF001\u0007" +
                "\uF003\u0009\uF002\u0009\uF002\u0006\u7001\u0005\u7002\u013D\u7002\u07FD\u7002\u000a" +
                "\uF001\u067E\u7001\u0722\u7001\u05FA\u7001\u0000\u7002\u07FE\u7001\u0006\u7001" +
                "\u0005\u7002\u0576\u7001\u07FE\u7001\u07FD\u7002\u07FD\u7002\u0006\u7001\u0005\u7002" +
                "\u04F6\u7001\u0116\u7001\u011E\u7001\u07FD\u7002\u07FD\u7002\u07FD\u7002\u0349" +
                "\u7002\u0339\u7002\u0000\u7002\u0335\u7002\u0335\u7002\u0000\u7002\u0329\u7002" +
                "\u0000\u7002\u032D\u7002\u07FD\u7002\u0000\u7002\u0335\u7002\u07FD\u7002\u0000\u7002" +
                "\u033D\u7002\u0000\u7002\u07FD\u7002\u0345\u7002\u034D\u7002\u0000\u7002\u034D" +
                "\u7002\u0355\u7002\u0000\u7002\u0000\u7002\u0359\u7002\u0369\u7002\u0000\u7002\u07FD" +
                "\u7002\u0369\u7002\u0369\u7002\u0115\u7002\u0365\u7002\u0365\u7002\u011D\u7002" +
                "\u0000\u7002\u036D\u7002\u0000\u7002\u0000\u7005\u0000\u7002\u0000\u7004\u0000\u7004\u0000" +
                "\u7004\u6800\u7004\u6800\u7004\u0000\u7004\u0000\u7004\u0000\u7004\u6800\u001b\u6800" +
                "\u001b\u6800\u7004\u6800\u7004\u0000\u7004\u6800\u001b\u6800\u7004\u6800\u001b\u0000" +
                "\u7004\u6800\u001b\u4000\u3006\u4000\u3006\u4000\u3006\u46B1\u3006\u7800\u0000" +
                "\u7800\u0000\u0000\u7004\u05F9\u7002\u05F9\u7002\u05F9\u7002\u6800\u0018\u01D2\u7001" +
                "\u009a\u7001\u6800\u0018\u0096\u7001\u0096\u7001\u0096\u7001\u7800\u0000\u0102\u7001\u7800" +
                "\u0000\u00fe\u7001\u00fe\u7001\u07FD\u7002\u0082\u7001\u7800\u0000\u0082\u7001\u0099\u7002" +
                "\u0095\u7002\u0095\u7002\u0095\u7002\u07FD\u7002\u0081\u7002\u007d\u7002\u0081\u7002\u0101" +
                "\u7002\u00fd\u7002\u00fd\u7002\u0022\u7001\u00f9\u7002\u00e5\u7002\u0000\u7001\u0000\u7001" +
                "\u0000\u7001\u00bd\u7002\u00d9\u7002\u0021\u7002\u0159\u7002\u0141\u7002\u07E5\u7002" +
                "\u01D1\u7002\u0712\u7001\u0181\u7002\u6800\u0019\u0006\u7001\u0005\u7002\u07E6\u7001" +
                "\u0000\u7002\u05FA\u7001\u05FA\u7001\u05FA\u7001\u0142\u7001\u0142\u7001\u0141" +
                "\u7002\u0141\u7002\u0000\u001c\u4000\u3006\u4000\u0007\u4000\u0007\u003e\u7001\u0006\u7001" +
                "\u0005\u7002\u003d\u7002\u7800\u0000\u00c2\u7001\u00c2\u7001\u00c2\u7001\u00c2\u7001\u7800" +
                "\u0000\u7800\u0000\u0000\u7004\u0000\u0018\u0000\u0018\u0000\u7002\u00c1\u7002\u00c1\u7002\u00c1" +
                "\u7002\u00c1\u7002\u07FD\u7002\u0000\u7002\u0000\u0018\u6800\u0014\u7800\u0000\u7800\u0000" +
                "\u6800\u001c\u6800\u001c\u2800\u601A\u7800\u0000\u4000\u3006\u4000\u3006\u4000\u3006" +
                "\u0800\u0014\u4000\u3006\u0800\u0018\u4000\u3006\u4000\u3006\u0800\u0018\u0800\u7005" +
                "\u0800\u7005\u0800\u7005\u7800\u0000\u7800\u0000\u0800\u7005\u0800\u7005\u0800" +
                "\u0018\u0800\u0018\u7800\u0000\u3000\u1010\u3000\u1010\u6800\u0019\u6800\u0019\u1000" +
                "\u0019\u2800\u0018\u2800\u0018\u1000\u601A\u3800\u0018\u1000\u0018\u6800\u001c\u6800\u001c" +
                "\u4000\u3006\u1000\u0018\u1000\u1010\u1000\u0018\u1000\u0018\u1000\u0018\u1000\u7005" +
                "\u1000\u7005\u1000\u7004\u1000\u7005\u1000\u7005\u4000\u3006\u4000\u3006\u4000" +
                "\u3006\u3000\u3409\u3000\u3409\u2800\u0018\u3000\u0018\u3000\u0018\u1000\u0018\u4000" +
                "\u3006\u1000\u7005\u1000\u0018\u1000\u7005\u4000\u3006\u3000\u1010\u6800\u001c" +
                "\u4000\u3006\u4000\u3006\u1000\u7004\u1000\u7004\u4000\u3006\u4000\u3006\u6800" +
                "\u001c\u1800\u3609\u1800\u3609\u1000\u7005\u1000\u001c\u1000\u001c\u1000\u7005\u7800" +
                "\u0000\u1000\u1010\u4000\u3006\u7800\u0000\u7800\u0000\u1000\u7005\u0800\u3409\u0800" +
                "\u3409\u0800\u7005\u4000\u3006\u0800\u7004\u0800\u7004\u0800\u7004\u7800\u0000" +
                "\u0800\u601A\u0800\u601A\u0800\u7004\u4000\u3006\u4000\u3006\u4000\u3006\u0800" +
                "\u0018\u0800\u0018\u1000\u7005\u7800\u0000\u1000\u001b\u1000\u7005\u1000\u7005\u1000" +
                "\u7004\u3000\u1010\u4000\u3006\u4000\u3006\u0000\u3008\u4000\u3006\u0000\u7005" +
                "\u0000\u3008\u0000\u3008\u0000\u3008\u4000\u3006\u0000\u3008\u4000\u3006\u0000\u7005" +
                "\u4000\u3006\u0000\u3749\u0000\u3749\u0000\u0018\u0000\u7004\u0000\u7005\u4000\u3006\u7800" +
                "\u0000\u0000\u7005\u0000\u7005\u7800\u0000\u4000\u3006\u7800\u0000\u7800\u0000\u0000\u3008" +
                "\u0000\u3008\u7800\u0000\u0000\u080B\u0000\u080B\u0000\u080B\u0000\u06EB\u0000\u001c\u2800" +
                "\u601A\u0000\u7005\u0000\u0018\u7800\u0000\u4000\u3006\u0000\u0018\u7800\u0000\u0000\u0018\u2800" +
                "\u601A\u0000\u001c\u0000\u7005\u4000\u3006\u0000\u7005\u0000\u074B\u0000\u080B\u0000\u080B" +
                "\u6800\u001c\u6800\u001c\u7800\u0000\u7800\u0000\u0000\u0018\u6800\u050B\u6800\u050B\u6800" +
                "\u04AB\u6800\u04AB\u6800\u04AB\u0000\u001c\u0000\u0018\u0000\u7005\u0000\u3008\u0000\u3006" +
                "\u0000\u3006\u0000\u3008\u0000\u7005\u0000\u3008\u0000\u7005\u0000\u001c\u0000\u080B\u0000\u7005" +
                "\u0000\u080B\u0000\u001c\u7800\u0000\u2800\u601A\u0000\u7004\u4000\u3006\u4000\u3006" +
                "\u0000\u0018\u0000\u3609\u0000\u3609\u0000\u7004\u7800\u0000\u0000\u001c\u0000\u001c\u0000\u0018\u0000" +
                "\u001c\u0000\u3409\u0000\u3409\u0000\u3008\u0000\u3008\u4000\u3006\u0000\u001c\u0000\u001c\u7800" +
                "\u0000\u0000\u001c\u0000\u0018\u4000\u3006\u0000\u3008\u0000\u3008\u0000\u7005\u07FE\u7001" +
                "\u07FE\u7001\u7800\u0000\u07FE\u7001\u07FD\uF002\u07FD\uF002\u07FD\uF002\u0000" +
                "\u0018\u0000\u7004\u07FD\uF002\u0000\u0018\u0000\u070B\u0000\u070B\u0000\u070B\u0000\u070B" +
                "\u0000\u042B\u0000\u054B\u0000\u080B\u0000\u080B\u7800\u0000\u0022\u7001\u0022\u7001\u0021" +
                "\u7002\u0021\u7002\u6800\u0014\u0000\u7005\u6000\u400C\u0000\u7005\u0000\u7005\uE800" +
                "\u0015\uE800\u0016\u7800\u0000\u0000\u746A\u0000\u746A\u0000\u746A\u0000\u7005\u4000\u3006" +
                "\u0000\u3008\u0000\u3008\u0000\u0018\u6800\u060B\u6800\u060B\u6800\u0014\u6800\u0018\u6800" +
                "\u0018\u4000\u3006\u4800\u1010\u4000\u3006\u0000\u7005\u0000\u7004\u0000\u7005\u4000" +
                "\u3006\u4000\u3006\u0000\u7005\u0000\u04EB\u7800\u0000\u4000\u0007\u4000\u3006\u0000" +
                "\u3008\u0000\u7005\u0000\u3008\u4000\u3006\u07FD\u7002\u7800\u0000\u07FE\u7001\u7800" +
                "\u0000\u0000\u7005\u0000\u3008\u0000\u7004\u0000\u7002\u0000\u7004\u07FD\u7002\u0000\u7002" +
                "\u0000\u7004\u07FD\u7002\u00ed\u7002\u07FE\u7001\u0000\u7002\u07E1\u7002\u07E1\u7002" +
                "\u07E2\u7001\u07E2\u7001\u07FD\u7002\u07E1\u7002\u7800\u0000\u07E2\u7001\u06D9" +
                "\u7002\u06D9\u7002\u06A9\u7002\u06A9\u7002\u0671\u7002\u0671\u7002\u0601\u7002" +
                "\u0601\u7002\u0641\u7002\u0641\u7002\u0609\u7002\u0609\u7002\u07FF\uF003\u07FF" +
                "\uF003\u06DA\u7001\u06DA\u7001\u07FF\uF003\u6800\u001b\u07FD\u7002\u6800\u001b" +
                "\u06AA\u7001\u06AA\u7001\u0672\u7001\u0672\u7001\u7800\u0000\u6800\u001b\u07FD" +
                "\u7002\u07E5\u7002\u0642\u7001\u0642\u7001\u07E6\u7001\u6800\u001b\u0602\u7001" +
                "\u0602\u7001\u060A\u7001\u060A\u7001\u6800\u001b\u7800\u0000\u6000\u400C\u6000" +
                "\u400C\u6000\u400C\u6000\u000c\u6000\u400C\u4800\u1010\u4800\u1010\u4800\u1010" +
                "\u0000\u1010\u0800\u1010\u6800\u0014\u6800\u0014\u6800\u001d\u6800\u001e\u6800\u0015\u6800" +
                "\u001d\u6000\u400D\u5000\u400E\u7800\u1010\u7800\u1010\u7800\u1010\u3800\u000c" +
                "\u2800\u0018\u2800\u0018\u2800\u0018\u6800\u0018\u6800\u0018\uE800\u001d\uE800\u001e\u6800" +
                "\u0018\u6800\u0018\u6800\u0018\u6800\u0018\u6800\u5017\u6800\u5017\u6800\u0018\u3800" +
                "\u0019\uE800\u0015\uE800\u0016\u6800\u0018\u6800\u0018\u6800\u0018\u6800\u0019\u6800\u0018" +
                "\u6800\u0018\u6000\u400C\u4800\u1010\u4800\u1010\u4800\u1010\u7800\u0000\u1800" +
                "\u060B\u0000\u7004\u2000\u0019\u2000\u0019\u6800\u0019\uE800\u0015\uE800\u0016\u0000\u7004" +
                "\u1800\u040B\u1800\u040B\u0000\u7004\u7800\u0000\u2800\u601A\u7800\u0000\u4000\u3006" +
                "\u4000\u0007\u4000\u0007\u4000\u3006\u4000\u0007\u4000\u0007\u0000\u7001\u6800\u001c\u6800" +
                "\u001c\u0000\u7001\u0000\u7002\u0000\u7001\u0000\u7001\u0000\u7002\u6800\u0019\u0000\u7001" +
                "\u6800\u001c\u6800\u001c\u07FE\u7001\u6800\u001c\u2800\u001c\u0000\u7002\u0072\u7001\u0000" +
                "\u7001\u0000\u7005\u0000\u7002\u6800\u0019\u0000\u7001\u6800\u001c\u6800\u0019\u0071\u7002" +
                "\u0000\u001c\u0042\u742A\u0042\u742A\u0042\u780A\u0042\u780A\u0041\u762A\u0041\u762A\u0041\u780A" +
                "\u0041\u780A\u0000\u780A\u0000\u780A\u0000\u780A\u0006\u7001\u0005\u7002\u0000\u742A\u0000" +
                "\u780A\u6800\u06EB\u6800\u0019\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u0019\u6800" +
                "\u001c\u6800\u001c\u6800\u001c\u6800\u0019\uE800\u0019\uE800\u0019\uE800\u0019\u2000\u0019" +
                "\u2800\u0019\u6800\u001c\u6800\u001c\u6800\u001c\uE800\u0015\uE800\u0016\u6800\u001c\u0000" +
                "\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u0000\u001c\u6800\u001c\u6800\u001c\u6800" +
                "\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u042B\u6800\u042B\u6800\u05AB\u6800" +
                "\u05AB\u1800\u072B\u1800\u072B\u006a\u001c\u006a\u001c\u006a\u001c\u006a\u001c\u0069\u001c\u0069" +
                "\u001c\u6800\u06CB\u6800\u040B\u6800\u040B\u6800\u040B\u6800\u040B\u6800\u058B" +
                "\u6800\u058B\u6800\u058B\u6800\u058B\u6800\u042B\u6800\u001c\u6800\u0019\u6800" +
                "\u0019\u6800\u0019\u6800\u0019\u6800\u0019\u6800\u0019\u6800\u0019\u6800\u001c\u6800\u001c" +
                "\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800" +
                "\u001c\u6800\u001c\u6800\u0019\u0000\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800" +
                "\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c\u6800\u001c" +
                "\u6800\u001c\u6800\u056B\u6800\u056B\u6800\u06EB\u6800\u06EB\uE800\u0019\uE800" +
                "\u0015\uE800\u0016\u6800\u0019\u6800\u0019\u6800\u0019\uE800\u0016\uE800\u0015\uE800\u001c" +
                "\u6800\u001c\u0005\u7002\u07FE\u7001\u0000\u7002\u6800\u001c\u6800\u001c\u0006\u7001\u0005" +
                "\u7002\u4000\u3006\u7800\u0000\u6800\u0018\u6800\u0018\u6800\u080B\u7800\u0000\u07FD" +
                "\u7002\uE800\u001d\uE800\u001e\u6800\u0018\u6800\u0014\u6800\u0018\u6800\u7004\u6800" +
                "\u0015\u6800\u0018\u6800\u0018\uE800\u0015\uE800\u0016\u6800\u0014\u6800\u001c\u0000\u7004" +
                "\u0000\u7005\u0000\u772A\u6800\u0014\u6800\u0015\u6800\u0016\u6800\u0016\u6800\u001c\u0000" +
                "\u740A\u0000\u740A\u0000\u740A\u6800\u0014\u0000\u7004\u0000\u764A\u0000\u776A\u0000\u748A" +
                "\u0000\u7004\u0000\u7005\u6800\u0018\u4000\u3006\u6800\u001b\u6800\u001b\u0000\u7004\u0000" +
                "\u7004\u0000\u7005\u0000\u7005\u6800\u0018\u0000\u05EB\u0000\u05EB\u0000\u042B\u0000\u042B" +
                "\u0000\u044B\u0000\u056B\u0000\u068B\u0000\u080B\u6800\u001c\u6800\u048B\u6800\u048B" +
                "\u6800\u048B\u0000\u001c\u0000\u001c\u0000\u001c\u6800\u080B\u0000\u7005\u0000\u7005\u0000\u7004" +
                "\u6800\u0018\u4000\u0007\u6800\u0018\u6800\u0018\u6800\u7004\u0000\u776A\u0000\u776A\u0000" +
                "\u776A\u0000\u762A\u6800\u001b\u6800\u7004\u6800\u7004\u0000\u001b\u0000\u001b\u0006\u7001" +
                "\u0741\u7002\u0000\u7002\u0742\u7001\u07FE\u7001\u0005\u7002\u7800\u0000\u7800\u0000" +
                "\u0000\u7002\u0000\u7004\u0006\u7001\u0005\u7002\u0000\u7005\u2800\u601A\u2800\u001c\u0000" +
                "\u3008\u0000\u3008\u0000\u7004\u0000\u3008\u0000\u7002\u0000\u001b\u0000\u3008\u0000\u0018\u0000" +
                "\u0013\u0000\u0013\u0000\u0012\u0000\u0012\u0000\u7005\u0000\u7705\u0000\u7005\u0000\u76E5\u0000\u7545" +
                "\u0000\u7005\u0000\u75C5\u0000\u7005\u0000\u7005\u0000\u76A5\u0000\u7005\u0000\u7665\u0000" +
                "\u7005\u0000\u75A5\u4000\u3006\u0800\u7005\u0800\u7005\u2000\u0019\u1000\u001b\u1000" +
                "\u001b\u1000\u001b\u7800\u0000\u6800\u0016\u6800\u0015\u1000\u601A\u6800\u001c\u4000\u3006" +
                "\u4000\u3006\u6800\u0018\u6800\u0015\u6800\u0016\u6800\u0018\u6800\u0014\u6800\u5017" +
                "\u6800\u5017\u6800\u0015\u6800\u5017\u6800\u5017\u3800\u0018\u7800\u0000\u6800\u0018" +
                "\u3800\u0018\u6800\u0014\uE800\u0015\uE800\u0016\u2800\u0018\u2000\u0019\u2000\u0014\u6800" +
                "\u0019\u7800\u0000\u6800\u0018\u2800\u601A\u7800\u0000\u4800\u1010\u6800\u0018\u2800" +
                "\u0018\u6800\u0018\u2000\u0019\u6800\u0019\u6800\u001b\u7800\u0000\u6800\u1010\u6800\u1010" +
                "\u6800\u1010"

        // The B table has 1056 entries for a total of 2112 bytes.
        val B: CharArray = ("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0440\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0440\u0000\u0000\u0000\u0000\u0000\u0460\u0460\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0032\u0032\u0032\u0032\u0000\u0000\u0000\u0000\u0020\u0000\u0031\u0031\u0031\u0031\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0031\u0000\u0000\u0000\u0840\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0031\u0000\u0020\u0000\u0000\u0031\u0000\u0000\u0000\u0000\u0000\u0032\u0032\u0032\u0000\u0032" +
                "\u0031\u0031\u0031\u0031\u0000\u0031\u0031\u0032\u0031\u0032\u0031\u0031\u0032\u0031\u0032\u0031\u0031\u0032\u0032" +
                "\u0031\u0032\u0032\u0032\u0031\u0032\u0032\u0032\u0031\u0031\u0032\u0032\u0032\u0032\u0032\u0031\u0032\u0032\u0031" +
                "\u0031\u0032\u0032\u0031\u0032\u0032\u0032\u0031\u0032\u0031\u0031\u0031\u0032\u0032\u0032\u0031\u0032\u0031\u0030" +
                "\u0031\u0031\u0030\u0030\u0032\u0030\u0031\u0032\u0030\u0031\u0031\u0032\u0031\u0031\u0031\u0032\u0032\u0032\u0032" +
                "\u0031\u0032\u0032\u0031\u0032\u0032\u0031\u0031\u0032\u0031\u0032\u0032\u0032\u0031\u0031\u0031\u0031\u0031\u0031" +
                "\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031" +
                "\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0030\u0031\u0031\u0031" +
                "\u0031\u0030\u0030\u0030\u0030\u0030\u0000\u0000\u0030\u0030\u0031\u0000\u0030\u0000\u0030\u0000\u0020\u0020\u0020" +
                "\u0025\u0000\u0000\u0031\u0031\u0031\u0031\u0000\u0032\u0032\u0020\u0032\u0032\u0032\u0000\u0032\u0000\u0032\u0032" +
                "\u0031\u0032\u0000\u0032\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0032\u0031\u0031\u0032" +
                "\u0032\u0032\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0032\u0031\u0000\u0032\u0031\u0032\u0031\u0032\u0032\u0032" +
                "\u0032\u0032\u0031\u0031\u0000\u0020\u0000\u0000\u0032\u0032\u0031\u0031\u0000\u0032\u0032\u0032\u0032\u0000\u0000" +
                "\u0030\u0000\u0000\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0020" +
                "\u0024\u0024\u0000\u0024\u0000\u0024\u0024\u0000\u0030\u0030\u0030\u0000\u0000\u0030\u0030\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0024\u0000\u0000\u0000\u0000\u0000\u0030\u0030" +
                "\u0030\u0030\u0030\u0024\u0020\u0024\u0020\u0020\u0000\u0000\u0000\u0000\u0024\u0030\u0000\u0030\u0024\u0000\u0000" +
                "\u0020\u0024\u0030\u0030\u0024\u0024\u0000\u0020\u0020\u0030\u0000\u0000\u0030\u0000\u0000\u0020\u0000\u0000\u0030" +
                "\u0020\u0020\u0030\u0020\u0030\u0030\u0030\u0000\u0000\u0000\u0030\u0024\u0024\u0020\u0000\u0000\u0030\u0000\u0000" +
                "\u0030\u0030\u0030\u0000\u0024\u0024\u0024\u0020\u0030\u0024\u0024\u0024\u0024\u0024\u0020\u0030\u0020\u0020\u0020" +
                "\u0000\u0030\u0030\u0024\u0000\u0030\u0030\u0000\u0024\u0000\u0000\u0024\u0024\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0030\u0000\u0000\u0024\u0000\u0000\u0000\u0000\u0000\u0030\u0024\u0030\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0030\u0024\u0024\u0024\u0024\u0030\u0024\u0030\u0000\u0000" +
                "\u0030\u0000\u0000\u0000\u0000\u0030\u0020\u0020\u0000\u0020\u0020\u0030\u0000\u0000\u0000\u0000\u0000\u0020\u0020" +
                "\u0020\u0020\u0020\u0000\u0000\u0000\u0000\u0000\u0020\u0024\u0024\u0030\u0032\u0032\u0000\u0032\u0031\u0031\u0031" +
                "\u0000\u0031\u0031\u0000\u0020\u0020\u0020\u0000\u0000\u0000\u0000\u0000\u0000\u0032\u0032\u0031\u0031\u0000\u0030" +
                "\u0000\u0030\u0030\u0000\u0000\u0000\u0030\u0030\u0030\u0030\u0020\u0020\u0020\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0020\u0000\u0020\u0030\u0030\u0030\u0034\u0034\u0030\u0020\u0000\u0000\u0024\u0020\u0030\u0020\u0020\u0031\u0000" +
                "\u0032\u0000\u0030\u0020\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0032\u0031\u0031\u0031\u0032\u0032\u0031" +
                "\u0031\u0000\u0032\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0031\u0030\u0030\u0032\u0032" +
                "\u0030\u0000\u0031\u0000\u0032\u0032\u0032\u0032\u0000\u0000\u0031\u0031\u0032\u0032\u0032\u0000\u0032\u0032\u0032" +
                "\u0032\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0020\u0420\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0000\u0000" +
                "\u0020\u0020\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0031\u0000\u0000\u0000\u0000\u0000\u0031\u0000\u0000\u0031\u0000\u0000\u0000\u0020\u0000\u0000\u0020\u0000\u0400" +
                "\u0032\u0000\u0000\u0032\u0031\u0032\u0032\u0031\u0030\u0032\u0840\u0000\u0032\u0000\u0030\u0031\u0032\u0032" +
                "\u0030\u0871\u0000\u0032\u0000\u0000\u0031\u0000\u0032\u0032\u0032\u0032\u0031\u0031\u0031\u0031\u0030\u0030" +
                "\u0030\u0032\u0031\u0030\u0030\u0000\u0840\u0840\u0840\u0840\u0000\u0000\u0000\u0840\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u08C0\u08C0\u0840\u0000\u0000\u0000\u0000\u0000\u0800\u0000\u0000\u0000\u0000" +
                "\u08C0\u08C0\u0840\u0840\u08C0\u0000\u0000\u0000\u0000\u0000\u0000\u0006\u0006\u0846\u0006\u0005" +
                "\u0005\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0000\u0000\u0840\u0840\u08C0" +
                "\u08C0\u0000\u0840\u0800\u0000\u0800\u0800\u0800\u0800\u0840\u0800\u0A40\u0800" +
                "\u0800\u0800\u0800\u0800\u08C0\u08C0\u0800\u0840\u0A40\u0AC0\u0AC0\u0A40\u0A40" +
                "\u08C0\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0840\u0000\u0000\u0000\u0000\u0031" +
                "\u0032\u0031\u0000\u0000\u0032\u0031\u0020\u0000\u0000\u0000\u0000\u0000\u0031\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0030\u0038\u0038\u0000\u0000\u0000\u0000\u0000\u0038\u0038\u0038\u0840" +
                "\u0030\u0038\u0038\u0038\u0030\u0030\u0840\u0020\u0030\u0030\u0030\u0030\u0030\u0030\u0020\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0840\u0000\u0000\u0038\u0038\u0030\u0000\u0000" +
                "\u0000\u0000\u0030\u0030\u0030\u0030\u0030\u0000\u0030\u0030\u0000\u0000\u0032\u0031\u0031\u0032\u0032\u0031\u0000" +
                "\u0000\u0031\u0031\u0032\u0031\u0030\u0000\u0000\u0024\u0020\u0030\u0024\u0031\u0000\u0024\u0000\u0000\u0000\u0000" +
                "\u0000\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0038\u0024\u0030\u0030\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0020\u0420\u0000\u0000\u0000\u0000\u0000\u0020\u0020\u0000" +
                "\u0020\u0020\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000").toCharArray()

        // In all, the character property tables require 20416 bytes.
        init {
            run {
                // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
                val data = A_DATA.toCharArray()
                assert(data.size == (1056 * 2))
                var i = 0
                var j = 0
                while (i < (1056 * 2)) {
                    val entry = data[i++].code shl 16
                    A!![j++] = entry or data[i++].code
                }
            }
        }
    }
}