package fp.yeyu.mcdata.interfaces;

import org.jetbrains.annotations.NotNull;

public interface ByteSerializable {
	void serialize(@NotNull ByteQueue writer);

	default void serialize(@NotNull ByteQueue writer, SerializationContext context) {
		serialize(writer);
	}
}
