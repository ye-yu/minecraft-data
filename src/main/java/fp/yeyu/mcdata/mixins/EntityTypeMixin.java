package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class EntityTypeMixin implements IntIdentifiable {
	@SuppressWarnings("rawtypes")
	@Override
	public int getSelfRawId() {
		return Registry.ENTITY_TYPE.getRawId((((EntityType) (Object) this)));
	}
}
