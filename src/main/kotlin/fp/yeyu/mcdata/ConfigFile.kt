package fp.yeyu.mcdata

import kotlinx.serialization.Serializable

@Serializable
data class ConfigFile(
        val useRawId: Boolean = true,
        val useRuntimeMappedId: Boolean = false
)