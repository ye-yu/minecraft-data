package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.data.GameKey
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import java.util.stream.IntStream
import kotlin.streams.asSequence

object StringAttributeUtil {

    private val isClient: Boolean get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT
    private val envPrefix: String get() = if (isClient) "client: " else "server: "

    private fun getCameraEntity(player: PlayerEntity): Entity =
            if (isClient) MinecraftClient.getInstance().cameraEntity ?: player
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
        fun BlockPos.lowerCorner(distance: Int): BlockPos = BlockPos(this.x - distance, 0, this.z - distance)
        fun BlockPos.upperCorner(distance: Int): BlockPos = BlockPos(this.x + distance, 256, this.z + distance)
        val world = if (isClient) MinecraftClient.getInstance().world ?: return "mobs: ?" else player.world
        val distance = 100
        val box = Box(player.blockPos.lowerCorner(distance), player.blockPos.upperCorner(distance))
        val camera = getCameraEntity(player)
        val entities = world.getEntitiesByClass(LivingEntity::class.java, box) {
            SightUtil.isEntityVisible(camera, it)
        }
        return "mobs: " + entities.let { list ->
            if (list.isEmpty()) "none" else list.joinToString { it.toString() }
        }
    }

    fun getInventory(player: PlayerEntity): String {
        return "inv: " + IntStream.range(0, player.inventory.size())
                .mapToObj { Pair(it, player.inventory.getStack(it)) }
                .filter { !it.second.isEmpty }
                .asSequence()
                .joinToString(",") { "${it.first}:${it.second}" }
    }

    fun getVisibleBlocks(player: PlayerEntity): String {
        val camera = getCameraEntity(player)
        val world = player.world
        val distance = 5
        val startBlockPos = camera.blockPos.add(-distance, 0, -distance)
        val contextBlockPos = startBlockPos.mutableCopy()
        val blocks = StringBuilder()
        for (x in 0 until 2 * (distance + 1)) {
            for (z in 0 until 2 * (distance + 1)) {
                contextBlockPos.set(startBlockPos.x + x, startBlockPos.y, startBlockPos.z + z)
                getUpwardBlock(world, contextBlockPos, camera).run { this?.run { blocks.append(this) } }
                getDownwardBlock(world, contextBlockPos, camera).run { this?.run { blocks.append(this) } }
            }
        }

        return "blocks: $blocks"
    }

    /* todo: mixin so that no longer use .up() */
    private fun getUpwardBlock(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity): String? {
        var toImmutable = contextBlockPos.toImmutable()
        while (toImmutable.y != 250) {
            val blockState = world.getBlockState(toImmutable)
            if (blockState.isAir) {
                toImmutable = toImmutable.up()
                continue
            }
            return if (SightUtil.isBlockVisible(camera, toImmutable)) blockState.block.toString() + toImmutable.toString() else null
        }
        return null
    }

    /* todo: mixin so that no longer use .down() */
    private fun getDownwardBlock(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity): String? {
        var toImmutable = contextBlockPos.toImmutable()
        while (toImmutable.y != 0) {
            val blockState = world.getBlockState(toImmutable)
            if (blockState.isAir) {
                toImmutable = toImmutable.down()
                continue
            }
            return if (SightUtil.isBlockVisible(camera, toImmutable)) {
                if (blockState.block == Blocks.BEDROCK) {
                    println("Bedrock at $toImmutable can be seen")
                }
                blockState.block.toString() + toImmutable.toString()
            } else {
                if (blockState.block == Blocks.BEDROCK) {
                    println("Bedrock at $toImmutable cannot be seen")
                }
                null
            }
        }
        return null
    }


    fun getTimeStamp(): String = "${System.currentTimeMillis()}"
    fun getEndStamp(): String = "====="
    fun getUUID(player: PlayerEntity): String = "uuid: ${player.uuidAsString}"

}
