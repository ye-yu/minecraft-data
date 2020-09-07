package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ShortIdentifiable {
	@Override
	public int getSelfRawId() {
		return Registry.ITEM.getRawId((((Item) (Object) this)));
	}
}
