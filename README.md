# Desolation ![environment: any](https://img.shields.io/badge/environment-any-4caf50?style=flat-square) ![Mod loader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Minecraft: 26.2](https://img.shields.io/badge/minecraft-26.2-brightgreen?style=flat-square) ![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

> Wildfires bring both destruction and renewal.

**Desolation** is a Fabric mod that adds the aftermath of a wildfire to Minecraft: a scorched
**Charred Forest** biome where blackened trees, ash and smouldering embers replace the greenery —
along with the plants and creatures that reclaim the burnt ground.

## Features

- **Charred Forest biome** (plus small and clearing variants) that replaces forests and taigas during
  world generation, with its own surface rules laying down charred soil and ash.
- **Charred wood set** — logs, wood, stripped variants, planks, stairs, slabs, fences, fence gates,
  doors, trapdoors, buttons, pressure plates, standing/wall signs and hanging signs.
- **Charred trees** — full, small and fallen variants generated with custom trunk and foliage placers.
- **Ash & embers** — ash blocks, ash layers, ash piles, charred soil, ember and cooled ember blocks,
  activated charcoal, plus charcoal bits from processing.
- **Flora** — charred saplings (and potted), charred branches, ash brambles, scorched tufts and the
  edible **Cinderfruit** plant grown from seeds.
- **Creatures** — the **Ash Scuttler** and the **Blackened**, rendered with GeckoLib animations.
- **Cinder Dash** — a bindable movement ability with spark particle effects.
- **In-game config** via Cloth Config / Mod Menu (biome spawn chances, clearings, and more).

## Requirements

| | Version |
|---|---|
| Minecraft | **26.2** (a 26.1.2 build is also provided — see below) |
| Fabric Loader | 0.19.3+ |
| Java | 25+ |
| Fabric API | required |
| Cloth Config, GeckoLib, Biolith | bundled (JiJ) |
| Mod Menu | optional (config screen) |

## Download

Grab the latest release from the mod's [Modrinth](https://modrinth.com/mod/desolation/) or
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/desolation) page.

Prebuilt jars for the current port are also committed under [`dist/`](dist/):

| File | Minecraft |
|---|---|
| `desolation-1.10.0-mc26.2.jar` | 26.2 |
| `desolation-1.10.0-mc26.1.2.jar` | 26.1.2 |

## Build from Source

Requires **JDK 25**.

```sh
./gradlew build
```

The jar files are written to `build/libs`. To regenerate the data-driven assets
(models, blockstates, tags, recipes, loot) run:

```sh
./gradlew runDatagen
```

Porting notes and the full 26.1.2 → 26.2 API migration are documented in
[`MIGRATION.md`](MIGRATION.md).

## License

Released under the [MIT License](LICENSE).
