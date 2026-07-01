import com.xpdustry.toxopid.extension.anukeXpdustry
import com.xpdustry.toxopid.spec.ModDependency
import com.xpdustry.toxopid.spec.ModMetadata
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec
import org.gradle.kotlin.dsl.register
import java.util.Properties

plugins {
    id("com.diffplug.spotless") version "8.7.0"
    id("net.kyori.indra") version "4.0.0"
    id("com.gradleup.shadow") version "9.4.3"
    id("com.xpdustry.toxopid") version "4.2.0"
}

version = "2.1.1" + if (findProperty("release").toString().toBoolean()) "" else "-SNAPSHOT"
group = "com.xpdustry"
description = "A collection of plugins providing SQL drivers for other plugins."

val mindustryVersion = "158"

val drivers =
    listOf(
        Triple("sqlite", "org.sqlite.JDBC", "org.xerial:sqlite-jdbc:3.53.2.0"),
        Triple("h2", "org.h2.Driver", "com.h2database:h2:2.4.240"),
        Triple("mariadb", "org.mariadb.jdbc.Driver", "org.mariadb.jdbc:mariadb-java-client:3.5.9"),
        Triple("mysql", "com.mysql.cj.jdbc.Driver", "com.mysql:mysql-connector-j:9.7.0"),
        Triple("postgresql", "org.postgresql.Driver", "org.postgresql:postgresql:42.7.12"),
        Triple("postgresql-embedded", "org.postgresql.Driver", "io.zonky.test:embedded-postgres:2.2.2"),
    )

val downloadSlf4md =
    tasks.register<GithubAssetDownload>("downloadSlf4md") {
        owner = "xpdustry"
        repo = "slf4md"
        asset = "slf4md.jar"
        version = "v1.2.0"
    }

val dist =
    tasks.register<Copy>("dist") {
        destinationDir = temporaryDir
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

    tasks.runMindustryServer {
        dependsOn(downloadSlf4md)
        mods.from(downloadSlf4md)
    }
}

for ((identifier, driver, library) in drivers) {
    project(":sql4md-$identifier") {
        val `package` = identifier.replace("-", "_")
        val metadata =
            ModMetadata(
                displayName = "SQL4MD-${identifier.uppercase()}",
                name = "sql4md-$identifier",
                description = "$description This implementation provides the $identifier driver.",
                version = version.toString(),
                author = "xpdustry",
                repository = "xpdustry/sql4md",
                mainClass = "com.xpdustry.sql4md.$`package`.DriverLoader",
                minGameVersion = mindustryVersion,
                java = true,
                hidden = true,
                dependencies = mutableListOf(ModDependency("slf4md", soft = true)),
            )

        if (identifier == "postgresql-embedded") {
            metadata.dependencies += ModDependency("sql4md-postgresql")
        }

        dependencies {
            runtimeOnly(library)
            implementation(rootProject)
        }

        configurations.runtimeClasspath {
            exclude(group = "org.slf4j")
            exclude(group = "com.google.errorprone")
            exclude(group = "org.checkerframework")

            if (identifier == "postgresql-embedded") {
                exclude(group = "org.postgresql")
            }
        }

        val generateResourceFiles =
            tasks.register("generateResourceFiles") {
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

            fun relocate(value: String) = relocate(value, "com.xpdustry.sql4md.$`package`.shadow.${value.split(".").last()}")
            relocate("com.github.benmanes.caffeine")
            relocate("com.google.protobuf")
            relocate("waffle")
            relocate("com.sun.jna")
            // relocate("io.zonky.test")
            relocate("org.apache.commons")
            relocate("org.tukaani")
            relocate("com.xpdustry.sql4md.base", "com.xpdustry.sql4md.$`package`")
            mergeServiceFiles()
        }

        tasks.build {
            dependsOn(tasks.shadowJar)
        }

        dist {
            from(tasks.shadowJar)
        }

        rootProject.tasks.runMindustryServer {
            dependsOn(tasks.shadowJar)
            mods.from(tasks.shadowJar)
        }
    }
}
