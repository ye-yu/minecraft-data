package fp.yeyu.mcdata.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ByteQueue {

	void push(double d);

	void push(long l);

	void push(int i);

	void push(short s);

	void push(byte b);

	void push(boolean b);

	void push(Enum<?> e);

	void push(UUID uuid);

	double popDouble();

	long popLong();

	int popInt();

	short popShort();

	byte popByte();

	boolean popBoolean();

	<T extends Enum<T>> T popEnum(@NotNull Class<T> e);

	UUID popUUID();


}
