package com.fleeksoft.io.bytearrayinputstream

import com.fleeksoft.io.ByteArrayInputStream
import kotlin.random.Random

/* @test
 * @library /test/lib
 * @build jdk.test.lib.RandomFactory
 * @run main ReadAllReadNTransferTo
 * @bug 6766844 8180451
 * @summary Verify ByteArrayInputStream readAllBytes, readNBytes, and transferTo
 * @key randomness
 */
object ReadAllReadNTransferTo {
    private const val SIZE = 0x4d4d

    private val random: Random = Random

    fun main(vararg args: String?) {
        val buf = ByteArray(SIZE)
        random.nextBytes(buf)
        val position: Int = random.nextInt(SIZE / 2)
        val size: Int = random.nextInt(SIZE - position)

        var bais: ByteArrayInputStream = ByteArrayInputStream(buf)
        bais.readAllBytes()
        if (bais.read(ByteArray(0)) != -1) {
            throw RuntimeException("read(byte[]) did not return -1")
        }
        if (bais.read(ByteArray(1), 0, 0) != -1) {
            throw RuntimeException("read(byte[],int,int) did not return -1")
        }

        bais = ByteArrayInputStream(buf, position, size)
        val off = if (size < 2) 0 else random.nextInt(size / 2)
        val len = if (size - off < 1) 0 else random.nextInt(size - off)

        val bN = ByteArray(off + len)
        if (bais.readNBytes(bN, off, len) != len) {
            throw RuntimeException("readNBytes return value")
        }
        if (!bN.copyOfRange(off, off + len).contentEquals(buf.copyOfRange(position, position + len))) {
            throw RuntimeException("readNBytes content")
        }

        val bAll: ByteArray = bais.readAllBytes()
        requireNotNull(bAll) { "readAllBytes return value" }
        if (bAll.size != size - len) {
            throw RuntimeException("readAllBytes return value length")
        }
        val rangeToCompare = buf.copyOfRange(position + len, position + len + bAll.size)

        if (!bAll.contentEquals(rangeToCompare)) {
            throw RuntimeException("readAllBytes content")
        }

        bais = ByteArrayInputStream(buf)
        /*val baos: ByteArrayOutputStream = ByteArrayOutputStream(buf.size)
        if (bais.transferTo(baos) !== buf.size) {
            throw RuntimeException("transferTo return value length")
        }
        if (!Arrays.equals(buf, baos.toByteArray())) {
            throw RuntimeException("transferTo content")
        }*/
    }
}
