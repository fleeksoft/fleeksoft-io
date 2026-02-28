package com.fleeksoft.charset.internal


expect open class ThreadLocalRef<T>() {
    fun get(): T?
    fun set(value: T?)
    fun remove()
}

var <T> ThreadLocalRef<T>.value: T?
    get() = get()
    set(value) {
        set(value)
    }