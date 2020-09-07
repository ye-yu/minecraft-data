package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ShortIdentifiable {

	private short shortId = -1;

	@Override
	public void setShortId(short shortId) {
		this.shortId = shortId;
	}

	@Override
	public short getShortId() {
		return shortId;
	}
}
