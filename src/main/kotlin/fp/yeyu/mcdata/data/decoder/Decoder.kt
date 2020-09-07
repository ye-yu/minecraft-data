package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.EncodingKey
import net.minecraft.network.PacketByteBuf

object Decoder {
    fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("data")
        jsonWriter.beginArray()

        do {
            val readByte: Byte = buf.readByte()
            if (readByte == EncodingKey.EOF.byte) break

            EncodingKey[readByte].byteParser?.decode(buf, jsonWriter)
        } while (true)

        jsonWriter.endArray()
        jsonWriter.endObject()
    }
}