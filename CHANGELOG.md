# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [20.0.1]

### Fixed
* Fixed `FilterParser.parse` returning null on failure, which can cause player kick on login with FTB Quests
  * Now returns a special "invalid" filter which doesn't match, and includes the failure reason in its display text

## [1.0.2]

### Changed

* Initial public release

## [1.0.1]

* Cleaned up a bit for pre-release and internal acceptance testing

## [1.0.0]

* Initial version