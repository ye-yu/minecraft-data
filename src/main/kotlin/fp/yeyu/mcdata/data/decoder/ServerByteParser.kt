package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

object ServerByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("log")
        jsonWriter.value("server")
    }
}