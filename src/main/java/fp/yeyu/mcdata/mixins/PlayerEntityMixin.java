package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ByteSerializable, SerializationContext {
	@Shadow @Final public PlayerInventory inventory;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void serialize(@NotNull PacketByteBuf buffer) {
		EncodingKey.UUID.serialize(buffer);
		buffer.writeUuid(uuid);

		EncodingKey.POSITION.serialize(buffer);
		((ByteSerializable) getPos()).serialize(buffer);


		Entity camera;
		if (world.isClient) {
			camera = getEither(MinecraftClient.getInstance().cameraEntity, this);
		} else {
			camera = ((ServerPlayerEntity) (Object) this).getCameraEntity();
		}

		EncodingKey.ROTATION.serialize(buffer);
		buffer.writeDouble(camera.pitch);
		buffer.writeDouble(camera instanceof LivingEntity ? ((LivingEntity) camera).headYaw : camera.yaw);

		final HitResult hitResult = camera.rayTrace(20.0, 0f, false);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			final ByteSerializable blockPos = (ByteSerializable) ((BlockHitResult) hitResult).getBlockPos();
			EncodingKey.FOCUS.serialize(buffer);
			blockPos.serialize(buffer, this);
		}

		EncodingKey.INVENTORY.serialize(buffer);
		((ByteSerializable) inventory).serialize(buffer);

		EncodingKey.CURSOR.serialize(buffer);
		buffer.writeVarInt(inventory.selectedSlot);

		/* todo: add opened menu, mouse, menu inventory, cursor slot */
	}

	@NotNull
	private Entity getEither(@Nullable Entity priority, @NotNull Entity other) {
		if (priority == null) return other;
		return priority;
	}

	@Override
	public @Nullable World getWorld() {
		return world;
	}
}
