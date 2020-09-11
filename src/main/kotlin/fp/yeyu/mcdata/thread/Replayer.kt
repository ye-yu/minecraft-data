package fp.yeyu.mcdata.thread

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen

/**
 * Replays:
 *  - keys down
 *  - open menu
 *  - close menu
 * */
object Replayer {
    val hasMenuOpen: Boolean get() = MinecraftClient.getInstance().currentScreen is HandledScreen<*>


}