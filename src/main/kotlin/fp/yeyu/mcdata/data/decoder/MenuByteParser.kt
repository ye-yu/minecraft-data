package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap
import net.minecraft.util.registry.Registry

object MenuByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("menu")
        val menuId = queue.popInt()

        if (ConfigFile.configuration.useRawId) {
            jsonWriter.value(menuId)
        } else {
            if (menuId == -1) {
                jsonWriter.nullValue()
            } else {
                Bootstrap.initialize()
                val screenHandlerType = Registry.SCREEN_HANDLER[menuId]
                val id = Registry.SCREEN_HANDLER.getId(screenHandlerType)
                jsonWriter.value(id.toString())
            }
        }
    }
}
