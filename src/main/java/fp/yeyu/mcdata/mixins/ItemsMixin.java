package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import fp.yeyu.mcdata.util.OrdinalMapperUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public class ItemsMixin {

	private static short currentShort = 0;

	/* todo: export byteId with corresponding item; or use Registry.ITEM.getRawId */
	@Inject(method="register(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("RETURN"))
	private static void onRegister(Identifier id, Item item, CallbackInfoReturnable<Item> cir) {
		OrdinalMapperUtil.INSTANCE.mapItem(currentShort, id.toString());
		((ShortIdentifiable) item).setShortId(currentShort++);
	}
}
