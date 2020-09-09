package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScreenHandlerType.class)
public class ScreenHandlerTypeMixin implements IntIdentifiable {
	@SuppressWarnings("rawtypes")
	@Override
	public int getSelfRawId() {
		return Registry.SCREEN_HANDLER.getRawId((((ScreenHandlerType) (Object) this)));
	}
}
