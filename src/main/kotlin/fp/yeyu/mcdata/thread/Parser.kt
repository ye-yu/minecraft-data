package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.data.decoder.Decoder
import fp.yeyu.mcdata.util.FileUtil
import java.io.File

object Parser {
    fun start() {
        val listFiles = FileUtil.logDirectoryInstance.list() ?: return

        listFiles.filter { it.endsWith(".dat") && File(FileUtil.logDirectoryInstance, it).isFile }.forEach {
            Decoder.decodeFromLogFolder(it)
        }

        FileUtil.refreshDestinationNames()
    }
}
