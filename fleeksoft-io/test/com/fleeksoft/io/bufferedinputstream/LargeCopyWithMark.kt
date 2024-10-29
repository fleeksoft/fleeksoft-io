package com.fleeksoft.io.bufferedinputstream

/*class LargeCopyWithMark {
    val BUFF_SIZE: Int = 8192
    val BIS_BUFF_SIZE: Int = Int.MAX_VALUE / 2 + 100
    val BYTES_TO_COPY: Long = 2L * Int.MAX_VALUE

    init {
        val cond: Boolean = BIS_BUFF_SIZE * 2 < 0
        require(!cond) { "doubling must overflow" }
    }

    @Test
    @Test fun main() {
        val buff = ByteArray(BUFF_SIZE)

        MyInputStream(BYTES_TO_COPY).use { myis ->
            BufferedInputStream(myis, BIS_BUFF_SIZE).use { bis ->
                MyOutputStream().use { myos ->

                    // will require a buffer bigger than BIS_BUFF_SIZE
                    bis.mark(BIS_BUFF_SIZE + 100)
                    while (true) {
                        val count: Int = bis.read(buff, 0, BUFF_SIZE)
                        if (count == -1) break
                        myos.write(buff, 0, count)
                    }
                }
            }
        }
    }
}

private class MyInputStream(private var bytesLeft: Long) : InputStream() {
    override fun read(): Int {
        return 0
    }

    @Override
    
    fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (bytesLeft <= 0) return -1
        val result: Long = Math.min(bytesLeft, len.toLong())
        bytesLeft -= result
        return result.toInt()
    }

    @Override
    
    fun available(): Int {
        return if (bytesLeft > 0) 1 else 0
    }
}

private class MyOutputStream : OutputStream() {
    @Override
    
    fun write(b: Int) {
    }

    @Override
    
    fun write(b: ByteArray?) {
    }

    @Override
    
    fun write(b: ByteArray?, off: Int, len: Int) {
    }
}*/
