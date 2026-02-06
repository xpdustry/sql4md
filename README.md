# sql4md

[![Downloads](https://img.shields.io/github/downloads/xpdustry/sql4md/total?color=008080&label=Downloads)](https://github.com/xpdustry/sql4md/releases)
[![Mindustry 8.0](https://img.shields.io/badge/Mindustry-8.0-008080)](https://github.com/Anuken/Mindustry/releases)
[![Discord](https://img.shields.io/discord/519293558599974912?color=008080&label=Discord)](https://discord.xpdustry.com)

## Description

With this plugin, you will no longer need to bundle your database drivers inside your own plugins.

## Supported Databases

| Name       | Plugin ID         | Website                                                              |
|------------|-------------------|----------------------------------------------------------------------|
| SQLite     | sql4md-sqlite     | [GitHub](https://github.com/xerial/sqlite-jdbc)                      |
| H2         | sql4md-h2         | [GitHub](https://github.com/h2database/h2database)                   |
| MariaDB    | sql4md-mariadb    | [GitHub](https://github.com/mariadb-corporation/mariadb-connector-j) |
| MySQL      | sql4md-mysql      | [GitHub](https://github.com/mysql/mysql-connector-j)                 |
| PostgreSQL | sql4md-postgresql | [Website](https://jdbc.postgresql.org/)                              |

## Usage

### For server operators

This plugin requires :

- Mindustry v154 or above.

- Java 17 or above.

- [SLF4MD](https://github.com/xpdustry/slf4md) (optional)

### For developers

You only need to add the driver plugin you want in your `plugin.json` dependencies:

```json
{
  "dependencies": [ "sql4md-h2", "sql4md-mariadb" ]
}
```

For local testing, I recommend using the [toxopid](https://github.com/xpdustry/toxopid) Gradle plugin,
you will be able to automatically download this mod alongside yours and launch it in a local Mindustry server:

<details open>
<summary>Gradle</summary>

```groovy
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec

plugins {
    id "com.xpdustry.toxopid" version "4.x.x"
}

def downloadSql4md = tasks.register("downloadSql4md", GithubAssetDownload) {
    owner = "xpdustry"
    repo = "sql4md"
    asset = "sql4md-sqlite.jar"
    version = "v2.x.x"
}

tasks.withType(MindustryExec).configureEach {
    mods.from(downloadSql4md)
}
```
</details>

<details>
<summary>Kotlin</summary>

```kt
import com.xpdustry.toxopid.task.GithubAssetDownload
import com.xpdustry.toxopid.task.MindustryExec

plugins {
    id("com.xpdustry.toxopid") version "4.x.x"
}

val downloadSql4md by tasks.registering(GithubAssetDownload::class) {
    owner = "xpdustry"
    repo = "sql4md"
    asset = "sql4md-sqlite.jar"
    version = "v2.x.x"
}

tasks.withType<MindustryExec> {
    mods.from(downloadSql4md)
}
```
</details>

## Building

- `./gradlew :sql4md-$database:shadowJar` to compile and bundle a plugin (will be located at `sql4md-$database/builds/libs/sql4md-$database.jar`).
- `./gradlew :sql4md-$database:runMindustryServer` to run the plugin in a local Mindustry server.
- `./gradlew :runMindustryServer` to run all the database plugins at once in a local Mindustry server.
