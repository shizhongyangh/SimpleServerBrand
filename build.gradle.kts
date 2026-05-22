import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    `maven-publish`

    id("com.gradleup.shadow") version "9.4.1"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "dev.slne.surf.serverbrandcustomizer"
version = findProperty("version") as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1+")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
}

paper {
    main = "dev.slne.surf.serverbrandcustomizer.SurfServerbrandCustomizer"
    apiVersion = "26.1"
    authors = listOf("twisti")
    description = "Customize the server brand displayed to the player in the F3 menu"
    foliaSupported = true

    serverDependencies {
        register("packetevents") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("26.1.2")
        downloadPlugins {
            modrinth("packetevents", "2.12.1+spigot")
            modrinth("viaversion", "5.6.0")
        }
    }

    shadowJar {
        archiveClassifier = ""
    }
}

publishing {
    repositories {
        maven("https://reposilite.slne.dev/releases/") {
            name = "slne-repository-releases"
            credentials {
                username = System.getenv("SLNE_RELEASES_REPO_USERNAME")
                password = System.getenv("SLNE_RELEASES_REPO_PASSWORD")
            }
        }
    }

    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

val targetJavaVersion = 25
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}