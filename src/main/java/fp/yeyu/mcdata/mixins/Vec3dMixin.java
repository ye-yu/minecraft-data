package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vec3d.class)
public class Vec3dMixin implements ByteSerializable {
	@Override
	public void serialize(@NotNull ByteQueue writer) {
		final Vec3d vec3d = (((Vec3d) (Object) this));
		writer.push(vec3d.getX());
		writer.push(vec3d.getY());
		writer.push(vec3d.getZ());
	}
}
