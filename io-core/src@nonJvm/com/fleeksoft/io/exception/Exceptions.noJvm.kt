package com.fleeksoft.io.exception

import kotlin.Exception
import kotlin.IndexOutOfBoundsException

actual open class IOException : Exception {
    actual constructor() : super()
    actual constructor(msg: String) : super(msg)
}

actual open class EOFException : IOException {
    actual constructor() : super()
    actual constructor(msg: String) : super(msg)
}

actual abstract class VirtualMachineError : Error {
    constructor() : super()
    constructor(msg: String) : super(msg)
}

actual class OutOfMemoryError : VirtualMachineError {
    actual constructor() : super()
    actual constructor(msg: String) : super(msg)
}

actual class ArrayIndexOutOfBoundsException : IndexOutOfBoundsException {
    actual constructor() : super()
    actual constructor(msg: String) : super(msg)
}

actual class ReadOnlyBufferException actual constructor() : UnsupportedOperationException()
actual open class CharacterCodingException actual constructor() : IOException()
actual class BufferUnderflowException actual constructor() : RuntimeException()
actual class BufferOverflowException actual constructor() : RuntimeException()
actual class MalformedInputException actual constructor(inputLength: Int) : CharacterCodingException() {
    override val message: String? = "Input length = $inputLength"
}

actual class UnmappableCharacterException actual constructor(inputLength: Int) : Exception() {
    override val message: String? = "Input length = $inputLength"
}

actual class CoderMalfunctionError actual constructor(cause: Exception) : Error(cause)

/*
actual class UncheckedIOException : RuntimeException {
    actual constructor(message: String, cause: IOException) : super(message, cause)
    actual constructor(cause: IOException) : super(cause)
}*/


actual class URISyntaxException : Exception {
    private var _input: String = ""
    private var _index: Int = -1

    actual constructor(input: String, reason: String, index: Int) : super(reason) {
        if (index < -1) {
            throw IllegalArgumentException()
        } else {
            this._input = input
            this._index = index
        }
    }

    actual fun getInput(): String = _input
    actual fun getIndex(): Int = _index

    actual constructor(input: String, reason: String) : this(input, reason, -1)

    actual fun getReason(): String {
        return super.message.toString()
    }

    override val message: String?
        get() {
            val sb = StringBuilder()
            sb.append(this.getReason())
            if (this._index > -1) {
                sb.append(" at index ")
                sb.append(this._index)
            }

            sb.append(": ")
            sb.append(this._input)
            return sb.toString()
        }
}