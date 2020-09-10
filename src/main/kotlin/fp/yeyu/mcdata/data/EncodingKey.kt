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
    TIME(2, TimestampByteParser),
    WORLD(3, WorldByteParser),
    BIOME(4, BiomeByteParser),
    POSITION(5, PositionByteParser),
    ROTATION(6, RotationByteParser),
    HEALTH(7, HealthByteParser),
    EFFECT(8, StatusEffectByteParser),
    FOCUS(9, FocusByteParser),
    INVENTORY(10, InventoryByteParser),
    HOTBAR(11, HotBarCursorByteParser),
    MENU(12, MenuByteParser),
    KEYBOARD(13, KeyboardByteParser),
    MOUSE(14, MouseByteParser),
    MOUSE_POSITION(15, MousePositionByteParser),
    MENU_SLOTS(16, MenuSlotByteParser),
    MENU_CURSOR_SLOT(17, CursorStackByteParser),
    ACTION(18, ActionByteParser),
    MOBS(19, MobsByteParser),
    BLOCKS(20, BlocksByteParser),
    END(-2, EndByteParser),
    EOF(-1);

    companion object {
        operator fun get(byte: Byte): EncodingKey = values().firstOrNull { it.byte == byte } ?: BUFFER
    }

    override fun serialize(queue: ByteQueue) {
        queue.push(this.byte)
    }

}