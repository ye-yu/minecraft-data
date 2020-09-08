package fp.yeyu.mcdata

import fp.yeyu.mcdata.interfaces.ByteQueue
import io.netty.buffer.PooledByteBufAllocator
import net.minecraft.network.PacketByteBuf
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import kotlin.NoSuchElementException

object LogRingBuffer : ByteQueue {

    var readerPointer = 0
    var writerPointer = 0
    var warn = true

    private val ringBufferSize: Int by ConfigFile
    private val ring: Array<PacketByteBuf>
    private val logger: Logger = LogManager.getLogger()

    class RingBufferSizeException(size: Int) : NegativeArraySizeException("Ring buffer cannot be less than 2! Got $size instead")

    init {
        if (ringBufferSize < 2) throw RingBufferSizeException(ringBufferSize)
        ring = Array(ringBufferSize) { PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer()) }
    }

    override fun push(d: Double) {
        ring[writerPointer].writeDouble(d)
    }

    override fun push(l: Long) {
        ring[writerPointer].writeVarLong(l)
    }

    override fun push(i: Int) {
        ring[writerPointer].writeVarInt(popInt())
    }
    override fun push(s: Short) {
        ring[writerPointer].writeShort(s.toInt())
    }

    override fun push(byte: Byte) {
        ring[writerPointer].writeByte(byte.toInt())
    }

    override fun push(enum: Enum<*>) {
        ring[writerPointer].writeEnumConstant(enum)
    }

    override fun push(uuid: UUID) {
        ring[writerPointer].writeUuid(uuid)
    }

    override fun push(b: Boolean) {
        ring[writerPointer].writeBoolean(b)
    }

    override fun popDouble(): Double = ring[readerPointer].readDouble()
    override fun popLong(): Long = ring[readerPointer].readVarLong()
    override fun popInt(): Int = ring[readerPointer].readVarInt()
    override fun popShort(): Short = ring[readerPointer].readShort()
    override fun popByte(): Byte = ring[readerPointer].readByte()
    override fun popBoolean(): Boolean = ring[readerPointer].readBoolean()
    override fun <T : Enum<T>?> popEnum(e: Class<T>): T = ring[readerPointer].readEnumConstant(e)
    override fun popUUID(): UUID = ring[readerPointer].readUuid()


    fun hasNext(): Boolean = readerPointer != writerPointer

    fun publish() {
        if ((writerPointer + 1) != readerPointer) {
            writerPointer = (writerPointer++) % ringBufferSize
        } else if (warn) {
            warn = false
            logger.warn("Reader too slow! Writer may overwrite next publish. Set a larger ring size?")
        }
    }

    fun consume() {
        if (readerPointer == writerPointer) throw NoSuchElementException("Writer has not published the next value yet.")
        ring[readerPointer].clear()
        readerPointer = (readerPointer++) % ringBufferSize
    }
}