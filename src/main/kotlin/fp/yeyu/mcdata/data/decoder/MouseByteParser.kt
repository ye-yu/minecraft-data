package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object MouseByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("mouse_button")
        val size = queue.popInt()

        jsonWriter.beginArray()

        repeat(size) {
            jsonWriter.value(queue.popInt())
        }

        jsonWriter.endArray()
    }

}
