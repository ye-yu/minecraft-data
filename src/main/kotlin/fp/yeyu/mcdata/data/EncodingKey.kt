package fp.yeyu.mcdata.data

import net.minecraft.network.PacketByteBuf

enum class EncodingKey(private val byte: Byte) {
    BUFFER(0),
    LOCAL(1),
    SERVER(2),
    TIME(3),
    UUID(4),
    POSITION(5),
    ROTATION(6),
    FOCUS(7),
    ACTION(8),
    MOBS(9),
    CURSOR(10),
    INVENTORY(11),
    VISIBLE(12),
    MENU(13),
    MOUSE(14),
    KEYBOARD(15),
    SLOTS(16),
    END(Byte.MAX_VALUE);

    operator fun get(byte: Byte): EncodingKey = values().first { it.byte == byte }

    fun serialize(buf: PacketByteBuf) {
        buf.writeByte(this.byte.toInt())
    }
}