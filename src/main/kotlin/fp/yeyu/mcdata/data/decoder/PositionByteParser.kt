package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object PositionByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("position")
        jsonWriter.beginObject()
        jsonWriter.name("x")
        jsonWriter.value(queue.popDouble())
        jsonWriter.name("y")
        jsonWriter.value(queue.popDouble())
        jsonWriter.name("z")
        jsonWriter.value(queue.popDouble())
        jsonWriter.endObject()
    }

}
