package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteSerializable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Vec3d.class)
public class Vec3dMixin implements ByteSerializable {
	@Override
	public void serialize(@NotNull PacketByteBuf buffer) {
		final Vec3d vec3d = (((Vec3d) (Object) this));
		buffer.writeDouble(vec3d.getX());
		buffer.writeDouble(vec3d.getY());
		buffer.writeDouble(vec3d.getZ());
	}
}
