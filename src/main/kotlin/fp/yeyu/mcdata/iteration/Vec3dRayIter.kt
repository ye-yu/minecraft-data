package fp.yeyu.mcdata.iteration

import fp.yeyu.mcdata.util.NumberUtil
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.properties.Delegates

open class Vec3dRayIter(from: Vec3d, to: Vec3d) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    private var current = BlockPos.ORIGIN.mutableCopy()
    private lateinit var toBlockPos: BlockPos
    private lateinit var directionVector: Vec3d
    private var factor by Delegates.notNull<Double>()
    var multiplier = 0

    var from: Vec3d = from
        set(value) {
            field = value
            current.set(value)
        }
    var to: Vec3d = to
        set(value) {
            field = value
            toBlockPos = BlockPos(value)
            directionVector = value.subtract(from)
            factor = 1.0 / NumberUtil.max(directionVector.x, directionVector.y, directionVector.z)
            multiplier = 0
        }
    
    private val isOutOfRange: Boolean
        get() = when {
            (current.x - from.x) > (to.x - from.x) -> true
            (current.y - from.y) > (to.y - from.y) -> true
            (current.z - from.z) > (to.z - from.z) -> true
            else -> false
        }

    override fun iterator(): Iterator<BlockPos> = this

    override fun hasNext(): Boolean = current != toBlockPos

    override fun next(): BlockPos = current.also {
        it.x = (from.x * directionVector.x * factor * multiplier).toInt()
        it.y = (from.y * directionVector.y * factor * multiplier).toInt()
        it.z = (from.z * directionVector.z * factor * multiplier).toInt()
        multiplier++
        
        if (isOutOfRange) {
            current.set(toBlockPos)
        }
    }

    private fun BlockPos.set(value: Vec3d) {
        x = value.x.toInt()
        y = value.y.toInt()
        z = value.z.toInt()
    }
}
