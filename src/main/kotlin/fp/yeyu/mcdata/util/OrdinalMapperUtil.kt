package fp.yeyu.mcdata.util

import net.minecraft.util.registry.Registry
import java.io.File
import java.io.FileWriter

object OrdinalMapperUtil {
    private val mobMapperFile by lazy { createNew("mob-map.txt") }
    private val blockMapperFile by lazy { createNew("block-map.txt") }
    private val itemMapperFile by lazy { createNew("item-map.txt") }

    private fun writeOrdinalMap(target: File, ordinal: Int, identifier: String) {
        FileWriter(target, true).use {
            it.write("$ordinal $identifier\n")
        }
    }

    private fun createNew(child: String): File {
        return File(FileUtil.logDirectory, child)
    }

    fun exportRawIds(): Int {
        itemMapperFile.run(File::delete)
        blockMapperFile.run(File::delete)
        mobMapperFile.run(File::delete)
        exportRegistry(Registry.ITEM, itemMapperFile)
        exportRegistry(Registry.BLOCK, blockMapperFile)
        exportRegistry(Registry.ENTITY_TYPE, mobMapperFile)
        return 1
    }

    private fun <T> exportRegistry(registry: Registry<T>, mapperFile: File) {
        registry.forEach {
            val rawId = registry.getRawId(it)
            val id = registry.getId(it)
            writeOrdinalMap(mapperFile, rawId, id.toString())
        }
    }
}