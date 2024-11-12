# sql4md

[![Build status](https://github.com/xpdustry/sql4md/actions/workflows/build.yml/badge.svg?branch=master&event=push)](https://github.com/xpdustry/sql4md/actions/workflows/build.yml)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0-ffd37f)](https://github.com/Anuken/Mindustry/releases)

## Description

Instead of bundling database binaries in your plugin, use this one instead, comes with the most popular databases connectors:

- MySQL
- MariaDB
- SQLite

## Installation

This plugin requires :

- [SLF4MD](https://github.com/xpdustry/slf4md)

- Java 17 or above.

- Mindustry v146 or above.

## Building

- `./gradlew shadowJar` to compile the plugin into a usable jar (will be located
  at `builds/libs/(plugin-name).jar`).

- `./gradlew runMindustryServer` to run the plugin in a local Mindustry server.

- `./gradlew runMindustryDesktop` to start a local Mindustry client that will let you test the plugin.
