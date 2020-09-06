package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos
import kotlin.properties.Delegates

open class BlockPosRayIter(from: BlockPos, to: BlockPos) : Iterable<BlockPos>, Iterator<BlockPos> {

    var next = false
    lateinit var current: BlockPos.Mutable
    private lateinit var directionVector: BlockPos
    private var factor by Delegates.notNull<Double>()
    var multiplier = 0

    var from = from
        set(value) {
            field = value
            this.current = value.mutableCopy()
        }
    var to = to
        set(value) {
            field = value
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

    override fun hasNext(): Boolean = current != to

    override fun next(): BlockPos = current.also {
        it.set(
                from.x + directionVector.x * (factor * multiplier),
                from.y + directionVector.y * (factor * multiplier),
                from.z + directionVector.z * (factor * multiplier)
        )
        multiplier++

        if (isOutOfRange) {
            current.set(to)
        }

    }

}
