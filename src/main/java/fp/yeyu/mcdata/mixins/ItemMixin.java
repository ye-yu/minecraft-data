package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ByteIdentifiable {

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
