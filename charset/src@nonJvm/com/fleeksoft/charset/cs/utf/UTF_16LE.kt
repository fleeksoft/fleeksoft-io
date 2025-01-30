/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
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

package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.cs.Unicode
import com.fleeksoft.charset.cs.UnicodeDecoder
import com.fleeksoft.charset.cs.UnicodeEncoder

class UTF_16LE : Unicode("UTF-16LE") {

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : UnicodeDecoder(cs, LITTLE)

    private class Encoder(cs: Charset) : UnicodeEncoder(cs, LITTLE, false)

    companion object {
        val INSTANCE: UTF_16LE = UTF_16LE()
    }
}
