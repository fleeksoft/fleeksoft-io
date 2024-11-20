package com.fleeksoft.charset.internal

internal object ArraysSupport {
    const val SOFT_MAX_ARRAY_LENGTH = Int.MAX_VALUE - 8

    fun newLength(oldLength: Int, minGrowth: Int, prefGrowth: Int): Int {
        // Preconditions not checked because of inlining
        // require(oldLength >= 0) { "Old length must be >= 0" }
        // require(minGrowth > 0) { "Minimum growth must be > 0" }

        val prefLength = oldLength + maxOf(minGrowth, prefGrowth) // might overflow
        return if (prefLength in 1..SOFT_MAX_ARRAY_LENGTH) {
            prefLength
        } else {
            // Call a separate method for huge length calculation
            hugeLength(oldLength, minGrowth)
        }
    }

    private fun hugeLength(oldLength: Int, minGrowth: Int): Int {
        val minLength = oldLength + minGrowth
        if (minLength < 0) { // overflow
            throw Exception("Required array length $oldLength + $minGrowth is too large")
        }
        return when {
            minLength <= SOFT_MAX_ARRAY_LENGTH -> SOFT_MAX_ARRAY_LENGTH
            else -> minLength
        }
    }
}