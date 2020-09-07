package fp.yeyu.mcdata.test

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.data.decoder.Decoder
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import java.io.File
import java.io.FileWriter

object DecodingTest {
    private val directoryName: File by lazy {
        val dir = File("test-output")
        if (!dir.exists() && dir.mkdir()) dir
        else if (dir.isFile) throw FileAlreadyExistsException(dir)
        else dir
    }

    fun prepare() {
        directoryName.listFiles()?.forEach { if (it.isFile) it.delete() }
    }

    private fun getResourceStream(name: String) = DecodingTest::class.java.getResourceAsStream(name)

    fun readSimpleLocal() {
        val destination = File(directoryName, "simple-local-dat.json")
        getResourceStream("/test/simple-local.dat").use {
            val bytes = it.readBytes()
            val buf = PacketByteBuf(Unpooled.buffer()).load(bytes)
            EncodingKey.EOF.serialize(buf)

            JsonWriter(FileWriter(destination)).use { jsonWriter ->
                jsonWriter.setIndent("  ")
                Decoder.decode(buf, jsonWriter)
            }
        }
    }

    fun readTwoLocal() {
        val destination = File(directoryName, "two-local-dat.json")
        getResourceStream("/test/two-local.dat").use {
            val bytes = it.readBytes()
            val buf = PacketByteBuf(Unpooled.buffer()).load(bytes)
            EncodingKey.EOF.serialize(buf)

            JsonWriter(FileWriter(destination)).use { jsonWriter ->
                jsonWriter.setIndent("  ")
                Decoder.decode(buf, jsonWriter)
            }
        }
    }

    fun readSession1() {
        val destination = File(directoryName, "session1-dat.json")
        getResourceStream("/test/session1.dat").use {
            val bytes = it.readBytes()
            val buf = PacketByteBuf(Unpooled.buffer()).load(bytes)
            EncodingKey.EOF.serialize(buf)

            JsonWriter(FileWriter(destination)).use { jsonWriter ->
                jsonWriter.setIndent("  ")
                Decoder.decode(buf, jsonWriter)
            }
        }
    }


    private fun PacketByteBuf.load(bytes: ByteArray): PacketByteBuf {
        writeBytes(bytes)
        return this
    }
}

fun main() {
    DecodingTest.prepare()
    DecodingTest.readSimpleLocal()
    DecodingTest.readTwoLocal()
    DecodingTest.readSession1()
}
