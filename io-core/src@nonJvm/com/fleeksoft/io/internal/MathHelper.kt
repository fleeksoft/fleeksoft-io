package com.fleeksoft.io.internal

internal object MathHelper {
    fun addExact(a: Long, b: Long): Long {
        return if (a > 0 && b > Long.MAX_VALUE - a) {
            throw ArithmeticException("Overflow")
        } else if (a < 0 && b < Long.MIN_VALUE - a) {
            throw ArithmeticException("Underflow")
        } else {
            a + b
        }
    }
}