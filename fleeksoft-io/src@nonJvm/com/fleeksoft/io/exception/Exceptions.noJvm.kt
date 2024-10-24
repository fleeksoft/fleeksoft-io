package com.fleeksoft.io.exception

actual class IOException : Exception {
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

actual class ArrayIndexOutOfBoundsException: IndexOutOfBoundsException {
    actual constructor() : super()
    actual constructor(msg: String) : super(msg)
}