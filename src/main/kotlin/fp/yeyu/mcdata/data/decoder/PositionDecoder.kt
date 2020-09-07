package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object PositionDecoder : Decoder {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("position")
        jsonWriter.beginObject()
        jsonWriter.name("x")
        jsonWriter.value(buf.readDouble())
        jsonWriter.name("y")
        jsonWriter.value(buf.readDouble())
        jsonWriter.name("z")
        jsonWriter.value(buf.readDouble())
        jsonWriter.endObject()
    }

}
