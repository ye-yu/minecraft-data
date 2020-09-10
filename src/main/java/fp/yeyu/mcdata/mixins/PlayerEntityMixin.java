package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import fp.yeyu.mcdata.interfaces.KeyLogger;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import kotlin.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	@Shadow
	public abstract boolean isCreative();

	@SuppressWarnings("ConstantConditions")
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

		final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = getActiveStatusEffects();
		if (!activeStatusEffects.isEmpty()) {
			EncodingKey.EFFECT.serialize(writer);
			writer.push(activeStatusEffects.size());
			activeStatusEffects.forEach((effect, instance) -> {
				writer.push(StatusEffect.getRawId(effect));
				writer.push(instance.getDuration());
			});
		}

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

		ScreenHandler screenHandler = getSerializableScreenHandler();

		if (screenHandler == null) return;

		if (screenHandler.getType() != null) {
			serializeMenu(writer, ((IntIdentifiable) screenHandler.getType()), screenHandler);
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

		if (isCreative()) writer.push(-2);
		else writer.push(-1);

		writeScreenHandlerSlots(writer, screenHandler);
	}

	private void serializeMenu(ByteQueue writer, IntIdentifiable screenIdentifiable, ScreenHandler screenHandler) {
		EncodingKey.MENU.serialize(writer);
		writer.push(screenIdentifiable.getSelfRawId());
		writeScreenHandlerSlots(writer, screenHandler);
	}

	private void writeScreenHandlerSlots(ByteQueue writer, ScreenHandler screenHandler) {
		final ItemStack cursorStack = inventory.getCursorStack();
		if (!cursorStack.isEmpty()) {
			EncodingKey.MENU_CURSOR_SLOT.serialize(writer);
			writer.push(cursorStack.getCount());
			writer.push(((IntIdentifiable) cursorStack.getItem()).getSelfRawId());
		}

		final int extraSlots = screenHandler.slots.size() - inventory.main.size();
		if (extraSlots <= 0) return;
		final List<Pair<Integer, Slot>> slots = IntStream
				.range(0, extraSlots)
				.mapToObj(slot -> new Pair<>(slot, screenHandler.slots.get(slot)))
				.filter((pair -> !pair.getSecond().getStack().isEmpty())).collect(Collectors.toList());
		if (!slots.isEmpty()) {
			EncodingKey.MENU_SLOTS.serialize(writer);
			writer.push(slots.size());
			slots.forEach(pair -> {
				final int slotNumber = pair.getFirst();
				final Slot slot = pair.getSecond();
				final ItemStack stack = slot.getStack();
				writer.push(slotNumber);
				writer.push(stack.getCount());
				writer.push(((IntIdentifiable) stack.getItem()).getSelfRawId());
			});
		}
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
