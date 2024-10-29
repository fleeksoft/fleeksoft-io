package com.fleeksoft.io

actual typealias ByteBuffer = java.nio.ByteBuffer

actual fun ByteBuffer.duplicateExt(): ByteBuffer = this.duplicate()