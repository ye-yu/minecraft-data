package fp.yeyu.mcdata

import fp.yeyu.mcdata.interfaces.PlayDataState
import fp.yeyu.mcdata.thread.Consumer
import fp.yeyu.mcdata.thread.Parser
import fp.yeyu.mcdata.thread.PlayDataGroup
import fp.yeyu.mcdata.thread.Publisher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object PlayData : ClientModInitializer {
    private val logger: Logger = LogManager.getLogger()
    private lateinit var publisherThread: Thread
    private lateinit var consumerThread: Thread
    private val emptyThread = Thread {}

    override fun onInitializeClient() {
        publisherThread = Thread(PlayDataGroup, { Publisher.start() }, "publisher thread")
        consumerThread = Thread(PlayDataGroup, { Consumer.start() }, "consumer thread")

        logger.info("Trying to parse any leftovers byte data.")
        if (ConfigFile.configuration.convertToJson) Parser.start()
        ClientTickEvents.END_CLIENT_TICK.register {

            val state = it as PlayDataState
            if (state.worldHasChanged() && state.hasNotLogged()) {
                state.setHasNotLogged(false)
                if (it.world == null && ConfigFile.configuration.convertToJson) {
                    Thread(PlayDataGroup, {
                        @Suppress("ControlFlowWithEmptyBody")
                        while (Consumer.pause());
                        logger.info("Player exited the world. Disabling publisher...")
                        Publisher.startTracking = false
                        logger.info("Converting byte data...")
                        Parser.start()
                        logger.info("Completed.")
                    }, "parser thread").start()
                } else {
                    if (!publisherThread.startIfNotAlive()) publisherThread = emptyThread
                    if (!consumerThread.startIfNotAlive()) consumerThread = emptyThread
                    logger.info("Player entered a world. Starting publisher...")
                    Publisher.startTracking = true
                    logger.info("Starting consumer...")
                    Consumer.resume()
                }
            }
        }
        logger.info("Initialized PlayData client")
    }

    private fun Thread.startIfNotAlive(): Boolean {
        if (state != Thread.State.NEW) {
            logger.error("Cannot start thread $name: State is in $state")
            logger.info(stackTrace.joinToString(",\n") { it.className })
            return false
        }

        if (!isAlive) {
            logger.info("(Re)started thread: $name")
            logger.info(stackTrace.joinToString(",\n") { it.className })
            start()
        }
        return true
    }
}
