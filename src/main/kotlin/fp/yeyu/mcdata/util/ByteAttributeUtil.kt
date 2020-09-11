package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.data.GameKey
import fp.yeyu.mcdata.interfaces.ByteQueue
import fp.yeyu.mcdata.interfaces.ByteSerializable
import fp.yeyu.mcdata.interfaces.SerializationContext
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

object ByteAttributeUtil {
    private val isClient: Boolean get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT
    private fun getCameraEntity(player: PlayerEntity): Entity =
            if (isClient) MinecraftClient.getInstance().cameraEntity ?: player
            else (player as ServerPlayerEntity).cameraEntity

    fun writeTimeStamp(buf: ByteQueue) {
        EncodingKey.TIME.serialize(buf)
        buf.push(System.currentTimeMillis())
    }

    fun writeKeyPresses(buf: ByteQueue) {
        GameKey.values().filter { it.key.isPressed }.also {
            if (it.isNotEmpty()) {
                GameKey.encodingKey.serialize(buf)
                buf.push(it.size)
                it.forEach { key -> key.serialize(buf) }
            }
        }
    }

    private fun getVisibleMobs(player: PlayerEntity): MutableList<LivingEntity> {
        fun BlockPos.lowerCorner(distance: Int): BlockPos = BlockPos(this.x - distance, 0, this.z - distance)
        fun BlockPos.upperCorner(distance: Int): BlockPos = BlockPos(this.x + distance, 256, this.z + distance)
        val world = if (isClient) MinecraftClient.getInstance().world ?: return mutableListOf() else player.world
        val distance = 100
        val box = Box(player.blockPos.lowerCorner(distance), player.blockPos.upperCorner(distance))
        val camera = getCameraEntity(player)

        return world.getEntitiesByClass(LivingEntity::class.java, box) {
            SightUtil.isEntityVisible(camera, it)
        }
    }

    fun writeVisibleMobs(buf: ByteQueue, player: PlayerEntity) {
        getVisibleMobs(player).run {
            if (isNotEmpty()) {
                EncodingKey.MOBS.serialize(buf)
                buf.push(size)
                forEach { (it as ByteSerializable).serialize(buf) }
            }
        }
    }

    fun writePlayerStats(buf: ByteQueue, player: PlayerEntity) {
        (player as ByteSerializable).serialize(buf)
    }

    fun writeVisibleBlock(buf: ByteQueue, player: PlayerEntity) {
        getVisibleBlocks(player).run {
            if (isNotEmpty()) {
                EncodingKey.BLOCKS.serialize(buf)
                buf.push(size)
                forEach { (it as ByteSerializable).serialize(buf, player as SerializationContext) }
            }
        }
    }

    private fun getVisibleBlocks(player: PlayerEntity): MutableList<BlockPos> {
        val camera = getCameraEntity(player)
        val world = player.world
        val distance = 5
        val startBlockPos = camera.blockPos.add(-distance, 0, -distance)
        val contextBlockPos = startBlockPos.mutableCopy()
        val blocks = mutableListOf<BlockPos>()
        for (x in 0 until 2 * (distance + 1)) {
            for (z in 0 until 2 * (distance + 1)) {
                contextBlockPos.set(startBlockPos.x + x, startBlockPos.y, startBlockPos.z + z)
                blocks += getUpwardBlocks(world, contextBlockPos.mutableCopy(), camera)
                blocks += getDownwardBlocks(world, contextBlockPos.mutableCopy(), camera)
            }
        }

        return blocks
    }

    private tailrec fun getUpwardBlocks(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity, list: MutableList<BlockPos> = mutableListOf(), patience: Int = 5): MutableList<BlockPos> {
        return if (contextBlockPos.y == 256) list
        else {
            var patienceMutable = patience - 1
            if (!world.getBlockState(contextBlockPos).isAir && SightUtil.isBlockVisible(camera, contextBlockPos)) {
                list += contextBlockPos.mutableCopy()
                patienceMutable = 5
            } else {
                if (patience == 0) return list
            }
            getUpwardBlocks(world, contextBlockPos.also { it.y += 1 }, camera, list, patienceMutable)
        }
    }

    private tailrec fun getDownwardBlocks(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity, list: MutableList<BlockPos> = mutableListOf(), patience: Int = 5): MutableList<BlockPos> {
        return if (contextBlockPos.y == 0) list
        else {
            var patienceMutable = patience - 1
            if (!world.getBlockState(contextBlockPos).isAir && SightUtil.isBlockVisible(camera, contextBlockPos)) {
                list += contextBlockPos.mutableCopy()
                patienceMutable = 5
            } else {
                if (patience == 0) return list
            }
            getDownwardBlocks(world, contextBlockPos.also { it.y -= 1 }, camera, list, patienceMutable)
        }
    }


}