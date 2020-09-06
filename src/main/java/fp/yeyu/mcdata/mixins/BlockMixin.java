package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements ByteIdentifiable {

	private byte byteId = -127;

	@Override
	public void setByteId(byte byteId) {
		this.byteId = byteId;
	}

	@Override
	public byte getByteId() {
		return byteId;
	}
}
