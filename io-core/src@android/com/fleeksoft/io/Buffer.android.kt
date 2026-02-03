package com.fleeksoft.io

import android.os.Build

actual typealias Buffer = java.nio.Buffer


actual fun Buffer.setPositionExt(pos: Int): Buffer {
    return this.position(pos)
}

actual fun Buffer.setLimitExt(newLimit: Int): Buffer {
    return this.limit(newLimit)
}

actual fun Buffer.clearExt(): Buffer {
    return this.clear()
}

actual fun Buffer.flipExt(): Buffer {
    return this.flip()
}

actual fun Buffer.rewindExt(): Buffer {
    return this.rewind()
}

actual fun Buffer.markExt(): Buffer {
    return this.mark()
}

actual fun Buffer.resetExt(): Buffer {
    return this.reset()
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
actual fun Buffer.sliceExt(): Buffer {
    if (this is CharBuffer) return this.slice()
    if (this is ByteBuffer) return this.slice()
    return this.slice()
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
actual fun Buffer.sliceExt(index: Int, length: Int): Buffer {
    return this.slice(index, length)
}