package com.fleeksoft.io.exception

expect class IOException : Exception {
    constructor()
    constructor(msg: String)
}

expect abstract class VirtualMachineError : Error


expect class OutOfMemoryError : VirtualMachineError {
    constructor()
    constructor(msg: String)
}