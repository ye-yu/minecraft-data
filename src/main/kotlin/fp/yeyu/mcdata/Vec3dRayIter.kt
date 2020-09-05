package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class Vec3dRayIter(private val from: Vec3d, private val to: Vec3d) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    var current = BlockPos(from)
    var toBlockPos = BlockPos(to)
    var directionVector: Vec3d = to.subtract(from)
    var factor = 1.0 / NumberUtil.max(directionVector.x, directionVector.y, directionVector.z)
    var multiplier = 0

    override fun iterator(): Iterator<BlockPos> = Vec3dRayIter(from, to)

    override fun hasNext(): Boolean = current != toBlockPos

    override fun next(): BlockPos = BlockPos(from.add(directionVector.multiply(factor * multiplier++))).also { current = it }
}
