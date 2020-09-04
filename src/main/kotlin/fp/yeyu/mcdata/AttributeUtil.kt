package fp.yeyu.mcdata

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RayTraceContext
import net.minecraft.world.World
import java.util.stream.IntStream
import kotlin.streams.asSequence

object AttributeUtil {

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
        val world = if (isClient) MinecraftClient.getInstance().world ?: return "mobs: ?" else player.world
        val distance = 100
        val box = Box(player.blockPos.lowerCorner(distance), player.blockPos.upperCorner(distance))
        val entities = world.getEntitiesByClass(LivingEntity::class.java, box) { true }
        return "mobs: " + entities.filter{ isEntityVisible(player, it) }.joinToString { it.toString() }
    }

    fun getInventory(player: PlayerEntity): String {
        return "inv: " + IntStream.range(0, player.inventory.size())
                .mapToObj { Pair(it, player.inventory.getStack(it)) }
                .filter{ !it.second.isEmpty }
                .asSequence()
                .joinToString(",") {  "${it.first}:${it.second}" }
    }

    fun getVisibleBlocks(player: PlayerEntity): String {
        return "blocks: "
    }

    fun getTimeStamp(): String = "${System.currentTimeMillis()}"
    fun getEndStamp(): String = "====="
    fun getUUID(player: PlayerEntity): String = "uuid: ${player.uuidAsString}"

    private fun isEntityVisible(player: PlayerEntity, target: Entity): Boolean {
        val camera = if (isClient) MinecraftClient.getInstance().cameraEntity ?: return false else (player as ServerPlayerEntity).cameraEntity
        return isEntityVisible(target, target.pos, camera.pos)
    }

    private fun isEntityVisible(sourceEntity: Entity, sourcePos: Vec3d, targetPos: Vec3d): Boolean {
        val world = sourceEntity.world

        val rayTraceContext = RayTraceContext(sourcePos, targetPos, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, sourceEntity)
        return world.rayTrace(rayTraceContext).let {
            when(it.type) {
                HitResult.Type.BLOCK -> false
                else -> true
            }
        }
    }

    private fun isOfEntityAt(world: World, position: BlockPos, target: Entity): Boolean {
        val entitiesByClass = world.getEntitiesByClass(target::class.java, Box(position), null)
        return entitiesByClass.any { it == target }
    }

    private fun BlockPos.lowerCorner(distance: Int): BlockPos = BlockPos(this.x - distance, 0, this.z - distance)
    private fun BlockPos.upperCorner(distance: Int): BlockPos = BlockPos(this.x + distance, 256, this.z + distance)
}
