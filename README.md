# Ez Mace Keybinds 1.1 (Minecraft 26.x)

Version 1.1 for the Minecraft 26.x line, including Lunge Swap. It's a client-side Fabric mod with one-press keybinds for mace, elytra and spear techniques.

## Features
- Trigger Firework
- Trigger Elytra/Armor Swap, plus a Swap/Revert mode toggle
- Trigger Wind Charge
- Trigger Pearl Catch
- Trigger Air Pot
- Trigger Pearl
- Trigger Lunge Swap: swap to your spear, left-click, then swap back

All keybinds are unbound by default. Set them under **Options > Controls > Key Binds > Ez Mace Keybinds**.

## Supported versions
Declares `minecraft: >=26.1 <27`, so Fabric loads it on any 26.x version. It is compiled against 26.2, and only 26.2 has been checked against the code. 26.1 and 26.3 are untested.

## Requirements
- Minecraft Java 26.x
- Fabric Loader 0.19.3+ and Fabric API
- **Java 25+** (Gradle 9.8+ to build)

## Build
    gradle clean build
The jar is written to `build/libs/`.
