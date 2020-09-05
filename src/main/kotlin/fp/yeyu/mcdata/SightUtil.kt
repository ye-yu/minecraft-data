package fp.yeyu.mcdata

import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


object SightUtil {

    /* fixme */
    fun getEntitiesInSight(entities: List<Entity>, startPoint: Vec3d, distance: Double, radian: Double, yaw: Double): MutableList<Entity> {
        val newEntities: MutableList<Entity> = ArrayList<Entity>()

        val startPos2d = intArrayOf(startPoint.x.toInt(), startPoint.z.toInt())
        val deg2rad = 0.017453292
        val endPos2d = intArrayOf((distance * cos(yaw * deg2rad  - radian / 2)).toInt(), (distance * sin(yaw * deg2rad - radian / 2)).toInt())

        for (entity in entities) {
            val entityVector: IntArray = getVectorForPoints(startPos2d[0], startPos2d[1], entity.blockPos.x, entity.blockPos.y)
            val angle: Double = getAngleBetweenVectors(endPos2d, entityVector)
            if (Math.toDegrees(angle) < angle && Math.toDegrees(angle) > 0) {
                println("Entity $entity is in sight")
                newEntities.add(entity)
            }
        }
        return newEntities
    }

    private fun getAngleBetweenVectors(vector1: IntArray, vector2: IntArray): Double {
        return atan2(vector2[1], vector2[0]) - atan2(vector1[1], vector1[0])
    }

    private fun atan2(i: Int, j: Int): Double {
        return atan2(i.toDouble(), j.toDouble())
    }

    private fun getVectorForPoints(startX: Int, startY: Int, endX: Int, endY: Int): IntArray {
        return intArrayOf(endX - startX, endY - startY)
    }


}