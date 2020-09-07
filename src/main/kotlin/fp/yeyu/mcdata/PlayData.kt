package fp.yeyu.mcdata

import fp.yeyu.mcdata.util.CommandUtil
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer

/**
 * Tracked data:
 *  - block position
 *  - head rotation
 *  - cross-hair block
 *  - key press*
 *  - mouse press*
 *  - visible-mob
 *  - hot bar cursor
 *  - current gui inventories
 *  - visible blocks
 *
 * */
object PlayData : ModInitializer, ClientModInitializer {
    val configuration: ConfigFile by ConfigFile.Companion

    override fun onInitialize() {
        CommandUtil.initMain()
        println(configuration.toString())
    }

    override fun onInitializeClient() {
        CommandUtil.initClient()
    }
}
