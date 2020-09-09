package fp.yeyu.mcdata.data

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.decoder.*
import fp.yeyu.mcdata.interfaces.ByteQueue
import fp.yeyu.mcdata.interfaces.ByteSerializable

enum class EncodingKey(val byte: Byte, val byteParser: ByteParser = object : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
    }
}) : ByteSerializable {
    BUFFER(0),
    START(1, StartByteParser),
    WORLD(2, WorldByteParser),
    TIME(3, TimestampByteParser),
    UUID(4, UUIDByteParser),
    POSITION(5, PositionByteParser),
    ROTATION(6, RotationByteParser),
    FOCUS(7, FocusByteParser),
    ACTION(8, ActionByteParser),
    MOBS(9, MobsByteParser),
    CURSOR(10, HotBarCursorByteParser),
    INVENTORY(11, InventoryByteParser),
    BLOCKS(12, BlocksByteParser),
    MENU(13, MenuByteParser),
    MOUSE(14),
    KEYBOARD(15),
    ITEM_SLOTS(16),
    CURSOR_SLOTS(17),
    BIOME(18, BiomeByteParser),
    END(Byte.MAX_VALUE, EndByteParser),
    EOF(-1);

    companion object {
        operator fun get(byte: Byte): EncodingKey = values().first { it.byte == byte }
    }

    override fun serialize(queue: ByteQueue) {
        queue.push(this.byte)
    }

}