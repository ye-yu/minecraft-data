package fp.yeyu.mcdata

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fp.yeyu.mcdata.util.FileUtil
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

@Suppress("unused")
class ConfigFile(
        val useRawId: Boolean = true,
        val ringBufferSize: Int = 16,
        val useHeapBuffer: Boolean = false,
        val bufferInitialCapacity: Int = 4096
) {

    fun writeToModFile() {
        JsonWriter(FileWriter(CONFIG_JSON)).use {
            it.setIndent("  ")
            it.beginObject()

            ConfigFile::class.memberProperties.forEach { prop: KProperty1<ConfigFile, *> ->
                it.name(prop.name)

                val propValue = prop.get(this)
                when (prop.returnType) {
                    Int::class.createType() -> it.value(propValue as Int)
                    Boolean::class.createType() -> it.value(propValue as Boolean)
                    else -> throw ClassCastException("Unable to cast ${prop.name} of type ${prop.returnType} yet.")
                }
            }
            it.endObject()
        }
    }

    companion object {
        private val DEFAULT = ConfigFile()
        private val CONFIG_JSON = File(FileUtil.configDirectoryInstance, "config.json")
        val configuration: ConfigFile = deserialize() // deserialize once

        private fun deserialize(): ConfigFile {
            val config = if (!CONFIG_JSON.exists()) DEFAULT
            else {
                val propMap = mutableMapOf<String, Any?>()
                with(JsonParser().parse(JsonReader(FileReader(CONFIG_JSON)))) {
                    this.asJsonObject.run {

                        ConfigFile::class.memberProperties.forEach {

                            val name = it.name
                            val def = it.get(DEFAULT)
                            val value = getOrNullPrimitive(name, this)

                            when (it.returnType) {
                                Int::class.createType() -> {
                                    val toEntry: Any = value?.asInt ?: Int::class.cast(def)
                                    propMap[name] = toEntry
                                    println("Deserialized $name to $toEntry")
                                }

                                Boolean::class.createType() -> {
                                    val toEntry: Any = value?.asBoolean ?: Boolean::class.cast(def)
                                    propMap[name] = toEntry
                                    println("Deserialized $name to $toEntry")
                                }

                                else -> throw ClassCastException("Property $name expects ${it.returnType}.")
                            }
                        }

                        val primaryConstructor = ConfigFile::class.primaryConstructor!!
                        primaryConstructor.callBy(
                                propMap.map {
                                    primaryConstructor.findParameterByName(it.key)!! to it.value
                                }.toMap()
                        )
                    }
                }
            }

            return config.also {
                it.writeToModFile()
            }
        }

        private fun getOrNullPrimitive(field: String, jsonObject: JsonObject): JsonPrimitive? {
            val get = jsonObject.get(field)
            return if (get == null || get.isJsonNull || !get.isJsonPrimitive) null else get.asJsonPrimitive
        }

        @Suppress("UNCHECKED_CAST")
        operator fun <T> getValue(any: Any, property: KProperty<*>): T {
            return ConfigFile::class.memberProperties.first { it.name == property.name }.get(configuration) as T
        }
    }
}
