package fp.yeyu.mcdata.interfaces;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public interface ByteSerializable {
	void serialize(@NotNull PacketByteBuf buffer);

	default void serialize(@NotNull PacketByteBuf buffer, SerializationContext context) {
		serialize(buffer);
	}
}
