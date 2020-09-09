package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.LogRingBuffer
import java.io.File
import java.io.IOException

object FileUtil {

    private const val modDirectory = "./mods/Play Data"
    private const val logDirectory = "log"
    private const val convertedLogDirectory = "finished"
    var logDestination = createLogDestination()
    var logDestinationByte = createLogByteDestination()
    var parallelDestinationByte = Array(LogRingBuffer.parallelWriteThreads, FileUtil::createLogByteDestination)

    val logDirectoryInstance = File(modDirectory, logDirectory).also {
        createDirsOrFailIfNotExists(it)
    }

    val configDirectoryInstance = File(modDirectory).also {
        createDirsOrFailIfNotExists(it)
    }

    val convertedLogDirectoryInstance = File(logDirectoryInstance, convertedLogDirectory).also {
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

    private fun createLogDestination(): File = File(logDirectoryInstance, "${System.currentTimeMillis()}.log")
    private fun createLogByteDestination(ordinal: Int? = null): File = File(logDirectoryInstance, "${System.currentTimeMillis()}${ordinal ?: ""}.dat")
    
    fun refreshDestinationNames() {
        logDestination = createLogDestination()
        logDestinationByte = createLogByteDestination()
        parallelDestinationByte = Array(LogRingBuffer.parallelWriteThreads, FileUtil::createLogByteDestination)
    }

}