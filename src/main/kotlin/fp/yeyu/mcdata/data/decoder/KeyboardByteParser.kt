package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object KeyboardByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("keyboard")
        val size = queue.popInt()

        jsonWriter.beginArray()

        repeat(size) {
            jsonWriter.value(queue.popInt())
        }

        jsonWriter.endArray()

    }

}
