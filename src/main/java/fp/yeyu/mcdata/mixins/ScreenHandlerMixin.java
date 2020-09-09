package fp.yeyu.mcdata.mixins;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

	@Shadow @Final private ScreenHandlerType<?> type;

	@Inject(method = "getType", at = @At("INVOKE"), cancellable = true)
	void getTypeMixin(CallbackInfoReturnable<ScreenHandlerType<?>> cir) {
		if (type != null) return;
		cir.setReturnValue(null);
	}
}
