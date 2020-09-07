object Jetbrains {

  object Kotlin {
    const val version = "1.4.0"
    const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

    private const val annotationsVersion = "20.0.0"
    const val annotations = "org.jetbrains:annotations:$annotationsVersion"
  }

  object Kotlinx {
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.9"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.9"
    const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC"
    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0"
  }
}

object Mods {
  const val modmenu = "io.github.prospector:modmenu:1.14.5+build.+"
}

object Fabric {

  object Kotlin {
    const val version = "${Jetbrains.Kotlin.version}+build.+"
  }

  object Loader {
    const val version = "0.9.2+build.+" // https://maven.fabricmc.net/net/fabricmc/fabric-loader/
  }

  object API {
    const val version = "0.19.0+build.398-1.16"
  }

  object Loom {
    const val version = "0.4-SNAPSHOT"
  }

  object YarnMappings {
    const val version = "${Minecraft.version}+build.21"
  }
}

object Minecraft {
  const val version = "1.16.2"
}

object CurseGradle {
  const val version = "1.4.0"
}
