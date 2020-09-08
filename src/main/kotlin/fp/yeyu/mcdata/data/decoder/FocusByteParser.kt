package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.interfaces.ByteQueue
import net.minecraft.Bootstrap
import net.minecraft.util.registry.Registry

object FocusByteParser : ByteParser {
    override fun decode(queue: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.name("crosshair")

        run {
            jsonWriter.beginObject()
            jsonWriter.name("block_id")
            val blockId = queue.popInt()
            if (ConfigFile.configuration.useRawId) {
                jsonWriter.value(blockId)
            } else {
                Bootstrap.initialize()
                val block = Registry.BLOCK.get(blockId)
                val id = Registry.BLOCK.getId(block)
                jsonWriter.value(id.toString())
            }

            jsonWriter.name("block_position")

            run {
                jsonWriter.beginObject()
                jsonWriter.name("x")
                jsonWriter.value(queue.popInt())
                jsonWriter.name("y")
                jsonWriter.value(queue.popInt())
                jsonWriter.name("z")
                jsonWriter.value(queue.popInt())
                jsonWriter.endObject()
            }

            jsonWriter.endObject()
        }

    }

}
