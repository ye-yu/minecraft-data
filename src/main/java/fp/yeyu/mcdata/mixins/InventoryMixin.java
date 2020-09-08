package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.ShortIdentifiable;
import kotlin.Pair;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(PlayerInventory.class)
public class InventoryMixin implements ByteSerializable {

	@Override
	public void serialize(@NotNull ByteQueue writer) {
		PlayerInventory inv = (PlayerInventory) (Object) this;
		final Collection<Pair<Integer, ItemStack>> itemStacks = IntStream
				.range(0, inv.size())
				.mapToObj((it) -> new Pair<>(it, inv.getStack(it)))
				.filter((it) -> !it.getSecond().isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));
		writer.push(itemStacks.size());

		for (Pair<Integer, ItemStack> pair : itemStacks) {
			writer.push(pair.getFirst());
			writer.push(pair.getSecond().getCount());
			writer.push(((ShortIdentifiable) pair.getSecond().getItem()).getSelfRawId());
		}

	}
}
