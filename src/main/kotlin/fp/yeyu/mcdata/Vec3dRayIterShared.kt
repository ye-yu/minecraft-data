package fp.yeyu.mcdata

import net.minecraft.util.math.Vec3d

/**
 * Not thread-safe
 * */
object Vec3dRayIterShared : Vec3dRayIter(Vec3d.ZERO, Vec3d.ZERO) {

    fun new(from: Vec3d, to: Vec3d): Vec3dRayIterShared {
        this.from = from
        this.to = to
        return this
    }


}