package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.data.decoder.Decoder
import fp.yeyu.mcdata.util.FileUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

object Parser {
    private val logger: Logger = LogManager.getLogger()

    fun start() {
        val listFiles = FileUtil.logDirectoryInstance.list() ?: return

        listFiles.filter { it.endsWith(".dat") && File(FileUtil.logDirectoryInstance, it).isFile }.forEach {
            try {
                Decoder.decodeFromLogFolder(it)
            } catch (t: Throwable) {
                logger.error("Cannot encode $it into json format", t)
            }
        }

        FileUtil.refreshDestinationNames()
    }
}
