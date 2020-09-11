package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.ConfigFile
import fp.yeyu.mcdata.util.LogUtil
import net.minecraft.client.MinecraftClient

object Publisher {

    var startTracking = false
        set(value) {
            field = value
            sleepTemp = true
        }

    private val sleep = ConfigFile.configuration.writeMillisecondSleep.toLong()
    private var sleepTemp = true
    private val isScreenPaused: Boolean
        get() = MinecraftClient.getInstance().isPaused


    fun start() {
        Thread.sleep(sleep)
        while (true) {
            if (sleepTemp) {
                Thread.sleep(sleep)
                sleepTemp = false
            }

            if (!startTracking) continue
//            if (isScreenPaused) continue // fixme: why cannnooottttt
            LogUtil.publish()
            Thread.sleep(sleep)
        }
    }

    fun reset() {
        startTracking = false
    }
}
