package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements ShortIdentifiable {

	private short byteId = -1;

	@Override
	public void setShortId(short shortId) {
		this.byteId = shortId;
	}

	@Override
	public short getShortId() {
		return byteId;
	}
}
