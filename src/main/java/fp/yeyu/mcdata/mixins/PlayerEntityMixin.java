package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import fp.yeyu.mcdata.interfaces.KeyLogger;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ByteSerializable, SerializationContext {
	@Shadow
	@Final
	public PlayerInventory inventory;

	@Shadow
	public ScreenHandler currentScreenHandler;

	@Shadow
	protected HungerManager hungerManager;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void serialize(@NotNull ByteQueue writer) {
		EncodingKey.WORLD.serialize(writer);
		((ByteSerializable) world).serialize(writer);

		EncodingKey.BIOME.serialize(writer);
		((ByteSerializable) (Object) world.getBiome(getBlockPos())).serialize(writer, this);

		EncodingKey.POSITION.serialize(writer);
		((ByteSerializable) getPos()).serialize(writer);

		EncodingKey.HEALTH.serialize(writer);
		writer.push(getHealth());
		writer.push(hungerManager.getFoodLevel());

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

		final KeyLogger keyLogger = (KeyLogger) MinecraftClient.getInstance().currentScreen;
		if (keyLogger == null) return;
		final ArrayList<Integer> pressedKeys = keyLogger.getPressedKeys();

		if (!pressedKeys.isEmpty()) {
			EncodingKey.KEYBOARD.serialize(writer);
			writer.push(pressedKeys.size());
			pressedKeys.forEach(writer::push);
		}

		final ArrayList<Integer> pressedMouseButton = keyLogger.getPressedMouseButton();
		if (!pressedMouseButton.isEmpty()) {
			EncodingKey.MOUSE.serialize(writer);
			writer.push(pressedMouseButton.size());
			pressedMouseButton.forEach(writer::push);
			pressedMouseButton.clear();
		}

		EncodingKey.MOUSE_POSITION.serialize(writer);
		writer.push(keyLogger.getMouseX());
		writer.push(keyLogger.getMouseY());
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
