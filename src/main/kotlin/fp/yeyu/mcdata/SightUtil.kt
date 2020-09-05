package fp.yeyu.mcdata

import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import java.util.*
import java.util.stream.IntStream
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


object SightUtil {

    fun getEntitiesInSight(entities: List<Entity>, startPoint: Vec3d, distance: Double, radian: Double, yaw: Double): MutableList<Entity> {
        val newEntities: MutableList<Entity> = ArrayList<Entity>()

        val startPos2d = intArrayOf(startPoint.x.toInt(), startPoint.z.toInt())
        val deg2rad = 0.017453292519943295
        val endPos2d = intArrayOf((distance * cos(yaw * deg2rad - radian / 2)).toInt(), (distance * sin(yaw * deg2rad - radian / 2)).toInt())

        for (entity in entities) {
            val entityVector: IntArray = getDirectionVector(startPos2d[0], startPos2d[1], entity.blockPos.x, entity.blockPos.y)
            val angle: Double = getAngleBetweenVectors(endPos2d, entityVector)
            if (angle in 0.0..radian) newEntities.add(entity)
        }
        return newEntities
    }

    private fun getAngleBetweenVectors(vector1: IntArray, vector2: IntArray): Double {
        return atan2(vector2[1], vector2[0]) - atan2(vector1[1], vector1[0])
    }

    private fun atan2(i: Int, j: Int): Double {
        return atan2(i.toDouble(), j.toDouble())
    }

    private fun getDirectionVector(startX: Int, startY: Int, endX: Int, endY: Int): IntArray {
        return intArrayOf(endX - startX, endY - startY)
    }

    private val indicesArray = IntArray(4) { it }
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

    fun isEntityVisible(player: PlayerEntity, target: Entity): Boolean {
        if (target is ClientPlayerEntity) return false
        val camera = if (player.world.isClient) MinecraftClient.getInstance().cameraEntity
                ?: return false else (player as ServerPlayerEntity).cameraEntity
        val offset = 0.2
        val xs = arrayOf(target.boundingBox.minX - offset, target.boundingBox.minX, target.boundingBox.maxX, target.boundingBox.maxX + offset)
        val ys = arrayOf(target.boundingBox.minY - offset, target.boundingBox.minY, target.boundingBox.maxY, target.boundingBox.maxY + offset)
        val zs = arrayOf(target.boundingBox.minZ - offset, target.boundingBox.minZ, target.boundingBox.maxZ, target.boundingBox.maxZ + offset)

        return isEntityInSightOf(player, target) && boundPermute.map { Vec3d(xs[it[0]], ys[it[1]], zs[it[2]]) }
                .any { canSeeThroughEntityAt(target, it, camera.pos) }
    }

    private fun isEntityInSightOf(player: PlayerEntity, target: Entity): Boolean {
        val camera = if (player.world.isClient) MinecraftClient.getInstance().cameraEntity
                ?: return false else (player as ServerPlayerEntity).cameraEntity
        val startPoint = camera.blockPos
        val distance = 50
        val yaw = camera.yaw
        val radian = PI
        val startPos2d = intArrayOf(startPoint.x, startPoint.z)
        val deg2rad = 0.017453292519943295
        val endPos2d = intArrayOf((distance * cos(yaw * deg2rad - radian / 2)).toInt(), (distance * sin(yaw * deg2rad - radian / 2)).toInt())

        val entityVector: IntArray = getDirectionVector(startPos2d[0], startPos2d[1], target.blockPos.x, target.blockPos.y)
        val angle: Double = getAngleBetweenVectors(endPos2d, entityVector)
        return angle in 0.0..radian
    }

    private fun canSeeThroughEntityAt(sourceEntity: Entity, sourcePos: Vec3d, targetPos: Vec3d): Boolean {
        val world = sourceEntity.world

        for (rayBlock in Vec3dRayIter(sourcePos, targetPos)) {
            val block = world.getBlockState(rayBlock)

            if (block.material == Material.AIR) continue
            if (block.isTranslucent(world, rayBlock)) continue
            return false
        }
        return true
    }
}