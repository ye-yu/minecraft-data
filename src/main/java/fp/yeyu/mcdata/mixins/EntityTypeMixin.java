package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin implements ByteIdentifiable {

	private static byte currentByte = 0;
	private byte byteId = -1;

	@Inject(method = "register", at = @At("RETURN"))
	private static <T extends Entity> void onRegister(String id, EntityType.Builder<T> type, CallbackInfoReturnable<EntityType<T>> cir) {
		((ByteIdentifiable) cir.getReturnValue()).setByteId(currentByte++);
	}

	@Override
	public byte getByteId() {
		return byteId;
	}

	@Override
	public void setByteId(byte byteId) {
		this.byteId = byteId;
	}
}
