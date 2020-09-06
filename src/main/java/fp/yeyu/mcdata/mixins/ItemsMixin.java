package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public class ItemsMixin {

	private static byte currentByte = -127;

	/* todo: export byteId with corresponding item; or use Registry.ITEM.getRawId */
	@Inject(method="register(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("RETURN"))
	private static void onRegister(Identifier id, Item item, CallbackInfoReturnable<Item> cir) {
		((ByteIdentifiable) item).setByteId(currentByte++);
	}
}
