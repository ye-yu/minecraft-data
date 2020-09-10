package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap
import net.minecraft.util.registry.Registry

object MenuSlotByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("menu_inventory")

        val size = queue.popInt()
        jsonWriter.beginArray()
        repeat(size) {
            jsonWriter.beginObject()
            jsonWriter.name("slot")
            jsonWriter.value(queue.popInt())
            jsonWriter.name("count")
            jsonWriter.value(queue.popInt())
            jsonWriter.name("item_id")

            val itemId = queue.popInt()

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
