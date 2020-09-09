package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.VanillaWorldType;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements ByteSerializable, SerializationContext {

	@Shadow
	public abstract RegistryKey<World> getRegistryKey();

	@Override
	public void serialize(@NotNull ByteQueue writer) {
		final VanillaWorldType vanillaWorldType = VanillaWorldType.Companion.get(getRegistryKey());
		writer.push(vanillaWorldType.getSelfRawId());
	}

	@Override
	public @Nullable World getWorldContext() {
		return (((World) (Object) this));
	}
}
