package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.data.EncodingKey
import fp.yeyu.mcdata.data.GameKey
import fp.yeyu.mcdata.interfaces.ByteSerializable
import fp.yeyu.mcdata.interfaces.SerializationContext
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

object ByteAttributeUtil {
    private val isClient: Boolean get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT
    private fun getCameraEntity(player: PlayerEntity): Entity =
            if (isClient) MinecraftClient.getInstance().cameraEntity ?: player
            else (player as ServerPlayerEntity).cameraEntity

    fun writeTimeStamp(buf: PacketByteBuf) {
        EncodingKey.TIME.serialize(buf)
        buf.writeLong(System.currentTimeMillis())
    }

    fun writeKeyPresses(buf: PacketByteBuf) {
        GameKey.values().filter { it.key.isPressed }.also {
            if (it.isNotEmpty()) {
                GameKey.encodingKey.serialize(buf)
                buf.writeVarInt(it.size)
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

    fun writeKeyPressesEnum(buf: PacketByteBuf) {
        GameKey.values().filter { it.key.isPressed }.also {
            buf.writeBoolean(it.isNotEmpty())
            if (it.isNotEmpty()) {
                buf.writeVarInt(it.size)
                it.forEach(buf::writeEnumConstant)
            }
        }
    }

    fun parseKeyPresses(buf: PacketByteBuf): MutableList<GameKey> {
        val arr = mutableListOf<GameKey>()
        if (!buf.readBoolean()) return arr
        for (i in 0 until buf.readVarInt()) {
            arr += buf.readEnumConstant(GameKey::class.java)
        }
        return arr
    }

    fun writeVisibleMobs(buf: PacketByteBuf, player: PlayerEntity) {
        getVisibleMobs(player).run {
            if (isNotEmpty()) {
                EncodingKey.MOBS.serialize(buf)
                buf.writeVarInt(size)
                forEach { (it as ByteSerializable).serialize(buf) }
            }
        }
    }

    fun writePlayerStats(buf: PacketByteBuf, player: PlayerEntity) {
        (player as ByteSerializable).serialize(buf)
    }

    fun writeVisibleBlock(buf: PacketByteBuf, player: PlayerEntity) {
        getVisibleBlocks(player).run {
            if (isNotEmpty()) {
                EncodingKey.BLOCKS.serialize(buf)
                buf.writeVarInt(size)
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
                getUpwardBlockPos(world, contextBlockPos, camera)?.let { blocks += it }
                getDownwardBlockPos(world, contextBlockPos, camera)?.let { blocks += it }
            }
        }

        return blocks
    }

    private fun getUpwardBlockPos(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity): BlockPos? {
        var toImmutable = contextBlockPos.toImmutable()
        while (toImmutable.y != 250) {
            val blockState = world.getBlockState(toImmutable)
            if (blockState.isAir) {
                toImmutable = toImmutable.up()
                continue
            }
            return if (SightUtil.isBlockVisible(camera, toImmutable)) toImmutable else null
        }
        return null
    }

    private fun getDownwardBlockPos(world: World, contextBlockPos: BlockPos.Mutable, camera: Entity): BlockPos? {
        var toImmutable = contextBlockPos.toImmutable()
        while (toImmutable.y != 0) {
            val blockState = world.getBlockState(toImmutable)
            if (blockState.isAir) {
                toImmutable = toImmutable.down()
                continue
            }
            return if (SightUtil.isBlockVisible(camera, toImmutable)) toImmutable else null
        }
        return null
    }
}