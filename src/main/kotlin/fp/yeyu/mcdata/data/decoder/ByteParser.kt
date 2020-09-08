package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.interfaces.ByteQueue

interface ByteParser {
    fun decode(queue: ByteQueue, jsonWriter: JsonWriter)
}