# Ez Mace Keybinds – Minecraft 26.x (26.1+)

## Requirements
- **Java 25**
- **Gradle 9.8.0 or newer** (Loom 1.19.3 requires this; 9.2.1 is too old)
- Fabric Loader + Fabric API for your Minecraft 26.x version

## Supported versions
The jar is built against 26.2 but declares `minecraft: >=26.1 <27`, so Fabric loads it on any 26.x version.
Only 26.2 has been confirmed against the code; 26.1 and 26.3 are untested.

## Upgrade Gradle (Windows)
Download: https://gradle.org/releases/
Get **9.8.0** or latest 9.x binary zip, unzip, add `bin` to PATH.

Check:
```
gradle --version
```
Must show 9.8.0 or higher.

## Build
```
gradle clean build
```
JAR: `build/libs/EzMaceKeybinds-1.1+26.x-1.1.jar`
