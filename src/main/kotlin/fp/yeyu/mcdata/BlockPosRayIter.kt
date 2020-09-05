package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos

class BlockPosRayIter(private val from: BlockPos, private val to: BlockPos) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    var current = BlockPos(from)
    var toBlockPos = BlockPos(to)
    var directionVector: BlockPos = to.subtract(from)
    var factor = 1.0 / NumberUtil.max(directionVector.x, directionVector.y, directionVector.z)
    var multiplier = 0

    override fun iterator(): Iterator<BlockPos> = BlockPosRayIter(from, to)

    override fun hasNext(): Boolean = current != toBlockPos

    override fun next(): BlockPos = BlockPos(from.add(directionVector.multiply(factor * multiplier++))).also { current = it }

    private fun BlockPos.multiply(factor: Double): BlockPos = BlockPos(x * factor, y * factor, z * factor)
}
