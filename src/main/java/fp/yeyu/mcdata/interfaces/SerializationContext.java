package fp.yeyu.mcdata.interfaces;

import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface SerializationContext {
	@Nullable
	default World getWorldContext() {
		return null;
	}
}
