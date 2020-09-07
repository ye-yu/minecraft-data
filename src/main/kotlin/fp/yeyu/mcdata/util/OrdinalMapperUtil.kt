package fp.yeyu.mcdata.util

import java.io.File
import java.io.FileWriter

object OrdinalMapperUtil {
    private val mobMapperFile by lazy { createNew("mob-map.txt") }
    private val blockMapperFile by lazy { createNew("block-map.txt") }
    private val itemMapperFile by lazy { createNew("item-map.txt") }

    private fun writeOrdinalMap(target: File, ordinal: Short, identifier: String) {
        FileWriter(target, true).use {
            it.write("$ordinal $identifier\n")
        }
    }

    private fun createNew(child: String): File {
        return File(FileUtil.logDirectory, child).also {
            if (it.exists()) it.delete()
        }
    }

    fun mapMob(ordinal: Short, identifier: String) = writeOrdinalMap(mobMapperFile, ordinal, identifier)
    fun mapBlock(ordinal: Short, identifier: String) = writeOrdinalMap(blockMapperFile, ordinal, identifier)
    fun mapItem(ordinal: Short, identifier: String) = writeOrdinalMap(itemMapperFile, ordinal, identifier)
}