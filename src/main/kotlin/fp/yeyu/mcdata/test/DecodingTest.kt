package fp.yeyu.mcdata.test

import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.VariableByteBuf
import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.data.decoder.Decoder
import io.netty.buffer.Unpooled
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

    fun readData(name: String) {
        val destination = File(directoryName, "$name-dat.json")
        getResourceStream("/test/$name.dat").use {
            val bytes = it.readBytes()
            val buf = VariableByteBuf(Unpooled.buffer()).load(bytes)
            EncodingKey.EOF.serialize(buf)

            JsonWriter(FileWriter(destination)).use { jsonWriter ->
                jsonWriter.setIndent("  ")
                Decoder.decode(buf, jsonWriter)
            }
        }
    }

}

fun main() {
    DecodingTest.prepare()
    DecodingTest.readData("late-reader")
    DecodingTest.readData("simple1")
    DecodingTest.readData("simple2")
    DecodingTest.readData("session1")
}
