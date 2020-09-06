package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.EncodingKey;
import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import kotlin.Pair;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(PlayerInventory.class)
public class InventoryMixin implements ByteSerializable {

	@Override
	public void serialize(@NotNull PacketByteBuf buffer) {
		PlayerInventory inv = (PlayerInventory) (Object) this;
		final Collection<Pair<Integer, ItemStack>> itemStacks = IntStream
				.range(0, inv.size())
				.mapToObj((it) -> new Pair<>(it, inv.getStack(it)))
				.filter((it) -> !it.getSecond().isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));
		buffer.writeVarInt(itemStacks.size());

		for (Pair<Integer, ItemStack> pair : itemStacks) {
			buffer.writeVarInt(pair.getFirst());
			buffer.writeVarInt(pair.getSecond().getCount());
			buffer.writeByte(((ByteIdentifiable) pair.getSecond().getItem()).getByteId());
		}

	}
}
