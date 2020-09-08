package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object UUIDByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("uuid")
        jsonWriter.value(queue.popUUID().toString())
    }

}
