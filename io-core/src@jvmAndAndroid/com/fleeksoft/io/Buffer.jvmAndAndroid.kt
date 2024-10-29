package com.fleeksoft.io

actual typealias Buffer = java.nio.Buffer


actual fun Buffer.setPositionExt(pos: Int) {
    this.position(pos)
}

actual fun Buffer.setLimitExt(newLimit: Int) {
    this.limit(newLimit)
}

actual fun Buffer.clearExt() {
    this.clear()
}

actual fun Buffer.flipExt() {
    this.flip()
}

actual fun Buffer.rewindExt() {
    this.rewind()
}

actual fun Buffer.markExt() {
    this.mark()
}

actual fun Buffer.resetExt() {
    this.reset()
}

actual fun Buffer.sliceExt() {
    this.slice()
}

actual fun Buffer.sliceExt(index: Int, length: Int) {
    this.slice(index, length)
}