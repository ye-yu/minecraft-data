package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos
import kotlin.properties.Delegates

class BlockPosRayIter(from: BlockPos, to: BlockPos) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    lateinit var current: BlockPos
    private lateinit var directionVector: BlockPos
    private var factor by Delegates.notNull<Double>()
    var multiplier = 0

    var from = from
        set(value) {
            field = value
            this.current = value
        }
    var to = to
        set(value) {
            field = value
            directionVector = value.subtract(from)
            factor = 1.0 / NumberUtil.max(directionVector.x, directionVector.y, directionVector.z)
            multiplier = 0
        }

    override fun iterator(): Iterator<BlockPos> = this

    override fun hasNext(): Boolean = current != to

    override fun next(): BlockPos = BlockPos(from.add(directionVector.multiply(factor * multiplier++))).also { current = it }

    private fun BlockPos.multiply(factor: Double): BlockPos = BlockPos(x * factor, y * factor, z * factor)
}
