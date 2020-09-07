package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import net.minecraft.Bootstrap
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.registry.Registry

object MenuByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("menu")
        val menuId = buf.readVarInt()

        if (ConfigFile.configuration.useRawId) {
            jsonWriter.value(menuId)
        } else {
            Bootstrap.initialize()
            val screenHandlerType = Registry.SCREEN_HANDLER[menuId]
            val id = Registry.SCREEN_HANDLER.getId(screenHandlerType)
            jsonWriter.value(id.toString())
        }
    }
}
