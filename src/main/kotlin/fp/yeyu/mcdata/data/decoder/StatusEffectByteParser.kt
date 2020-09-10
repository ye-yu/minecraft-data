package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.util.registry.Registry

object StatusEffectByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("effect")

        val size = queue.popInt()

        jsonWriter.beginArray()

        repeat(size) {
            jsonWriter.beginObject()
            jsonWriter.name("id")

            val effectId = queue.popInt()
            if (ConfigFile.configuration.useRawId) {
                val statusEffect = Registry.STATUS_EFFECT[effectId]
                val id = Registry.STATUS_EFFECT.getId(statusEffect)
                jsonWriter.value(id.toString())
            } else {
                jsonWriter.value(effectId)
            }

            jsonWriter.name("duration")
            jsonWriter.value(queue.popInt())
            jsonWriter.endObject()
        }

        jsonWriter.endArray()
    }

}
