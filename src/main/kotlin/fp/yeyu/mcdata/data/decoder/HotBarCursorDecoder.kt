package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

object HotBarCursorDecoder : Decoder {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("hot_bar")
        jsonWriter.value(buf.readVarInt())
    }

}
