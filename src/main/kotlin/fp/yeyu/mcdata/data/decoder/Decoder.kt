package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import net.minecraft.network.PacketByteBuf

interface Decoder {
    fun decode(buf: PacketByteBuf, jsonWriter: JsonWriter)
}