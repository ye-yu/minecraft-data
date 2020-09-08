package fp.yeyu.mcdata

import fp.yeyu.mcdata.interfaces.ByteQueue
import io.netty.buffer.ByteBuf
import io.netty.buffer.UnpooledByteBufAllocator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.ByteBuffer
import java.util.*
import kotlin.NoSuchElementException

/**
 * Publisher cycle:
 *  - push
 *  - publish
 *
 * Consumer cycle:
 *  - consume
 *  - pop
 * */
object LogRingBuffer : ByteQueue {

    var readerPointer = 0
    var writerPointer = 0
    var warn = true

    private val ringBufferSize: Int by ConfigFile
    private val useHeapBuffer: Boolean by ConfigFile
    private val bufferInitialCapacity: Int by ConfigFile
    private val ring: Array<VariableByteBuf>
    private val logger: Logger = LogManager.getLogger()

    class RingBufferSizeException(size: Int) : NegativeArraySizeException("Ring buffer cannot be less than 2! Got $size instead")

    init {
        if (ringBufferSize < 2) throw RingBufferSizeException(ringBufferSize)
        ring = Array(ringBufferSize) { VariableByteBuf(getBuffer()) }
    }

    private fun getBuffer(): ByteBuf {
        return if (useHeapBuffer) {
            UnpooledByteBufAllocator.DEFAULT.heapBuffer(bufferInitialCapacity)
        } else {
            UnpooledByteBufAllocator.DEFAULT.directBuffer(bufferInitialCapacity)
        }
    }

    override fun push(d: Double) {
        ring[writerPointer].push(d)
    }

    override fun push(l: Long) {
        ring[writerPointer].push(l)
    }

    override fun push(i: Int) {
        ring[writerPointer].push(i)
    }

    override fun push(s: Short) {
        ring[writerPointer].push(s.toInt())
    }

    override fun push(byte: Byte) {
        ring[writerPointer].push(byte.toInt())
    }

    override fun push(enum: Enum<*>) {
        ring[writerPointer].push(enum)
    }

    override fun push(uuid: UUID) {
        ring[writerPointer].push(uuid)
    }

    override fun push(b: Boolean) {
        ring[writerPointer].push(b)
    }

    override fun toByteBuffer(): ByteBuffer = ring[readerPointer].toByteBuffer()
    override fun popDouble(): Double = ring[readerPointer].popDouble()
    override fun popLong(): Long = ring[readerPointer].popLong()
    override fun popInt(): Int = ring[readerPointer].popInt()
    override fun popShort(): Short = ring[readerPointer].popShort()
    override fun popByte(): Byte = ring[readerPointer].popByte()
    override fun popBoolean(): Boolean = ring[readerPointer].popBoolean()
    override fun <T : Enum<T>?> popEnum(e: Class<T>): T = ring[readerPointer].popEnum(e)
    override fun popUUID(): UUID = ring[readerPointer].popUUID()


    fun hasNext(): Boolean = readerPointer != writerPointer

    fun publish() {
        val next = (writerPointer + 1) % ringBufferSize
        if (next != readerPointer) {
            logger.info("Published $writerPointer, now ready for $next")
            writerPointer = next
        } else if (warn) {
            warn = false
            logger.warn("Reader too slow! Writer may overwrite next publish. Set a larger ring size?")
        }
    }

    fun consume() {
        if (readerPointer == writerPointer) throw NoSuchElementException("Writer has not published the next value yet.")
        logger.info("Consuming $readerPointer")
        flushReader()
        readerPointer = (++readerPointer) % ringBufferSize
    }

    private fun flushReader() {
        ring[readerPointer].clear()
    }

    override fun clear() {
        writerPointer = 0
        readerPointer = 0
        ring.forEach(VariableByteBuf::clear)
    }
}