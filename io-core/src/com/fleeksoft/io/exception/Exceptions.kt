package com.fleeksoft.io.exception

import kotlin.Exception

expect open class IOException : Exception {
    constructor()
    constructor(msg: String)
}

expect open class EOFException : IOException {
    constructor()
    constructor(msg: String)
}

expect abstract class VirtualMachineError : Error


expect class OutOfMemoryError : VirtualMachineError {
    constructor()
    constructor(msg: String)
}

expect class ArrayIndexOutOfBoundsException : IndexOutOfBoundsException {
    constructor()
    constructor(msg: String)
}

expect class ReadOnlyBufferException() : UnsupportedOperationException
expect open class CharacterCodingException() : IOException
expect class BufferUnderflowException() : RuntimeException
expect class BufferOverflowException() : RuntimeException
expect class MalformedInputException(inputLength: Int) : CharacterCodingException
expect class UnmappableCharacterException(inputLength: Int)
expect class CoderMalfunctionError(cause: Exception) : Error

// FIXME: extending RuntimeException failing in expect
class UncheckedIOException : RuntimeException {
    constructor(message: String, cause: IOException) : super(message, cause)
    constructor(cause: IOException) : super(cause)
}