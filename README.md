# Desolation — Fabric port for Minecraft 26.2 ![environment: any](https://img.shields.io/badge/environment-any-4caf50?style=flat-square) ![Mod loader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Minecraft: 26.2](https://img.shields.io/badge/minecraft-26.2-brightgreen?style=flat-square) ![License: MIT](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

> Wildfires bring both destruction and renewal.

> ⚠️ **Unofficial port.** This is **not** an official release — it's a community port of the
> **Desolation** Fabric mod to **Minecraft 26.2** (with a 26.1.2 build as well). It is not
> affiliated with or endorsed by the original mod authors.
>
> Everything has been debugged and **works properly**. The one thing intentionally left out:
> **boats have been cut**, because porting them was more effort than it was worth. Nothing else
> is missing.

**Desolation** is a Fabric mod that adds the aftermath of a wildfire to Minecraft: a scorched
**Charred Forest** biome where blackened trees, ash and smouldering embers replace the greenery —
along with the plants and creatures that reclaim the burnt ground. This repository is the
**Desolation Fabric 26.2 port** — a build of the mod updated to run on the latest Minecraft release.

## Features

- **Charred Forest biome** (plus small and clearing variants) that replaces forests and taigas during
  world generation, with its own surface rules laying down charred soil and ash. Each variant has its
  own palette, ambience, spawns and ash density.
- **Charred Hut** — the remains of a homestead the fire caught, generated in three states of collapse
  and two endings: the people who got out, and the ones who did not.
- **Ashen Lung** — breathing ash-laden air fills a meter rather than applying a flat debuff, and the
  **Air Filter** is worn on the head to hold it back.
- **Charred wood set** — logs, wood, stripped variants, planks, stairs, slabs, fences, fence gates,
  doors, trapdoors, buttons, pressure plates, standing/wall signs and hanging signs.
- **Charred trees** — full, small and fallen variants generated with custom trunk and foliage placers.
- **Ash & embers** — ash blocks, ash layers, ash piles, charred soil, ember and cooled ember blocks,
  activated charcoal, plus charcoal bits from processing.
- **Flora** — charred saplings (and potted), charred branches, ash brambles, scorched tufts and the
  edible **Cinderfruit** plant grown from seeds.
- **Creatures** — the **Ash Scuttler** and the **Blackened**, rendered with GeckoLib animations.
- **Cinder Dash** — a bindable movement ability with charges, applied and validated by the server;
  dashing through a mob hurts and ignites it.
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

A prebuilt jar for the current port is committed under [`dist/`](dist/):

| File | Minecraft |
|---|---|
| `desolation-2.0.1-mc26.2.jar` | 26.2 |

Every push and pull request also uploads the jars it built as the `Artifacts`
bundle of the [Check Build](../../actions/workflows/check_build.yml) workflow run,
which is the place to grab an unreleased build from.

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

If the wrapper cannot download Gradle (restricted network — `services.gradle.org`
redirects to GitHub release assets), a Gradle 9.6.1 distribution is vendored under
[`gradle-dist/`](gradle-dist/):

```sh
./gradle-dist/install.sh                                  # → /opt/gradle-9.6.1
JAVA_HOME=/path/to/jdk-25 /opt/gradle-9.6.1/bin/gradle build --no-daemon
```

Porting notes and the full 26.1.2 → 26.2 API migration are documented in
[`MIGRATION.md`](MIGRATION.md).

### Port status

Everything in this **Desolation Fabric 26.2** port is tested and working. The only content
deliberately dropped during the port is the **charred boats** — they were omitted because
porting them wasn't worth the effort. All other blocks, biomes, creatures and features behave
as in the original.

## License

Released under the [MIT License](LICENSE).
