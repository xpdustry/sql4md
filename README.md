# sql4md

[![Downloads](https://img.shields.io/github/downloads/xpdustry/sql4md/total?color=008080&label=Downloads)](https://github.com/xpdustry/sql4md/releases)
[![Mindustry 8.0](https://img.shields.io/badge/Mindustry-8.0-008080)](https://github.com/Anuken/Mindustry/releases)
[![Discord](https://img.shields.io/discord/519293558599974912?color=008080&label=Discord)](https://discord.xpdustry.com)

## Description

Instead of bundling database binaries in your plugin, use this one instead, comes with the most popular databases connectors:

- MySQL
- MariaDB
- H2
- SQLite

## Installation

This plugin requires :

- [SLF4MD](https://github.com/xpdustry/slf4md)

- Java 17 or above.

- Mindustry v154 or above.

## Building

- `./gradlew :shadowJar` to compile the plugin into a usable jar (will be located at `builds/libs/sql4md.jar`).
- `./gradlew :runMindustryServer` to run the plugin in a local Mindustry server.
- `./gradlew :runMindustryDesktop` to start a local Mindustry client that will let you test the plugin.
