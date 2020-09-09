package fp.yeyu.mcdata.data

import fp.yeyu.mcdata.interfaces.IntIdentifiable
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

enum class VanillaWorldType(val dimension: RegistryKey<World>) : IntIdentifiable {
    OVERWORLD(World.OVERWORLD),
    NETHER(World.NETHER),
    END(World.END);

    override fun getSelfRawId(): Int = ordinal

    companion object {
        operator fun get(dimension: RegistryKey<World>): VanillaWorldType =
                values().first { it.dimension == dimension }
    }
}