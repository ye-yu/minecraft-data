package fp.yeyu.mcdata.thread

import fp.yeyu.mcdata.data.GameKey
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import org.apache.commons.lang3.tuple.MutablePair

/**
 * Replays:
 *  - keys down
 *  - open menu
 *  - close menu
 * */
object Replayer {
    private val hasMenuOpen: Boolean get() = MinecraftClient.getInstance().currentScreen is HandledScreen<*>
    private val currentScreen: HandledScreen<*>? get() = MinecraftClient.getInstance().currentScreen as HandledScreen<*>?
    private val movements = ArrayDeque<MutablePair<Array<out GameKey>, Int>>()
    private val slotEvents = ArrayDeque<Int>()

    fun tick() {
        if (hasMenuOpen) tickMenu()
        else tickMovement()
    }

    private fun tickMovement() {
        if (movements.isEmpty()) return
        if (movements[0].right == 0) {
            movements.removeFirst()
        } else {
            movements[0].left.forEach { it.key.isPressed = true }
            movements[0].right--
        }
    }

    /**
     * handles slot clicks only
     * */
    private fun tickMenu() {
        val slotId = slotEvents.removeFirst()
        MinecraftClient.getInstance().interactionManager?.clickButton(currentScreen?.screenHandler?.syncId
                ?: -1, slotId)
    }

    fun pushMovement(ticks: Int, vararg keys: GameKey) {
        val to = keys mutableTo ticks
        movements.addLast(to)
    }

    fun pushSlotClick(slotNumber: Int) {
        slotEvents += slotNumber
    }

    private infix fun <T> T.mutableTo(ticks: Int): MutablePair<T, Int> = MutablePair(this, ticks)
}
