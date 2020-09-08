package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object RotationByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("rotation")
        jsonWriter.beginObject()
        jsonWriter.name("pitch")
        jsonWriter.value(queue.popDouble())
        jsonWriter.name("yaw")
        jsonWriter.value(queue.popDouble())
        jsonWriter.endObject()
    }

}
