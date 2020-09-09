package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ByteSerializable, SerializationContext {
	@Shadow
	@Final
	public PlayerInventory inventory;

	@Shadow
	public ScreenHandler currentScreenHandler;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void serialize(@NotNull ByteQueue writer) {
		EncodingKey.WORLD.serialize(writer);
		((ByteSerializable) world).serialize(writer);

		EncodingKey.BIOME.serialize(writer);
		((ByteSerializable) (Object) world.getBiome(getBlockPos())).serialize(writer, this);

		EncodingKey.UUID.serialize(writer);
		writer.push(uuid);

		EncodingKey.POSITION.serialize(writer);
		((ByteSerializable) getPos()).serialize(writer);


		Entity camera;
		if (world.isClient) {
			camera = getEither(MinecraftClient.getInstance().cameraEntity, this);
		} else {
			camera = ((ServerPlayerEntity) (Object) this).getCameraEntity();
		}

		EncodingKey.ROTATION.serialize(writer);
		writer.push(camera.pitch);
		writer.push(camera instanceof LivingEntity ? ((LivingEntity) camera).headYaw : camera.yaw);

		final HitResult hitResult = camera.rayTrace(20.0, 0f, false);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			final ByteSerializable blockPos = (ByteSerializable) ((BlockHitResult) hitResult).getBlockPos();
			EncodingKey.FOCUS.serialize(writer);
			blockPos.serialize(writer, this);
		}

		EncodingKey.INVENTORY.serialize(writer);
		((ByteSerializable) inventory).serialize(writer);

		EncodingKey.CURSOR.serialize(writer);
		writer.push(inventory.selectedSlot);

		/* todo: mouse, menu inventory, cursor slot */
		ScreenHandler screenHandler = getSerializableScreenHandler();

		if (screenHandler == null) return;

		if (screenHandler.getType() != null) {
			serializeMenu(writer, ((IntIdentifiable) screenHandler.getType()));
		} else {
			serializeCreativeMenu(writer, screenHandler);
		}
	}

	private void serializeCreativeMenu(ByteQueue writer, ScreenHandler screenHandler) {
		EncodingKey.MENU.serialize(writer);
		writer.push(-1);
	}

	private ScreenHandler getSerializableScreenHandler() {
		if (world.isClient) {
			if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen) {
				return ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler();
			}
		}

		if (!world.isClient) return currentScreenHandler;
		return null;
	}

	private void serializeMenu(ByteQueue buffer, IntIdentifiable screenIdentifiable) {
		EncodingKey.MENU.serialize(buffer);
		buffer.push(screenIdentifiable.getSelfRawId());
	}

	@NotNull
	private Entity getEither(@Nullable Entity priority, @NotNull Entity other) {
		if (priority == null) return other;
		return priority;
	}

	@Override
	public @Nullable World getWorldContext() {
		return world;
	}

}
