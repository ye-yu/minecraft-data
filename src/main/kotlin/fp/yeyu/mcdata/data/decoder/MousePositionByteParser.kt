package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object MousePositionByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("mouse_position")
        jsonWriter.beginObject()

        run {
            jsonWriter.name("x")
            jsonWriter.value(queue.popDouble())
            jsonWriter.name("y")
            jsonWriter.value(queue.popDouble())
        }

        jsonWriter.endObject()
    }

}
