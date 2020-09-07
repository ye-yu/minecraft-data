package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object FocusByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("crosshair")

        run {
            jsonWriter.beginObject()
            jsonWriter.name("block_ordinal")
            jsonWriter.value(buf.readByte())
            jsonWriter.name("block_position")

            run {
                jsonWriter.beginObject()
                jsonWriter.name("x")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.name("y")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.name("z")
                jsonWriter.value(buf.readVarInt())
                jsonWriter.endObject()
            }

            jsonWriter.endObject()
        }

    }

}
