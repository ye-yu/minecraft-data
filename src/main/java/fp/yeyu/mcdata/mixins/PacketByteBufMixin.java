package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.interfaces.ByteQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;
import java.util.UUID;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin implements ByteQueue {

	@Shadow
	public abstract ByteBuf writeDouble(double d);

	@Shadow
	public abstract PacketByteBuf writeVarLong(long l);

	@Shadow
	public abstract PacketByteBuf writeVarInt(int i);

	@Shadow
	public abstract ByteBuf writeShort(int i);

	@Shadow
	public abstract ByteBuf writeByte(int i);

	@Shadow
	public abstract PacketByteBuf writeEnumConstant(Enum<?> enum_);

	@Shadow
	public abstract PacketByteBuf writeUuid(UUID uUID);

	@Shadow
	public abstract double readDouble();

	@Shadow
	public abstract long readVarLong();

	@Shadow
	public abstract int readInt();

	@Shadow
	public abstract short readShort();

	@Shadow
	public abstract int readVarInt();

	@Shadow
	public abstract byte readByte();

	@Shadow
	public abstract <T extends Enum<T>> T readEnumConstant(Class<T> class_);

	@Shadow
	public abstract UUID readUuid();

	@Shadow public abstract ByteBuf writeBoolean(boolean bl);

	@Shadow public abstract boolean readBoolean();

	@Shadow public abstract byte[] array();

	@Shadow public abstract ByteBuffer nioBuffer();

	@Override
	public void push(double d) {
		writeDouble(d);
	}

	@Override
	public void push(long l) {
		writeVarLong(l);
	}

	@Override
	public void push(int i) {
		writeVarInt(i);
	}

	@Override
	public void push(short s) {
		writeShort(s);
	}

	@Override
	public void push(byte b) {
		writeByte(b);
	}

	@Override
	public void push(boolean b) {
		writeBoolean(b);
	}

	@Override
	public void push(Enum<?> e) {
		writeEnumConstant(e);
	}

	@Override
	public void push(UUID uuid) {
		writeUuid(uuid);
	}

	@Override
	public double popDouble() {
		return readDouble();
	}

	@Override
	public long popLong() {
		return readVarLong();
	}

	@Override
	public int popInt() {
		return readVarInt();
	}

	@Override
	public short popShort() {
		return readShort();
	}

	@Override
	public byte popByte() {
		return readByte();
	}

	@Override
	public boolean popBoolean() {
		return readBoolean();
	}

	@Override
	public <T extends Enum<T>> T popEnum(@NotNull Class<T> e) {
		return readEnumConstant(e);
	}

	@Override
	public UUID popUUID() {
		return readUuid();
	}

	@Override
	public @NotNull ByteBuffer toByteBuffer() {
		return nioBuffer();
	}
}
