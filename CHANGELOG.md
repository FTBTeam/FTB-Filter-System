# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [21.11.0]

### Changed
* Ported to Minecraft 1.21.11

## [21.1.5]

### Added
* Added some glob/regex functionality to the item, item tag, and mod filters (thanks @Furglitch)
  * Not available via the GUI, but can be set via CLI, e.g.: `/ftbfiltersystem set_filter item(minecraft:iron_*)`
  * Glob strings must contain a '*' or '?' character and are implictly anchor to start and of text being testd
  * Full regex strings must start and end with a '/' character, e.g. `/ftbfiltersystem set_filter item(/:iron_/)`

## [21.1.4]

### Changed
* API Breakage: several API methods now take a `HolderLookup.Provider` parameter
  * This is required for (de)serialization of some filter objects

### Fixed
* Fixed some dangerous registry access usage which could lead to unexpected crashes (see above change)

## [21.1.3]

### Fixed
* Fixed Component Filter screen being unable to parse NBT-encoded item component data

## [21.1.2]

### Fixed
* Fixed a potential performance issue related to filter caching

## [21.1.1]

### Added
* Added pt_br translation (thanks @Xlr11)
* Added ru_ru translation (thanks @BazZziliuS)

### Fixed
* Hopefully fix a crash on init ("frozen registry")

## [21.1.0]

### Changed
* Built for Minecraft 1.21.1; 1.21.0 not supported anymore

### Fixed
* Hopefully fixed thread-safety issue in filter caching code

## [21.0.1]

### Added
* Added tr_tr translation (thanks @RuyaSavascisi)

### Fixed
* Fixed an issue where editing an existing filter via GUI could lead to bad filter data being cached

## [21.0.0]

### Changed
* Filter items are now registered to the `FTB Suite` creative tab instead of its own tab
* FTB Filter System now requires FTB Library to be installed
* Version bump from 3.0.0 to 21.0.0 reflects new version numbering scheme based on Minecraft major/minor release

### Fixed
* Fixed recipe for Smart Filter not working

## [3.0.0]

### Changed
* Ported to Minecraft 1.21. Support for Fabric and NeoForge.
  * Forge support may be re-added if/when Architectury adds support for Forge

## [2.1.0]

### Changed
* Ported to Minecraft 1.20.6. Support for Fabric and NeoForge.
    * Forge support may be re-added if/when Architectury adds support for Forge

## [2.0.1]

### Changed
* Ported to MC 1.20.4. Supports Forge, NeoForge & Fabric.

## [1.0.2]


## [1.0.1]

* Cleaned up a bit for pre-release and internal acceptance testing

## [1.0.0]

* Initial version
