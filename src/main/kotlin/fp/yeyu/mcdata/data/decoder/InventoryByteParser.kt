package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import net.minecraft.Bootstrap
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.registry.Registry

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
            jsonWriter.name("item_id")

            val itemId = buf.readVarInt()

            if (ConfigFile.configuration.useRawId) {
                jsonWriter.value(itemId)
            } else {
                Bootstrap.initialize()
                val item = Registry.ITEM[itemId]
                val id = Registry.ITEM.getId(item)
                jsonWriter.value(id.toString())
            }
            jsonWriter.endObject()
        }
        jsonWriter.endArray()
    }

}
