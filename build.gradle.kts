import com.xpdustry.toxopid.extension.anukeXpdustry
import com.xpdustry.toxopid.spec.ModMetadata
import com.xpdustry.toxopid.spec.ModPlatform
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec

plugins {
    id("com.diffplug.spotless") version "8.2.1"
    id("net.kyori.indra") version "4.0.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("com.xpdustry.toxopid") version "4.2.0"
}

val metadata = ModMetadata.fromJson(rootProject.file("plugin.json"))
val rootPackage = "com.xpdustry.sql4md"
metadata.version += if (findProperty("release").toString().toBoolean()) "" else "-SNAPSHOT"
version = metadata.version
group = "com.xpdustry"
description = metadata.description

repositories {
    mavenCentral()
    anukeXpdustry()
}

spotless {
    java {
        palantirJavaFormat()
        forbidWildcardImports()
        importOrder("", "\\#")
        licenseHeaderFile(rootProject.file("HEADER.txt"))
    }
    kotlinGradle {
        ktlint()
    }
}

toxopid {
    compileVersion = "v" + metadata.minGameVersion
    platforms = setOf(ModPlatform.SERVER)
}

dependencies {
    compileOnly(toxopid.dependencies.mindustryCore)
    compileOnly(toxopid.dependencies.arcCore)
    compileOnly("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.xerial:sqlite-jdbc:3.51.1.0")
    runtimeOnly("com.h2database:h2:2.4.240")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.5.7")
    runtimeOnly("com.mysql:mysql-connector-j:9.5.0")
    runtimeOnly("org.postgresql:postgresql:42.7.9")
}

configurations.runtimeClasspath {
    exclude(group = "org.slf4j")
    exclude(group = "com.google.errorprone")
    exclude(group = "org.checkerframework")
}

indra {
    javaVersions {
        target(17)
        minimumToolchain(17)
    }

    mitLicense()

    if (metadata.repository.isNotBlank()) {
        val repo = metadata.repository.split("/")
        github(repo[0], repo[1]) {
            ci(true)
            issues(true)
            scm(true)
        }
    }
}

val generateMetadataFile by tasks.registering {
    inputs.property("metadata", metadata)
    outputs.files(fileTree(temporaryDir))
    doLast {
        temporaryDir.resolve("plugin.json").writeText(ModMetadata.toJson(metadata))
    }
}

tasks.shadowJar {
    archiveFileName = "${metadata.name}.jar"
    archiveClassifier = "plugin"
    from(generateMetadataFile)
    from(rootProject.file("LICENSE.md")) { into("META-INF") }

    fun ezRelocate(pkg: String) = relocate(pkg, "$rootPackage.shadow.${pkg.split(".").last()}")
    ezRelocate("com.github.benmanes.caffeine")
    ezRelocate("com.google.protobuf")
    ezRelocate("waffle")
    ezRelocate("com.sun.jna")
    mergeServiceFiles()
}

val downloadSlf4md by tasks.registering(GithubAssetDownload::class) {
    owner = "xpdustry"
    repo = "slf4md"
    asset = "slf4md.jar"
    version = "v1.2.0"
}

tasks.runMindustryServer {
    mods.from(downloadSlf4md)
}

tasks.withType<MindustryExec> {
    jvmArguments.add("--enable-native-access=ALL-UNNAMED")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
