package fp.yeyu.mcdata

import fp.yeyu.mcdata.util.CommandUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object PlayData : ModInitializer, ClientModInitializer {
    private val logger: Logger = LogManager.getLogger()

    override fun onInitialize() {
        CommandUtil.initMain()
        logger.info("Initialized PlayData main")
    }

    override fun onInitializeClient() {
        CommandUtil.initClient()
        logger.info("Initialized PlayData client")
    }
}
