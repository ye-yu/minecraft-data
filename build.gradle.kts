plugins {
  kotlin("jvm") version Jetbrains.Kotlin.version
  kotlin("plugin.serialization") version Jetbrains.Kotlin.version
  id("fabric-loom") version Fabric.Loom.version
  id("com.matthewprenger.cursegradle") version CurseGradle.version
  `maven-publish`
}

group = Info.group
version = Info.version

repositories {
  maven(url = "http://maven.fabricmc.net") { name = "Fabric" }
  maven(url = "https://libraries.minecraft.net/") { name = "Mojang" }
  maven(url = "https://kotlin.bintray.com/kotlinx/") { name = "Kotlinx" }
  mavenCentral()
  jcenter()
}

minecraft {
  accessWidener("src/main/resources/playdata.accesswidener")
}

dependencies {
  minecraft("com.mojang", "minecraft", Minecraft.version)
  mappings("net.fabricmc", "yarn", Fabric.YarnMappings.version, classifier = "v2")

  modImplementation("net.fabricmc", "fabric-loader", Fabric.Loader.version)
  modImplementation("net.fabricmc", "fabric-language-kotlin", Fabric.Kotlin.version)
  modImplementation("net.fabricmc.fabric-api", "fabric-api", Fabric.API.version)

  modImplementation(Mods.modmenu)

//  includeApi(Jetbrains.Kotlin.stdLib)
//  includeApi(Jetbrains.Kotlin.reflect)
//  includeApi(Jetbrains.Kotlin.annotations)
//  includeApi(Jetbrains.Kotlinx.coroutines)
//  includeApi(Jetbrains.Kotlinx.coroutinesCore)
//  includeApi(Jetbrains.Kotlinx.serialization)
}

tasks {
  val sourcesJar by creating(Jar::class) {
    archiveClassifier.set("sources")

    from(sourceSets["main"].allSource)

    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
  }

  val javadocJar by creating(Jar::class) {
    archiveClassifier.set("javadoc")

    from(project.tasks["javadoc"])

    dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
  }

  compileJava {
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
  }

  compileKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.ExperimentalStdlibApi"
      )
    }
  }

  processResources {
    filesMatching("fabric.mod.json") {
      expand(
        "modId" to Info.modId,
        "name" to Info.name,
        "version" to Info.version,
        "description" to Info.description,
        "kotlinVersion" to Jetbrains.Kotlin.version,
        "minecraftVersion" to Minecraft.version,
        "fabricApiVersion" to Fabric.API.version
      )
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifacts {
        artifact(tasks["sourcesJar"]) {
          builtBy(tasks["remapSourcesJar"])
        }

        artifact(tasks["javadocJar"])
        artifact(tasks["remapJar"])
      }
    }

    repositories {
      mavenLocal()
    }
  }
}

fun DependencyHandlerScope.includeApi(notation: String) {
  include(notation)
  modApi(notation)
}
