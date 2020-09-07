package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class EntityMixin extends Entity implements ByteSerializable {
	public EntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	public void serialize(@NotNull PacketByteBuf buffer) {
		final byte byteId = ((ByteIdentifiable) getType()).getByteId();
		buffer.writeByte(byteId);
		((ByteSerializable) getPos()).serialize(buffer);
	}
}