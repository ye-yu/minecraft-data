package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.data.VanillaWorldType
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap

object WorldByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        val worldOrdinal = queue.popInt()

        jsonWriter.name("world_type")
        if (ConfigFile.configuration.useRawId) {
            jsonWriter.value(worldOrdinal)
        } else {
            Bootstrap.initialize()
            val world = VanillaWorldType.values()[worldOrdinal]
            jsonWriter.value(world.dimension.value.toString())
        }
    }
}