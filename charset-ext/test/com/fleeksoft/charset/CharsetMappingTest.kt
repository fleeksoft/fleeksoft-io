package com.fleeksoft.charset

import com.fleeksoft.charset.internal.CharsetNameMapping
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CharsetMappingTest {
    @Test
    fun testStandardCharsetsMapping() {
        CharsetNameMapping.standardCharsetMapKeys.keys.forEach { key ->
            val charset = Charsets.forName(key)
            assertNotNull(charset)
            val stdKey = CharsetNameMapping.standardCharsetMapKeys[key]
            assertEquals(charset.name(), stdKey)

            val nameKey = Charsets.normalizeCharsetName(charset.name())
            assertEquals(stdKey, CharsetNameMapping.standardCharsetMapKeys[nameKey])
        }
    }


    @Test
    fun testExtendedCharsetsMapping() {
        CharsetNameMapping.extendedCharsetMapKeys.keys.forEach { key ->
            val charset = Charsets.forName(key)
            assertNotNull(charset)
            val extKey = CharsetNameMapping.extendedCharsetMapKeys[key]
            assertEquals(charset.name(), extKey)

            val nameKey = Charsets.normalizeCharsetName(charset.name())
            assertEquals(extKey, CharsetNameMapping.extendedCharsetMapKeys[nameKey])
        }
    }
}