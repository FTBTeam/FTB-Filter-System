# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [21.1.1]

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
