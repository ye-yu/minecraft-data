package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object TimestampByteParser: ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("time")
        jsonWriter.value(buf.readLong())
    }
}