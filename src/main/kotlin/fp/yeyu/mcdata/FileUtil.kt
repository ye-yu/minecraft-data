package fp.yeyu.mcdata

import java.io.File
import kotlin.random.Random

object FileUtil {

    private val random = Random(System.currentTimeMillis())
    private const val logDirectory = "playdata-log"
    val logDestination by lazy(FileUtil::createLogDestination)
    val logDestinationByte by lazy(FileUtil::createLogByteDestination)

    private val logDirectoryInstance = File(logDirectory).also {
        if (!it.exists() && it.mkdir()) println("created directory $logDirectory")
        else if (it.isFile) throw FileAlreadyExistsException(it, reason = "$logDirectory already exists and it is a file")
    }

    private fun createLogDestination(): File = File(logDirectoryInstance, "${random.nextLong()}.log")
    private fun createLogByteDestination(): File = File(logDirectoryInstance, "${random.nextLong()}.dat")

}