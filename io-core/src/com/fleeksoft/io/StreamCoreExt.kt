package com.fleeksoft.io

public inline fun String.byteInputStream(): ByteArrayInputStream =
    ByteArrayInputStream(this.encodeToByteArray())