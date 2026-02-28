package com.fleeksoft.charset.internal

actual open class ThreadLocalRef<T> actual constructor() {
    private var localValue: T? = null

    actual fun remove() {
        value = null
    }

    actual fun get(): T? = localValue

    actual fun set(value: T?) {
        localValue = value
    }
}