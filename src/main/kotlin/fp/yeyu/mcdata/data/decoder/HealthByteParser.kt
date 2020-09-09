package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object HealthByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        val health = queue.popDouble()
        val hunger = queue.popInt()
        jsonWriter.name("health")
        jsonWriter.value(health)
        jsonWriter.name("hunger")
        jsonWriter.value(hunger)
    }

}
