package com.fleeksoft.io.stringreader

import com.fleeksoft.io.StringReader
import com.fleeksoft.io.exception.IOException
import kotlin.test.Test

/*
 * Copyright (c) 1998, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
/* @test
   @bug 4090254
   @summary Test StringReader ready method*/
/**
 * This class tests to see if StringReader generates
 * an exception if ready is called on closed stream
 */
class ClosedReady {
    @Test
    fun main() {
        val `in`: StringReader = StringReader("aaaaaaaaaaaaaaa")
        `in`.read()
        `in`.close()

        try {
            `in`.ready() // IOException should be thrown here
            throw RuntimeException(" No exception during read on closed stream")
        } catch (e: IOException) {
            println("Test passed: IOException is thrown")
        }
    }
}
