package fp.yeyu.mcdata

import net.minecraft.util.math.BlockPos

object NumberUtil {
    fun <T> max(a: T, vararg other: T): T where T: Number, T: Comparable<T> {
        var top = a
        for(o in other) {
            top = if (top > o) top else o
        }
        return top
    }

}