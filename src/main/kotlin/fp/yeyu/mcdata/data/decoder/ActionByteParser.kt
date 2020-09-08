package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.GameKey
import fp.yeyu.mcdata.interfaces.ByteQueue

object ActionByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("action")

        val size = queue.popInt()
        run {
            jsonWriter.beginArray()
            repeat(size) {
                jsonWriter.value(queue.popEnum(GameKey::class.java).name)
            }
            jsonWriter.endArray()
        }
    }
}
