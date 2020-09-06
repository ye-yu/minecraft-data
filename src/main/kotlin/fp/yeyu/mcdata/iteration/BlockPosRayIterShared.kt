package fp.yeyu.mcdata.iteration

import fp.yeyu.mcdata.iteration.BlockPosRayIter
import net.minecraft.util.math.BlockPos

/**
 * Not thread-safe
 * */
object BlockPosRayIterShared: BlockPosRayIter(BlockPos.ORIGIN, BlockPos.ORIGIN) {

    fun new(from: BlockPos, to: BlockPos): BlockPosRayIter {
        this.from = from
        this.to = to
        return this
    }
}
