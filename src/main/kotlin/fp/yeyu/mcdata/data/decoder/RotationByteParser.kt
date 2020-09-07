package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object RotationByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("rotation")
        jsonWriter.beginObject()
        jsonWriter.name("pitch")
        jsonWriter.value(buf.readDouble())
        jsonWriter.name("yaw")
        jsonWriter.value(buf.readDouble())
        jsonWriter.endObject()
    }

}
