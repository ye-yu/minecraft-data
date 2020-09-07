package fp.yeyu.mcdata.util

import java.io.File
import java.io.IOException
import kotlin.random.Random

object FileUtil {

    private val random = Random(System.currentTimeMillis())
    private const val modDirectory = "./mods/"
    private const val logDirectory = "log"
    val logDestination by lazy(FileUtil::createLogDestination)
    val logDestinationByte by lazy(FileUtil::createLogByteDestination)


    val logDirectoryInstance = File(modDirectory, logDirectory).also {
        createDirsOrFailIfNotExists(it)
    }

    val configDirectoryInstance = File(modDirectory).also {
        createDirsOrFailIfNotExists(it)
    }

    private fun createDirsOrFailIfNotExists(file: File) {
        if (file.exists()) {
            if (file.isFile) throw FileAlreadyExistsException(file)
            else return
        }
        if (file.mkdirs()) return
        throw IOException("Cannot create recursive directory of $file")
    }

    private fun createLogDestination(): File = File(logDirectoryInstance, "${random.nextLong()}.log")
    private fun createLogByteDestination(): File = File(logDirectoryInstance, "${random.nextLong()}.dat")

}