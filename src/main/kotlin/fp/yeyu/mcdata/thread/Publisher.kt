package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.util.LogUtil

object Publisher {

    var startTracking = false
        set(value) {
            field = value
            sleepTemp = true
        }

    private val sleep = ConfigFile.configuration.writeMillisecondSleep.toLong()
    private var sleepTemp = true

    fun start() {
        Thread.sleep(sleep)
        while(true) {
            if (sleepTemp) {
                Thread.sleep(sleep)
                sleepTemp = false
            }

            if (!startTracking) continue
            publish()
            Thread.sleep(sleep)
        }
    }

    fun publish() {
        LogUtil.publish()
    }

}