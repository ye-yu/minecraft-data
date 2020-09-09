package fp.yeyu.mcdata

import fp.yeyu.mcdata.interfaces.PlayDataState
import fp.yeyu.mcdata.util.CommandUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object PlayData : ModInitializer, ClientModInitializer {
    private val logger: Logger = LogManager.getLogger()

    override fun onInitialize() {
        CommandUtil.initMain()
        logger.info("Initialized PlayData main")
        ClientTickEvents.END_CLIENT_TICK.register {
            val state = it as PlayDataState
            if (state.worldHasChanged() && state.hasNotLogged()) {
                state.setHasNotLogged(false)
                if (it.world == null) {
                    logger.info("Player exited the world.")
                } else {
                    logger.info("Player entered a world.")
                }
            }
        }
    }

    override fun onInitializeClient() {
        CommandUtil.initClient()
        logger.info("Initialized PlayData client")
    }
}
