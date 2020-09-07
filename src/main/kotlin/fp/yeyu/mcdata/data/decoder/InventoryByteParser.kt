package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object InventoryByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("inventory")

        val size = buf.readVarInt()
        jsonWriter.beginArray()
        repeat(size) {
            jsonWriter.beginObject()
            jsonWriter.name("slot")
            jsonWriter.value(buf.readVarInt())
            jsonWriter.name("count")
            jsonWriter.value(buf.readVarInt())
            jsonWriter.name("item_ordinal")
            jsonWriter.value(buf.readVarInt())
            jsonWriter.endObject()
        }
        jsonWriter.endArray()
    }

}
