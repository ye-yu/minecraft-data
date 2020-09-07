package fp.yeyu.mcdata

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.util.FileUtil
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.reflect.KProperty

class ConfigFile private constructor(
        val useRawId: Boolean = true
) {
    fun writeToModFile() {
        JsonWriter(FileWriter(CONFIG_JSON)).use {
            it.setIndent("  ")
            it.beginObject()
            it.name("useRawId")
            it.value(useRawId)
            it.endObject()
        }
    }

    override fun toString(): String {
        return "Config[useRawId{$useRawId}]"
    }

    companion object {
        private val DEFAULT = ConfigFile()
        private val CONFIG_JSON = File(FileUtil.configDirectoryInstance, "config.json")

        private fun deserialize(): ConfigFile {
            val config = if (!CONFIG_JSON.exists()) DEFAULT
            else {
                with(JsonParser().parse(JsonReader(FileReader(CONFIG_JSON)))) {
                    this.asJsonObject.run {
                        val useRawId = getOrDefaultPrimitives("useRawId", this, JsonElement::getAsBoolean, DEFAULT.useRawId)
                        ConfigFile(useRawId)
                    }
                }
            }

            return config.also {
                it.writeToModFile()
            }
        }

        private fun <T> getOrDefaultPrimitives(@Suppress("SameParameterValue") field: String, jsonObject: JsonObject, getter: (JsonElement) -> T, default: T): T {
            if (!jsonObject.has(field)) return default
            return with(jsonObject.get(field)) {
                if (!this.isJsonPrimitive) default
                else try {
                    getter(this)
                } catch (cce: ClassCastException) {
                    default
                }
            }
        }

        operator fun getValue(playData: PlayData, property: KProperty<*>): ConfigFile {
            return deserialize()
        }
    }
}