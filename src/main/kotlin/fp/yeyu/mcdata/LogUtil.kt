package fp.yeyu.mcdata

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import java.util.stream.IntStream
import kotlin.streams.asSequence

object LogUtil {

    private val isClient: Boolean get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT
    private val envPrefix: String get() = if (isClient) "client: " else "server: "

    private fun getCameraEntity(player: ServerPlayerEntity): Entity =
            if (isClient) MinecraftClient.getInstance().cameraEntity!!
            else player.cameraEntity

    fun getBlockPosition(player: ServerPlayerEntity): String = "blockPos: ${player.blockPos.x} ${player.blockPos.y} ${player.blockPos.z}"
    fun getHeadRotation(player: ServerPlayerEntity): String = "headRot: ${player.headYaw} ${player.pitch}"
    fun getCrossHairBlock(player: ServerPlayerEntity): String = envPrefix + getCameraEntity(player).rayTrace(60.0, 0f, true).let {
        if (it.type == HitResult.Type.MISS) "none" else player.world.getBlockState(BlockPos(it.pos)).block.translationKey
    }

    private fun getKeyPresses(): String = "keys:" + if (!isClient) "none" else {
        GameKey.values().filter { it.key.isPressed }.joinToString(",")
    }

    fun getKeyPresses(buf: PacketByteBuf): String = "keys: ${buf.readString()}"

    fun writeKeyPresses(buf: PacketByteBuf) {
        buf.writeString(getKeyPresses())
    }

    fun getHotBarCursor(player: ServerPlayerEntity): String = "cursor: ${player.inventory.selectedSlot}"
    fun getVisibleMob(player: ServerPlayerEntity): String {
        return "mobs: ?"
    }

    fun getInventory(player: ServerPlayerEntity): String {
        return "inv: " + IntStream.range(0, player.inventory.size()).mapToObj(player.inventory::getStack).asSequence().joinToString(",") { it.toString() }
    }

    fun getVisibleBlocks(player: ServerPlayerEntity): String {
        return "mobs: blocks"
    }

    fun getTimeStamp(): String = "${System.currentTimeMillis()}"
    fun getEndStamp(): String = "====="
    fun getUUID(player: ServerPlayerEntity): String = "uuid: ${player.uuidAsString}"
}