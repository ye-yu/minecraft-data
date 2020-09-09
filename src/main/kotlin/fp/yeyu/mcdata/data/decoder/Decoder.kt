package fp.yeyu.mcdata.data.decoder

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.VariableByteBuf
import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.interfaces.ByteQueue
import fp.yeyu.mcdata.util.FileUtil
import io.netty.buffer.Unpooled
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter

object Decoder {
    fun decode(buf: ByteQueue, jsonWriter: JsonWriter) {
        jsonWriter.beginObject()
        jsonWriter.name("data")
        jsonWriter.beginArray()

        do {
            val readByte: Byte = buf.popByte()
            if (readByte == EncodingKey.EOF.byte) break

            EncodingKey[readByte].byteParser.decode(buf, jsonWriter)
        } while (true)

        jsonWriter.endArray()
        jsonWriter.endObject()
    }

    fun decodeFromLogFolder(name: String) {
        val source = File(FileUtil.logDirectoryInstance, name)
        val json = File(FileUtil.logDirectoryInstance, name.replace(".", "-") + ".json")

        FileInputStream(source).use {
            val bytes = it.readBytes()
            val buf = VariableByteBuf(Unpooled.buffer()).load(bytes)
            EncodingKey.EOF.serialize(buf)

            JsonWriter(FileWriter(json)).use { jsonWriter ->
                jsonWriter.setIndent("  ")
                decode(buf, jsonWriter)
            }
        }

        FileUtils.moveFileToDirectory(source, FileUtil.convertedLogDirectoryInstance, true)
    }
}