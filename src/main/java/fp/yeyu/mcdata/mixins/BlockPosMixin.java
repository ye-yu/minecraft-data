package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteIdentifiable;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(BlockPos.class)
public class BlockPosMixin implements ByteSerializable {
	@Override
	public void serialize(@NotNull PacketByteBuf buffer) {
		final BlockPos blockPos = (((BlockPos) (Object) this));
		buffer.writeVarInt(blockPos.getX());
		buffer.writeVarInt(blockPos.getY());
		buffer.writeVarInt(blockPos.getZ());
	}

	@Override
	public void serialize(@NotNull PacketByteBuf buffer, SerializationContext context) {
		final World world = context.getWorld();
		final BlockPos blockPos = (((BlockPos) (Object) this));
		final BlockState blockState = Objects.requireNonNull(world).getBlockState((((BlockPos) (Object) this)));
		final ByteIdentifiable block = (ByteIdentifiable) blockState.getBlock();
		buffer.writeByte(block.getByteId());
		buffer.writeVarInt(blockPos.getX());
		buffer.writeVarInt(blockPos.getY());
		buffer.writeVarInt(blockPos.getZ());
	}
}
