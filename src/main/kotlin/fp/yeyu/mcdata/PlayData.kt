package fp.yeyu.mcdata

import fp.yeyu.mcdata.util.CommandUtil
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Tracked data:
 *  - block position
 *  - head rotation
 *  - cross-hair block
 *  - key press*
 *  - mouse press*
 *  - visible-mob
 *  - hot bar cursor
 *  - current gui inventories
 *  - visible blocks
 *
 * */
object PlayData : ModInitializer, ClientModInitializer {
    val configuration: ConfigFile by lazy { readConfigFile() }
    private val configurationFile by lazy { getOrCreateConfigFile() }
    private val jsonInstance = Json {
        prettyPrint = true
    }

    private fun getOrCreateConfigFile(): File {
        val targetJson = "config.json"
        val directory = "./Play Data"

        val directoryFile = File(directory)
        return if (!directoryFile.exists() && directoryFile.mkdirs()) File(directoryFile, targetJson)
        else if (directoryFile.isFile) throw FileAlreadyExistsException(directoryFile)
        else if (directoryFile.isDirectory) File(directoryFile, targetJson)
        else throw FileAlreadyExistsException(directoryFile)
    }

    private fun readConfigFile() = if (configurationFile.exists()) {
        val meta = FileReader(configurationFile).use {
            it.readText()
        }
        jsonInstance.decodeFromString(ConfigFile.serializer(), meta)
    } else {
        val configFile = ConfigFile()
        val meta = jsonInstance.encodeToString(ConfigFile.serializer(), configFile)
        FileWriter(configurationFile).use {
            it.write(meta)
        }
        configFile
    }

    override fun onInitialize() {
        CommandUtil.initMain()
    }

    override fun onInitializeClient() {
        CommandUtil.initClient()
    }

}
