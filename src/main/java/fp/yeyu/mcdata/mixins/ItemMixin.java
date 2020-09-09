package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements IntIdentifiable {
	@Override
	public int getSelfRawId() {
		return Registry.ITEM.getRawId((((Item) (Object) this)));
	}
}
