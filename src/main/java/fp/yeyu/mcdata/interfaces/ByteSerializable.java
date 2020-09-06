package fp.yeyu.mcdata.interfaces;

import fp.yeyu.mcdata.data.EncodingKey;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public interface ByteSerializable {

	/**
	 * Always call to super
	 * */
	default void serialize(@NotNull PacketByteBuf buffer) {
		buffer.writeVarInt(getKey().getByte());
	}

	EncodingKey getKey();
}
