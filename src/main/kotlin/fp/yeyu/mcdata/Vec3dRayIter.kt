package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.properties.Delegates

open class Vec3dRayIter(from: Vec3d, to: Vec3d) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    lateinit var current: BlockPos
    private lateinit var toBlockPos: BlockPos
    private lateinit var directionVector: Vec3d
    private var factor by Delegates.notNull<Double>()
    var multiplier = 0

    var from: Vec3d = from
        set(value) {
            field = value
            this.current = BlockPos(value)
        }
    var to: Vec3d = to
        set(value) {
            field = value
            toBlockPos = BlockPos(value)
            directionVector = value.subtract(from)
            factor = 1.0 / NumberUtil.max(directionVector.x, directionVector.y, directionVector.z)
            multiplier = 0
        }

    override fun iterator(): Iterator<BlockPos> = this

    override fun hasNext(): Boolean = current != toBlockPos

    override fun next(): BlockPos = BlockPos(from.add(directionVector.multiply(factor * multiplier++))).also { current = it }
}
