package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class EntityMixin extends Entity implements ByteSerializable {
	public EntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	public void serialize(@NotNull ByteQueue writer) {
		final int byteId = ((ShortIdentifiable) getType()).getSelfRawId();
		writer.push(byteId);
		((ByteSerializable) getPos()).serialize(writer);
	}
}
