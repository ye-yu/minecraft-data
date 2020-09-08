package fp.yeyu.mcdata

import fp.yeyu.mcdata.interfaces.ByteQueue
import io.netty.buffer.ByteBuf
import net.minecraft.network.PacketByteBuf
import java.nio.ByteBuffer
import java.util.*

class VariableByteBuf(val parent: ByteBuf) : ByteQueue {

    private val nettyBuffer = PacketByteBuf(parent)

    override fun push(d: Double) {
        nettyBuffer.writeDouble(d)
    }

    override fun push(l: Long) {
        nettyBuffer.writeVarLong(l)
    }

    override fun push(i: Int) {
        nettyBuffer.writeVarInt(i)
    }

    override fun push(s: Short) {
        nettyBuffer.writeShort(s.toInt())
    }

    override fun push(b: Byte) {
        nettyBuffer.writeByte(b.toInt())
    }

    override fun push(b: Boolean) {
        nettyBuffer.writeBoolean(b)
    }

    override fun push(e: Enum<*>?) {
        nettyBuffer.writeEnumConstant(e)
    }

    override fun push(uuid: UUID?) {
        nettyBuffer.writeUuid(uuid)
    }

    override fun popDouble(): Double {
        return nettyBuffer.readDouble()
    }

    override fun popLong(): Long {
        return nettyBuffer.readVarLong()
    }

    override fun popInt(): Int {
        return nettyBuffer.readVarInt()
    }

    override fun popShort(): Short {
        return nettyBuffer.readShort()
    }

    override fun popByte(): Byte {
        return nettyBuffer.readByte()
    }

    override fun popBoolean(): Boolean {
        return nettyBuffer.readBoolean()
    }

    override fun <T : Enum<T>?> popEnum(e: Class<T>): T {
        return nettyBuffer.readEnumConstant(e)
    }

    override fun popUUID(): UUID? {
        return nettyBuffer.readUuid()
    }

    override fun toByteBuffer(): ByteBuffer {
        return nettyBuffer.nioBuffer()
    }

    fun load(arr: ByteArray): VariableByteBuf {
        nettyBuffer.writeBytes(arr)
        return this
    }
}