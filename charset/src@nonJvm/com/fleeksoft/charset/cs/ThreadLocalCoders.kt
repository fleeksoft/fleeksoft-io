package com.fleeksoft.charset.cs

import com.fleeksoft.charset.internal.ThreadLocal
import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.internal.assert
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.Charsets


object ThreadLocalCoders {
    private const val CACHE_SIZE = 3

    private val decoderCache: Cache = object : Cache(CACHE_SIZE) {
        override fun hasName(ob: Any, name: Any): Boolean {
            if (name is Charset) return (ob as CharsetDecoder).charset() == name
            if (name is String) return ((ob as CharsetDecoder).charset().name() == name)
            return false
        }

        override fun create(name: Any): Any? {
            if (name is Charset) return name.newDecoder()
            if (name is String) return Charsets.forName(name).newDecoder()
            assert(false)
            return null
        }
    }

    fun decoderFor(name: Any): CharsetDecoder {
        val cd: CharsetDecoder = decoderCache.forName(name) as CharsetDecoder
        cd.reset()
        return cd
    }

    private val encoderCache: Cache = object : Cache(CACHE_SIZE) {
        override fun hasName(ob: Any, name: Any): Boolean {
            if (name is Charset) return (ob as CharsetEncoder).charset() == name
            if (name is String) return ((ob as CharsetEncoder).charset().name() == name)
            return false
        }

        override fun create(name: Any): Any? {
            if (name is Charset) return name.newEncoder()
            if (name is String) return Charsets.forName((name as String?).toString()).newEncoder()
            assert(false)
            return null
        }
    }

    fun encoderFor(name: Any): CharsetEncoder {
        val ce: CharsetEncoder = encoderCache.forName(name) as CharsetEncoder
        ce.reset()
        return ce
    }

    private abstract class Cache(private val size: Int) {
        // Thread-local reference to array of cached objects, in LRU order
        private val cache: ThreadLocal<Array<Any?>?> = ThreadLocal { null }

        abstract fun create(name: Any): Any?

        fun moveToFront(oa: Array<Any?>, i: Int) {
            val ob: Any? = oa[i]
            for (j in i downTo 1) oa[j] = oa[j - 1]
            oa[0] = ob
        }

        abstract fun hasName(ob: Any, name: Any): Boolean

        fun forName(name: Any): Any? {
            var oa: Array<Any?>? = cache.get()
            if (oa == null) {
                oa = arrayOfNulls<Any>(size)
                cache.setValue(oa)
            } else {
                for (i in oa.indices) {
                    val ob: Any? = oa[i]
                    if (ob == null) continue
                    if (hasName(ob, name)) {
                        if (i > 0) moveToFront(oa, i)
                        return ob
                    }
                }
            }

            // Create a new object
            val ob: Any? = create(name)
            oa[oa.size - 1] = ob
            moveToFront(oa, oa.size - 1)
            return ob
        }
    }
}