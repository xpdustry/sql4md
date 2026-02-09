import com.xpdustry.toxopid.extension.anukeXpdustry
import com.xpdustry.toxopid.spec.ModDependency
import com.xpdustry.toxopid.spec.ModMetadata
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec
import java.util.Properties

plugins {
    id("com.diffplug.spotless") version "8.2.1"
    id("net.kyori.indra") version "4.0.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("com.xpdustry.toxopid") version "4.2.0"
}

version = "2.0.2" + if (findProperty("release").toString().toBoolean()) "" else "-SNAPSHOT"
group = "com.xpdustry"
description = "A collection of plugins providing SQL drivers for other plugins."

val mindustryVersion = "154"

val drivers =
    listOf(
        Triple("sqlite", "org.sqlite.JDBC", "org.xerial:sqlite-jdbc:3.51.1.0"),
        Triple("h2", "org.h2.Driver", "com.h2database:h2:2.4.240"),
        Triple("mariadb", "org.mariadb.jdbc.Driver", "org.mariadb.jdbc:mariadb-java-client:3.5.7"),
        Triple("mysql", "com.mysql.cj.jdbc.Driver", "com.mysql:mysql-connector-j:9.6.0"),
        Triple("postgresql", "org.postgresql.Driver", "org.postgresql:postgresql:42.7.9"),
    )

val downloadSlf4md by tasks.registering(GithubAssetDownload::class) {
    owner = "xpdustry"
    repo = "slf4md"
    asset = "slf4md.jar"
    version = "v1.2.0"
}

val dist by tasks.registering(Copy::class) {
    destinationDir = temporaryDir
}

tasks.runMindustryServer {
    mods.setFrom(downloadSlf4md)
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.kyori.indra")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "com.xpdustry.toxopid")

    version = rootProject.version
    group = rootProject.group
    description = rootProject.description

    indra {
        javaVersions {
            target(17)
            minimumToolchain(17)
        }
    }

    toxopid {
        compileVersion = "v$mindustryVersion"
        platforms = setOf(ModPlatform.SERVER)
    }

    repositories {
        mavenCentral()
        anukeXpdustry()
    }

    dependencies {
        compileOnly(toxopid.dependencies.mindustryCore)
        compileOnly(toxopid.dependencies.arcCore)
    }

    spotless {
        java {
            palantirJavaFormat()
            forbidWildcardImports()
            importOrder("", "\\#")
            licenseHeader("// SPDX-License-Identifier: MIT")
        }
        kotlinGradle {
            ktlint()
        }
    }

    tasks.withType<MindustryExec> {
        jvmArguments.add("--enable-native-access=ALL-UNNAMED")
    }
}

for ((identifier, driver, library) in drivers) {
    project(":sql4md-$identifier") {
        val metadata =
            ModMetadata(
                displayName = "SQL4MD-${identifier.uppercase()}",
                name = "sql4md-$identifier",
                description = "$description. This implementation provides the $identifier driver.",
                version = version.toString(),
                author = "xpdustry",
                repository = "xpdustry/sql4md",
                mainClass = "com.xpdustry.sql4md.$identifier.DriverLoader",
                minGameVersion = mindustryVersion,
                java = true,
                hidden = true,
                dependencies = mutableListOf(ModDependency("slf4md", soft = true)),
            )

        dependencies {
            runtimeOnly(library)
            implementation(rootProject)
        }

        configurations.runtimeClasspath {
            exclude(group = "org.slf4j")
            exclude(group = "com.google.errorprone")
            exclude(group = "org.checkerframework")
        }

        val generateResourceFiles by tasks.registering {
            inputs.property("metadata", metadata)
            outputs.files(fileTree(temporaryDir))
            doLast {
                temporaryDir.resolve("plugin.json").writeText(ModMetadata.toJson(metadata))
                temporaryDir.resolve("sql4md.properties").outputStream().use { stream ->
                    val props = Properties()
                    props["sql4md.implementation.identifier"] = identifier
                    props["sql4md.implementation.driver"] = driver
                    props.store(stream, null)
                }
            }
        }

        tasks.shadowJar {
            archiveFileName = "${metadata.name}.jar"
            archiveClassifier = "plugin"
            from(generateResourceFiles)
            from(rootProject.file("LICENSE.md")) { into("META-INF") }

            fun relocate(pkg: String) = relocate(pkg, "com.xpdustry.sql4md.$identifier.shadow.${pkg.split(".").last()}")
            relocate("com.github.benmanes.caffeine")
            relocate("com.google.protobuf")
            relocate("waffle")
            relocate("com.sun.jna")
            relocate("com.xpdustry.sql4md.base", "com.xpdustry.sql4md.$identifier")
            mergeServiceFiles()
        }

        tasks.runMindustryServer {
            mods.from(downloadSlf4md)
        }

        tasks.build {
            dependsOn(tasks.shadowJar)
        }

        dist {
            from(tasks.shadowJar)
        }

        rootProject.tasks.runMindustryServer {
            mods.from(tasks.shadowJar)
        }
    }
}
