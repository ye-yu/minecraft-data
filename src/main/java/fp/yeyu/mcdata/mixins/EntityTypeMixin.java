package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import fp.yeyu.mcdata.util.OrdinalMapperUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin implements ShortIdentifiable {

	private static short currentShort = 0;
	private short shortId = -1;

	@Inject(method = "register", at = @At("RETURN"))
	private static <T extends Entity> void onRegister(String id, EntityType.Builder<T> type, CallbackInfoReturnable<EntityType<T>> cir) {
		OrdinalMapperUtil.INSTANCE.mapMob(currentShort, id);
		((ShortIdentifiable) cir.getReturnValue()).setShortId(currentShort++);
	}

	@Override
	public short getShortId() {
		return shortId;
	}

	@Override
	public void setShortId(short shortId) {
		this.shortId = shortId;
	}
}
