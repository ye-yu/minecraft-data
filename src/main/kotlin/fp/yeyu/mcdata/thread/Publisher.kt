package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.util.LogUtil

object Publisher {

    var startTracking: Boolean = false
    var publishNext = false

    private val sleep = ConfigFile.configuration.writeMillisecondSleep.toLong()

    fun start() {
        Thread.sleep(sleep)
        while (true) {
            if (!startTracking) continue
            if (!publishNext) continue
            publishNext = false
            LogUtil.publish()
        }
    }
}
