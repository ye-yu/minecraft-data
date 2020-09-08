package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap
import net.minecraft.util.registry.Registry

object MobsByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("mobs")

        val size = queue.popInt()

        jsonWriter.beginArray()
        repeat(size) {
            retrieveMobInfo(queue, jsonWriter)
        }
        jsonWriter.endArray()
    }

    private fun retrieveMobInfo(buf: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("mob_id")
        val mobId = buf.popInt()
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
            jsonWriter.value(buf.popDouble())
            jsonWriter.name("y")
            jsonWriter.value(buf.popDouble())
            jsonWriter.name("z")
            jsonWriter.value(buf.popDouble())
            jsonWriter.endObject()
        }

        jsonWriter.endObject()
    }

}
