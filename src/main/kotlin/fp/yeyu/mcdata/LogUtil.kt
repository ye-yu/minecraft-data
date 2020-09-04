package fp.yeyu.mcdata

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import java.util.stream.IntStream
import kotlin.streams.asSequence

object LogUtil {

    private val isClient: Boolean get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT
    private val envPrefix: String get() = if (isClient) "client: " else "server: "

    private fun getCameraEntity(player: PlayerEntity): Entity =
            if (isClient) MinecraftClient.getInstance().cameraEntity!!
            else (player as ServerPlayerEntity).cameraEntity

    fun getBlockPosition(player: PlayerEntity): String = "blockPos: ${player.blockPos.x} ${player.blockPos.y} ${player.blockPos.z}"
    fun getHeadRotation(player: PlayerEntity): String = "headRot: ${player.headYaw} ${player.pitch}"
    fun getCrossHairBlock(player: PlayerEntity): String = envPrefix + getCameraEntity(player).rayTrace(60.0, 0f, true).let {
        if (it.type == HitResult.Type.MISS) "none" else player.world.getBlockState(BlockPos(it.pos)).block.translationKey
    }

    fun getKeyPresses(): String = "keys:" + if (!isClient) "none" else {
        GameKey.values().filter { it.key.isPressed }.joinToString(",")
    }

    fun getKeyPresses(buf: PacketByteBuf): String = buf.readString()

    fun writeKeyPresses(buf: PacketByteBuf) {
        buf.writeString(getKeyPresses())
    }

    fun getHotBarCursor(player: PlayerEntity): String = "cursor: ${player.inventory.selectedSlot}"
    fun getVisibleMob(player: PlayerEntity): String {
        return "mobs: ?"
    }

    fun getInventory(player: PlayerEntity): String {
        return "inv: " + IntStream.range(0, player.inventory.size()).mapToObj(player.inventory::getStack).asSequence().joinToString(",") { it.toString() }
    }

    fun getVisibleBlocks(player: PlayerEntity): String {
        return "mobs: blocks"
    }

    fun getTimeStamp(): String = "${System.currentTimeMillis()}"
    fun getEndStamp(): String = "====="
    fun getUUID(player: PlayerEntity): String = "uuid: ${player.uuidAsString}"
}