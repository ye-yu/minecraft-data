package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.data.VanillaBiomeType
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap

object BiomeByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        val biomeId = queue.popInt()

        jsonWriter.name("biome")
        if (ConfigFile.configuration.useRawId) {
            jsonWriter.value(biomeId)
        } else {
            Bootstrap.initialize()
            val biome = VanillaBiomeType.values()[biomeId]
            jsonWriter.value(biome.biomeKey.value.toString())
        }
    }

}
