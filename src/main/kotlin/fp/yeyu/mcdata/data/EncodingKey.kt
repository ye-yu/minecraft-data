package fp.yeyu.mcdata.data

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.decoder.*
import net.minecraft.network.PacketByteBuf

enum class EncodingKey(val byte: Byte, val decoder: Decoder? = object : Decoder {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
    }
}) {
    BUFFER(0),
    LOCAL(1, LocalDecoder),
    SERVER(2, ServerDecoder),
    TIME(3, TimestampDecoder),
    UUID(4, UUIDDecoder),
    POSITION(5, PositionDecoder),
    ROTATION(6, RotationDecoder),
    FOCUS(7, FocusDecoder),
    ACTION(8, ActionDecoder),
    MOBS(9, MobsDecoder),
    CURSOR(10, HotBarCursorDecoder),
    INVENTORY(11, InventoryDecoder),
    BLOCKS(12, BlocksDecoder),
    MENU(13),
    MOUSE(14),
    KEYBOARD(15),
    ITEM_SLOTS(16),
    CURSOR_SLOTS(17),
    END(Byte.MAX_VALUE, EndDecoder),
    EOF(-1);

    companion object {
        operator fun get(byte: Byte): EncodingKey = values().first { it.byte == byte }
    }

    fun serialize(buf: PacketByteBuf) {
        buf.writeByte(this.byte.toInt())
    }
}