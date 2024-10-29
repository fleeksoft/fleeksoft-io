package com.fleeksoft.io

expect abstract class Buffer {
    fun remaining(): Int
    fun capacity(): Int
    fun limit(): Int

    abstract fun isReadOnly(): Boolean
    fun position(): Int
    fun hasRemaining(): Boolean
    abstract fun arrayOffset(): Int

    abstract fun array(): Any
    abstract fun hasArray(): Boolean

    // FIXME: issue on android in ByteBuffer and CharBuffer these functions return Buffer but in JVM it return self class
//    open fun position(pos: Int): Buffer
//    open fun limit(newLimit: Int): Buffer
//    open fun clear(): Buffer
//    open fun flip(): Buffer
//    open fun rewind(): Buffer
//    open fun mark(): Buffer
//    open fun reset(): Buffer
//    abstract fun duplicate(): Buffer
//    abstract fun slice(): Buffer
//    abstract fun slice(index: Int, length: Int): Buffer
}

expect fun Buffer.setPositionExt(pos: Int)
expect fun Buffer.setLimitExt(newLimit: Int)
expect fun Buffer.clearExt()
expect fun Buffer.flipExt()
expect fun Buffer.rewindExt()
expect fun Buffer.markExt()
expect fun Buffer.resetExt()
expect fun Buffer.sliceExt()
expect fun Buffer.sliceExt(index: Int, length: Int)