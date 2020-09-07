package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object MobsByteParser : ByteParser {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("mobs")

        val size = buf.readVarInt()

        jsonWriter.beginArray()
        repeat(size) {
            retrieveMobInfo(buf, jsonWriter)
        }
        jsonWriter.endArray()
    }

    private fun retrieveMobInfo(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("mob_ordinal")
        jsonWriter.value(buf.readByte())
        jsonWriter.name("position")

        run {
            jsonWriter.beginObject()
            jsonWriter.name("x")
            jsonWriter.value(buf.readDouble())
            jsonWriter.name("y")
            jsonWriter.value(buf.readDouble())
            jsonWriter.name("z")
            jsonWriter.value(buf.readDouble())
            jsonWriter.endObject()
        }

        jsonWriter.endObject()
    }

}
