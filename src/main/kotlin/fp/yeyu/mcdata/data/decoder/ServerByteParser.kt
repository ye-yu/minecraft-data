package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object ServerByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("log")
        jsonWriter.value("server")
    }
}