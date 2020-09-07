package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.PlayData
import net.minecraft.Bootstrap
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.registry.Registry

object FocusByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("crosshair")

        run {
            jsonWriter.beginObject()
            jsonWriter.name("block_id")
            val blockId = buf.readVarInt()
            if (PlayData.configuration.useRawId) {
                jsonWriter.value(blockId)
            } else {
                Bootstrap.initialize()
                val block = Registry.BLOCK.get(blockId)
                val id = Registry.BLOCK.getId(block)
                jsonWriter.value(id.toString())
            }

            jsonWriter.name("block_position")

            run {
                jsonWriter.beginObject()
                jsonWriter.name("x")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.name("y")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.name("z")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.endObject()
            }

            jsonWriter.endObject()
        }

    }

}
