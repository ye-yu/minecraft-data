package fp.yeyu.mcdata

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RayTraceContext
import java.util.stream.IntStream
import kotlin.math.PI
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
        val entities = world.getEntitiesByClass(LivingEntity::class.java, box) {
            isEntityVisible(player, it)
        }
        return "mobs: " + entities.joinToString { it.toString() }
    }

    fun getInventory(player: PlayerEntity): String {
        return "inv: " + IntStream.range(0, player.inventory.size())
                .mapToObj { Pair(it, player.inventory.getStack(it)) }
                .filter { !it.second.isEmpty }
                .asSequence()
                .joinToString(",") { "${it.first}:${it.second}" }
    }

    fun getVisibleBlocks(player: PlayerEntity): String {
        return "blocks: "
    }

    fun getTimeStamp(): String = "${System.currentTimeMillis()}"
    fun getEndStamp(): String = "====="
    fun getUUID(player: PlayerEntity): String = "uuid: ${player.uuidAsString}"

    private val indicesArray = intArrayOf(0, 1)
    private val boundPermute: Array<IntArray> = IntStream.range(0, 2)
            .mapToObj { it to indicesArray }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first, it)
                }.stream()
            }.map { it to indicesArray }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first[0], flat.first[1], it)
                }.stream()
            }.toArray {
                arrayOfNulls<IntArray>(it)
            }

    private fun isEntityVisible(player: PlayerEntity, target: Entity): Boolean {
        if (target is ClientPlayerEntity) return false
        val camera = if (isClient) MinecraftClient.getInstance().cameraEntity
                ?: return false else (player as ServerPlayerEntity).cameraEntity
        /* fixme */
        val xs = arrayOf(target.boundingBox.minX, target.boundingBox.maxX)
        val ys = arrayOf(target.boundingBox.minY, target.boundingBox.maxY)
        val zs = arrayOf(target.boundingBox.minZ, target.boundingBox.maxZ)

        return boundPermute.map { Vec3d(xs[it[0]], ys[it[1]], zs[it[2]]) }
                .any { isEntityVisible(target, it, camera.pos) }

    }

    private fun isEntityVisible(sourceEntity: Entity, sourcePos: Vec3d, targetPos: Vec3d): Boolean {
        val world = sourceEntity.world

        for(rayBlock in Vec3dRayIter(sourcePos, targetPos)) {
            val block = world.getBlockState(rayBlock)

            if (block.material == Material.AIR) continue
            println("Collided with $block")
            if (block.isTranslucent(world, rayBlock)) continue
            println("Block isn't translucent -> Entity is not visible")
            return false
        }
        return true
    }

    private fun BlockPos.lowerCorner(distance: Int): BlockPos = BlockPos(this.x - distance, 0, this.z - distance)
    private fun BlockPos.upperCorner(distance: Int): BlockPos = BlockPos(this.x + distance, 256, this.z + distance)
}
