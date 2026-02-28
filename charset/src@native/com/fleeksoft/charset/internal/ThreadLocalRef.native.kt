package com.fleeksoft.charset.internal

import kotlin.concurrent.AtomicInt
import kotlin.native.concurrent.ThreadLocal

actual open class ThreadLocalRef<T> actual constructor() {
    private val threadLocalId = ThreadLocalIdCounter.nextThreadLocalId()

    actual fun remove() {
        ThreadLocalState.threadLocalMap.remove(threadLocalId)
    }

    actual fun get(): T? = if (ThreadLocalState.threadLocalMap.containsKey(threadLocalId)) {
        ThreadLocalState.threadLocalMap[threadLocalId] as T
    } else {
        null
    }

    actual fun set(value: T?) {
        if (value == null) {
            remove()
        } else {
            ThreadLocalState.threadLocalMap[threadLocalId] = value
        }
    }
}

@ThreadLocal
private object ThreadLocalState {
    val threadLocalMap = HashMap<Int, Any>()
}

private object ThreadLocalIdCounter {
    val threadLocalId = AtomicInt(0)
    fun nextThreadLocalId(): Int = threadLocalId.addAndGet(1)
}