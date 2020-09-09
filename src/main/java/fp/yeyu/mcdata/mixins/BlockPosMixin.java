package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import fp.yeyu.mcdata.interfaces.IntIdentifiable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(BlockPos.class)
public class BlockPosMixin implements ByteSerializable {
	@Override
	public void serialize(@NotNull ByteQueue writer) {
		final BlockPos blockPos = (((BlockPos) (Object) this));
		writer.push(blockPos.getX());
		writer.push(blockPos.getY());
		writer.push(blockPos.getZ());
	}

	@Override
	public void serialize(@NotNull ByteQueue writer, SerializationContext context) {
		final World world = context.getWorldContext();
		final BlockPos blockPos = (((BlockPos) (Object) this));
		final BlockState blockState = Objects.requireNonNull(world).getBlockState((((BlockPos) (Object) this)));
		final IntIdentifiable block = (IntIdentifiable) blockState.getBlock();
		writer.push(block.getSelfRawId());
		writer.push(blockPos.getX());
		writer.push(blockPos.getY());
		writer.push(blockPos.getZ());
	}
}
