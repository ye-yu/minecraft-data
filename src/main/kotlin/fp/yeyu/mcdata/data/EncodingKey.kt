package fp.yeyu.mcdata.data

enum class EncodingKey(val byte: Byte) {
    LOCAL(0x0),
    SERVER(0x1),
    SYSTEM(0x2),
    UUID(0x3),
    POSITION(0x4),
    ROTATION(0x5),
    FOCUS(0x6),
    ACTION(0x7),
    MOBS(0x8),
    CURSOR(0x9),
    INVENTORY(0xA),
    VISIBLE(0xB),
    OPENED(0xC),
    MOUSE(0xD),
    KEYBOARD(0xE),
    MENU(0xF),
    END(Byte.MAX_VALUE);

    operator fun get(byte: Byte): EncodingKey = values().first { it.byte == byte }
}