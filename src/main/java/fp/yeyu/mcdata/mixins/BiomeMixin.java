package fp.yeyu.mcdata.mixins;

import fp.yeyu.mcdata.data.VanillaBiomeType;
import fp.yeyu.mcdata.interfaces.ByteQueue;
import fp.yeyu.mcdata.interfaces.ByteSerializable;
import fp.yeyu.mcdata.interfaces.SerializationContext;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;
import java.util.Optional;

@Mixin(Biome.class)
public class BiomeMixin implements ByteSerializable {
	@Override
	public void serialize(@NotNull ByteQueue writer) {
		throw new IllegalStateException("Serialize with a context! Requires a registry manager");
	}

	@Override
	public void serialize(@NotNull ByteQueue writer, SerializationContext context) {
		final World world = Objects.requireNonNull(context.getWorldContext());
		final Optional<RegistryKey<Biome>> biomeRegistryKey = world.getRegistryManager().get(Registry.BIOME_KEY).getKey((((Biome) (Object) this)));
		if (!biomeRegistryKey.isPresent()) throw new NullPointerException();
		final RegistryKey<Biome> key = biomeRegistryKey.get();
		VanillaBiomeType.Companion.get(key).serialize(writer);
	}
}
