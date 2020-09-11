package fp.yeyu.mcdata.util

import fp.yeyu.mcdata.iteration.Vec3dRayIterShared
import net.minecraft.block.Material
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.stream.IntStream
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


object SightUtil {

    private fun getAngleBetweenVectors(vector1: IntArray, vector2: IntArray): Double {
        return atan2(vector2[1], vector2[0]) - atan2(vector1[1], vector1[0])
    }

    private fun atan2(i: Int, j: Int): Double {
        return atan2(i.toDouble(), j.toDouble())
    }

    private fun getDirectionVector(startX: Int, startY: Int, endX: Int, endY: Int): IntArray {
        return intArrayOf(endX - startX, endY - startY)
    }

    private val entityBoundingIndices = IntArray(4) { it }
    private val entityBoundingPermute: Array<IntArray> = IntStream.range(0, 2)
            .mapToObj { it to entityBoundingIndices }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first, it)
                }.stream()
            }.map { it to entityBoundingIndices }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first[0], flat.first[1], it)
                }.stream()
            }.toArray {
                arrayOfNulls<IntArray>(it)
            }

    fun isEntityVisible(camera: Entity, target: Entity): Boolean {
        if (target is ClientPlayerEntity) return false
        val offset = 0.2
        val xs = arrayOf(target.boundingBox.minX - offset, target.boundingBox.minX, target.boundingBox.maxX, target.boundingBox.maxX + offset)
        val ys = arrayOf(target.boundingBox.minY - offset, target.boundingBox.minY, target.boundingBox.maxY, target.boundingBox.maxY + offset)
        val zs = arrayOf(target.boundingBox.minZ - offset, target.boundingBox.minZ, target.boundingBox.maxZ, target.boundingBox.maxZ + offset)

        return isEntityInFOV(camera, target) && entityBoundingPermute.map { Vec3d(xs[it[0]], ys[it[1]], zs[it[2]]) }
                .any { canSeeThroughEntityAt(target, it, camera.pos) }
    }

    private fun isEntityInFOV(camera: Entity, target: Entity): Boolean {
        return isBlockInFOV(camera, target.blockPos)
    }

    private fun isBlockInFOV(camera: Entity, target: BlockPos): Boolean {
        val startPoint = camera.blockPos
        val distance = 120
        val yaw = camera.yaw
        val radian = PI
        val startPos2d = intArrayOf(startPoint.x, startPoint.z)
        val deg2rad = 0.017453292519943295
        val endPos2d = intArrayOf((distance * cos(yaw * deg2rad - radian / 2)).toInt(), (distance * sin(yaw * deg2rad - radian / 2)).toInt())
        val entityVector: IntArray = getDirectionVector(startPos2d[0], startPos2d[1], target.x, target.y)
        val angle = wrapRadian(getAngleBetweenVectors(endPos2d, entityVector))
        return angle in 0.0..radian
    }

    private tailrec fun wrapRadian(rad: Double): Double {
        if (rad > PI) return wrapRadian(rad - PI)
        else if (rad < -PI) return wrapRadian(rad + PI)
        return rad
    }

    private fun canSeeThroughEntityAt(sourceEntity: Entity, sourcePos: Vec3d, targetPos: Vec3d): Boolean {
        val world = sourceEntity.world

        for (rayBlock in Vec3dRayIterShared.new(sourcePos, targetPos)) {
            val block = world.getBlockState(rayBlock)
            if (block.material == Material.AIR) continue
            if (block.isTranslucent(world, rayBlock)) continue
            return false
        }
        return true
    }

    private val blockIndices = IntArray(2) { it }
    private val blockCornerPermute: Array<IntArray> = IntStream.range(0, 2)
            .mapToObj { it to blockIndices }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first, it)
                }.stream()
            }.map { it to blockIndices }
            .flatMap { flat ->
                flat.second.map {
                    intArrayOf(flat.first[0], flat.first[1], it)
                }.stream()
            }.toArray {
                arrayOfNulls<IntArray>(it)
            }

    fun isBlockVisible(camera: Entity, targetPos: BlockPos): Boolean {
        val xs = arrayOf(targetPos.x, targetPos.x + 1)
        val ys = arrayOf(targetPos.y, targetPos.y + 1)
        val zs = arrayOf(targetPos.z, targetPos.z + 1)

        return isBlockInFOV(camera, targetPos) && blockCornerPermute
                .map { BlockPos(xs[it[0]], ys[it[1]], zs[it[2]]) }
                .any { canSeeThroughEntityAt(camera, camera.pos, it.toVed3d()) }
    }


    private fun BlockPos.toVed3d(): Vec3d = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())


}