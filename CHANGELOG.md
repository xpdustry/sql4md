# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/),
and this project adheres to [Semantic Versioning](http://semver.org/).

## v2.0.2 - 2026-04-22

### Maintenance

- Update dependency com.mysql:mysql-connector-j to v9.7.0 ([`05dd012`](https://github.com/xpdustry/sql4md/commit/05dd012832b7a3290f84298fb833d42d1327683f))
- Update dependency org.xerial:sqlite-jdbc to v3.53.0.0 ([`1e1424e`](https://github.com/xpdustry/sql4md/commit/1e1424e3a2e91144eed4e7821c82a2d388753539))
- Update dependency org.mariadb.jdbc:mariadb-java-client to v3.5.8 ([`1704d15`](https://github.com/xpdustry/sql4md/commit/1704d154dd45befa85de87cef5298f9e15e1c78a))
- Update dependency org.postgresql:postgresql to v42.7.10 ([`7db0856`](https://github.com/xpdustry/sql4md/commit/7db0856094739e383514eedc0d8f7e5ad8967a46))

## v2.0.1 - 2026-02-09

### Bugfixes

- sqlite no longer has a hard dependency on slf4md ([`69e3980`](https://github.com/xpdustry/sql4md/commit/69e398078b4f13c483737d8ccddf8b4e0e148db3))

## v2.0.0 - 2026-02-06

### Changes & New features

Drivers are now split  into their dedicated plugins.
The `sql4md` plugin that contained everything at once no longer exists.
More in the [README](https://github.com/xpdustry/sql4md).

### Maintenance

- Use spdx identifier instead of the whole license file ([`1eb0da9`](https://github.com/xpdustry/sql4md/commit/1eb0da97d27dcf8baa3fbdf64b6c22124ee91b90))

## v1.2.0 - 2026-01-16

### Changes & New features

- Added postgres drivers.

### Maintenance

- Refactored internals to be simpler.

## v1.1.2 - 2025-09-27

### Changes

- Now compatible with Mindustry v8.
- Updated SQL driver versions.

## v1.1.1 - 2025-06-23

### Chores

- Updated dependencies.

## v1.1.0 - 2024-11-12

### Features

- Added H2 database support.

## v1.0.0 - 2024-11-12

Initial release, enjoy!
