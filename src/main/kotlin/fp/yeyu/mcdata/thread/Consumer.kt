package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.LogRingBuffer
import fp.yeyu.mcdata.util.LogUtil

object Consumer {

    private val sleep = ConfigFile.configuration.writeMillisecondSleep.toLong()
    private var pause = false
    private var hasTerminate = true

    fun start() {
        Thread.sleep(sleep)
        while(true) {
            if (pause) {
                if (hasTerminate) continue
                Thread.sleep(sleep)
                hasTerminate = true
                continue
            }
            hasTerminate = false
            Thread.sleep(sleep)
            if (!LogRingBuffer.hasNext()) continue
            LogUtil.consume()
        }
    }

    fun pause(): Boolean {
        pause = true
        return hasTerminate
    }

    fun resume() {
        pause = false
    }
}
