package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements ShortIdentifiable {
	@Override
	public int getSelfRawId() {
		return Registry.BLOCK.getRawId((((Block) (Object) this)));
	}
}
