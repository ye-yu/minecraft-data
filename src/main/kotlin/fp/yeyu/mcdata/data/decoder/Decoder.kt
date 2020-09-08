package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.interfaces.ByteQueue

object Decoder {
    fun decode(buf: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("data")
        jsonWriter.beginArray()

        do {
            val readByte: Byte = buf.popByte()
            if (readByte == EncodingKey.EOF.byte) break

            EncodingKey[readByte].byteParser?.decode(buf, jsonWriter)
        } while (true)

        jsonWriter.endArray()
        jsonWriter.endObject()
    }
}