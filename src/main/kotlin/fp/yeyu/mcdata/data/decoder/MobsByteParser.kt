package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import net.minecraft.Bootstrap
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.registry.Registry

object MobsByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("mobs")

        val size = buf.readVarInt()

        jsonWriter.beginArray()
        repeat(size) {
            retrieveMobInfo(buf, jsonWriter)
        }
        jsonWriter.endArray()
    }

    private fun retrieveMobInfo(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("mob_id")
        val mobId = buf.readVarInt()
        if (ConfigFile.configuration.useRawId) {
            jsonWriter.value(mobId)
        } else {
            Bootstrap.initialize()
            val entityType = Registry.ENTITY_TYPE[mobId]
            val id = Registry.ENTITY_TYPE.getId(entityType)
            jsonWriter.value(id.toString())
        }

        jsonWriter.name("position")

        run {
            jsonWriter.beginObject()
            jsonWriter.name("x")
            jsonWriter.value(buf.readDouble())
            jsonWriter.name("y")
            jsonWriter.value(buf.readDouble())
            jsonWriter.name("z")
            jsonWriter.value(buf.readDouble())
            jsonWriter.endObject()
        }

        jsonWriter.endObject()
    }

}
