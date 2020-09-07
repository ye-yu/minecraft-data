package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import fp.yeyu.mcdata.util.OrdinalMapperUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public class BlocksMixin {
	private static short currentId = 0;

	@Inject(method = "register", at = @At("RETURN"))
	private static void onRegister(String id, Block block, CallbackInfoReturnable<Block> cir) {
		OrdinalMapperUtil.INSTANCE.mapBlock(currentId, id);
		((ShortIdentifiable) block).setShortId(currentId++);
	}
}
