package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.GameKey
import net.minecraft.network.PacketByteBuf

object ActionDecoder : Decoder {
    override fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter) {
        jsonWriter.name("action")

        val size = buf.readVarInt()
        run {
            jsonWriter.beginArray()
            repeat(size) {
                jsonWriter.value(buf.readEnumConstant(GameKey::class.java).name)
            }
            jsonWriter.endArray()
        }
    }
}
