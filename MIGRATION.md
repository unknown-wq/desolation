# Desolation — 1.21.6 → 26.1.2 Port: Migration Handoff

Foundation work (Agent 1) is complete: build toolchain, dependency versions, mappings
switch, and Trinkets/Cardinal removal. This document is the handoff for Agents 2–5 who
fix the domain `.java` code.

**Status of the build**: `./gradlew :dependencies` resolves cleanly (all deps download).
`./gradlew build` will still FAIL to *compile* — that is expected; the `.java` sources are
still written against Yarn (1.21.6) names and must be migrated to Mojang mappings (26.1.2).

---

## 1. Toolchain changes (done)

| Thing | Old (1.21.6) | New (26.1.2) |
|---|---|---|
| Minecraft | 1.21.6 (obfuscated, Yarn) | 26.1.2 (**unobfuscated**, no mappings) |
| Java | 21 | **25** (`JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64`) |
| Gradle wrapper | 8.12.1 | **9.5.1** |
| Fabric Loom plugin | `fabric-loom` `1.10.+` | **`net.fabricmc.fabric-loom` `1.17.13`** (id changed + version pinned via `loom_version` prop) |
| Mappings | `net.fabricmc:yarn:1.21.6+build.1:v2` | **none** — Yarn is discontinued; 26.1 is unobfuscated so the `mappings` line is REMOVED entirely (do NOT use `officialMojangMappings()` — Loom errors "Cannot use Mojang mappings in a non-obfuscated environment") |
| Mixin `compatibilityLevel` | `JAVA_17` | **`JAVA_25`** |
| AccessWidener header | `named` | **`official`** (body still Yarn-named — see §5) |
| `modImplementation`/`modApi` | used | changed to plain `implementation`/`api` (mod deps are now normal deps on unobfuscated MC) |
| `remapJar` in publishing | used | changed to `jar` (no remap step on unobfuscated MC) |

Build-script API bumps required by Gradle 9 (already applied in `build.gradle`):
- top-level `sourceCompatibility`/`targetCompatibility` project properties are gone →
  moved into the `java { }` block.
- top-level `archivesBaseName` is gone → `base { archivesName = ... }`.

---

## 2. Final dependency version table (set in `gradle.properties` / `build.gradle`)

| Dependency | Coordinate | Version | Notes |
|---|---|---|---|
| fabric-loader | `net.fabricmc:fabric-loader` | `0.19.3` | newest |
| fabric-api | `net.fabricmc.fabric-api:fabric-api` | `0.154.2+26.1.2` | newest `+26.1.2` |
| cloth-config | `me.shedaniel.cloth:cloth-config-fabric` | `26.1.154` | only 26.1 build |
| geckolib | **`com.geckolib:geckolib-fabric-26.1.2`** | `5.5.2` | **groupId changed** `software.bernie.geckolib` → `com.geckolib`; artifact still `geckolib-fabric-<mcver>`; repo unchanged (Cloudsmith `dl.cloudsmith.io/public/geckolib3/geckolib/maven/`) |
| biolith | `com.terraformersmc:biolith-fabric` | `3.6.0-alpha.9` | **alpha**, declares `minecraft ">=26.1 <26.3"` |
| terraform-tree-api-v1 | `com.terraformersmc.terraform-api:terraform-tree-api-v1` | `17.0.0-alpha.6` | **alpha** (version jumped 15.x→17.x for 26.1) |
| terraform-wood-api-v1 | `com.terraformersmc.terraform-api:terraform-wood-api-v1` | `17.0.0-alpha.6` | **alpha** |
| modmenu | `com.terraformersmc:modmenu` | `18.0.0-alpha.8` | 26.1 build (`minecraft ">1.26-"`); v20.x is for 26.2, do NOT use |

Verified resolved via `./gradlew :dependencies --configuration runtimeClasspath` → BUILD SUCCESSFUL.

### Removed dependencies
- **Cardinal Components** (`org.ladysnake.cardinal-components-api:cardinal-components-base`
  and `:cardinal-components-entity`, prop `cca_version`) — REMOVED. `grep` confirms zero
  usage anywhere in `src/`. Nothing to migrate.
- **Trinkets** (`dev.emi:trinkets`, prop `trinkets_version`) — REMOVED from build + resource
  dirs. Java that references it still exists and must be deleted/rewritten — see §6.

### Unavailable / unresolved
- **`terraform-boat-api-v1`** does NOT exist for 26.1 on the Terraformers maven (404 / "Latest
  build not found"). It was not a declared dependency in the old `build.gradle` either, but
  `fabric.mod.json` has a `terraform-boat-dfu` entrypoint (`raltsmc.desolation.init.helpers.DesolationBoatDfu`)
  and there is a `registry/DesolationBoats.java`. **Owner of boats code (Agent handling
  registry/entity): if no boat API ships for 26.1, remove the `terraform-boat-dfu` entrypoint
  from `fabric.mod.json`, delete `DesolationBoats.java`, and drop `DesolationBoatDfu`.** Left
  in place for now (a dangling entrypoint namespace is harmless at build config time).

---

## 3. Mappings: Yarn → Mojang (official) — the core of the port

Minecraft 26.1 ships **unobfuscated with parameter names**, so there is no mapping layer.
The code now compiles against Mojang's official names directly. Every Yarn class/method name
in `src/` must be renamed. Common renames observed in this codebase (with file counts):

| Yarn (old) | Mojang (new) | Files affected |
|---|---|---|
| `Identifier` | `ResourceLocation` | ~40 |
| `ServerWorld` | `ServerLevel` | ~13 |
| `World` | `Level` | ~12 |
| `Registries` (yarn) | `BuiltInRegistries` (vanilla) / `Registries` (Fabric keys) | ~13 |
| `PlayerEntity` | `Player` | ~10 |
| `StatusEffect` / `StatusEffectInstance` | `MobEffect` / `MobEffectInstance` | ~9 |
| `ActionResult` | `InteractionResult` | ~6 |
| `Hand` | `InteractionHand` | ~5 |
| `ClientPlayerEntity` | `LocalPlayer` | ~3 |
| `MinecraftClient` | `Minecraft` | ~2 |
| `AbstractBlock` / `AbstractBlock.Settings` | `BlockBehaviour` / `BlockBehaviour.Properties` | ~2 |
| `DrawContext` | `GuiGraphics` | 1 |
| `InGameHud` | `Gui` | 1 |
| `Formatting` | `ChatFormatting` | 1 |

Other very common renames you WILL hit (not exhaustively counted here):
`Text`→`Component`, `MutableText`→`MutableComponent`, `LivingEntity`→`LivingEntity` (same),
`ItemStack`/`BlockPos`/`BlockState` (same), `Item.Settings`→`Item.Properties`,
`FabricItemSettings`→removed (use `Item.Properties`), `SoundEvent` (same),
`Block.Settings`→`BlockBehaviour.Properties`, `EntityType.Builder` methods renamed,
`TrunkPlacer`/`FoliagePlacer` package + method renames, `net.minecraft.util.math.*`→
`net.minecraft.core.*` / `net.minecraft.world.phys.*`.

Also review the **Fabric API 26.1 rename list** (some Fabric API methods were renamed to match
Mojang): https://docs.fabricmc.net/develop/porting/fabric-api-26.1 and the NeoForge
1.21.11→26.1 primer for vanilla behavior changes. (Both were unreachable from this sandbox at
authoring time; fetch them from your IDE.)

### AccessWidener (`src/main/resources/desolation.accesswidener`)
Header was switched `named` → `official`. **The body is still Yarn-named** (e.g.
`net/minecraft/block/BlockSetType register (...)`) and must be remapped to Mojang names/paths
by whoever owns the widened members (registry/block domain). Under `official`, Loom validates
these against Mojang names — Yarn paths like `net/minecraft/block/...` will fail once you get
past compilation. Remap each entry (class, method descriptor, and package path).

---

## 4. `migrateMappings` — NOT run (rationale)

Loom's `migrateMappings` task was **not** run. It is designed to remap Yarn→Mojmap **on the
same Minecraft version** (i.e. on 1.21.11 while still obfuscated). Here we are simultaneously
(a) jumping MC versions 1.21.6→26.1.2, (b) removing the entire mappings layer, and (c) dropping
deps (Trinkets/Cardinal) whose types appear in source. Running `migrateMappings` in that state
requires both mapping sets to resolve against a compilable source tree, which is not the case,
and would very likely fail or produce garbage. **Approach for Agents 2–5:** migrate names
manually per-file using the table in §3 plus your IDE's "find usages"; the unobfuscated jar has
real names, so IDE auto-complete against `net.minecraft:minecraft-merged-*:26.1.2` is the
fastest oracle. (Optionally use the Ravel IntelliJ plugin, which Fabric API itself used, if you
prefer a GUI-assisted remap — https://docs.fabricmc.net/develop/porting/mappings/.)

---

## 5. Trinkets `.java` files to DELETE / rewrite (owned by Agents 2/4/5)

Trinkets resources were already removed (`assets/trinkets/**`, `data/trinkets/**`,
`goggles_overlay.png`+`.mcmeta`). The following source files still `import dev.emi.trinkets.*`
or reference Trinket types and **will not compile** — delete them and excise their references:

- `src/main/java/raltsmc/desolation/registry/DesolationItems.java` (remove trinket item + registration)
- `src/main/java/raltsmc/desolation/mixin/entity/player/PlayerEntityMixin.java`
- `src/main/java/raltsmc/desolation/mixin/client/gui/hud/InGameHudMixin.java` (goggles overlay)
- `src/main/java/raltsmc/desolation/client/render/entity/feature/HeadTrinketRenderer.java`
- `src/main/java/raltsmc/desolation/client/render/entity/feature/TrinketRenderers.java`
- `src/main/java/raltsmc/desolation/client/render/entity/model/HeadTrinketModel.java`
- `src/main/java/raltsmc/desolation/init/client/DesolationClient.java` (references `TrinketRenderers` registration — remove that call, keep the rest of the file)

When deleting the two client feature/model renderer files and `TrinketRenderers`, also remove
their registration in `DesolationClient` and any mixin entries. **Mixin JSON:** if you delete
`InGameHudMixin`/`PlayerEntityMixin` rather than rewrite them, remove their entries from
`desolation.mixins.json` (`client.gui.hud.InGameHudMixin`, `entity.player.PlayerEntityMixin`).
Note `PlayerEntityMixin` likely also has non-Trinket logic (fire/ember mechanics) — prefer to
*keep the file and strip only the Trinket parts* rather than delete it wholesale.

---

## 6. Remaining compile-error work, grouped by domain (for Agents 2–5)

All of the following are Yarn-named and need the §3 rename pass. Grouped so each agent knows
their front:

### A. Registry / block / item / entity  (`registry/`, `block/`, `item/`, `entity/`)
- `registry/` (30 files): `DesolationBlocks`, `DesolationItems`, `DesolationEntities`,
  `DesolationBlockSetTypes`, `DesolationBlockFamilies`, `DesolationWoodTypes`,
  `DesolationStatusEffects`, `DesolationPotions`, `DesolationParticles`, `DesolationSounds`,
  `DesolationBoats` (see boat-api note §2), `DesolationJukeboxSongs`, `DesolationItemGroups`,
  `DesolationRegistries`, `DesolationTrunkPlacerTypes`, `DesolationFoliagePlacerTypes`,
  `DesolationStructures`, `DesolationBiomes`, `DesolationLootTables`, `ItemGroupEntries`, …
  Expect: `Registry.register`/`Registries` key renames, `Item.Settings`→`Item.Properties`,
  `AbstractBlock.Settings`→`BlockBehaviour.Properties`, `FabricItemSettings` removal,
  `Identifier`→`ResourceLocation`.
- `block/` (11 blocks) + `item/` (3 items) + `entity/` (`AshScuttlerEntity`, `BlackenedEntity`,
  `entity/ai`, `entity/effect`): entity/block base-class + method renames, `World`→`Level`.

### B. Worldgen  (`world/`)
- `world/gen` (5), `world/feature` (6), `world/biome`, `world/structure` (2), plus
  `registry/DesolationTrunkPlacerTypes`/`DesolationFoliagePlacerTypes`. Biolith 3.6 is a new
  major (2.x→3.x) — its biome-placement API changed; check biolith's 26.1 API. Terraform
  tree/wood APIs are alpha 17.x — trunk placer / wood type registration signatures likely
  changed. `mixin/tree/TrunkPlacerMixin` lives here too.

### C. Client / render  (`client/`)
- `client/render` (9), `client/particle`, `init/client/DesolationClient`. GeckoLib jumped to
  `com.geckolib` groupId and GeckoLib 5.x — renderer/model base classes and registration
  changed vs 5.2.0; consult GeckoLib 5.5 docs. Vanilla render renames: `DrawContext`→
  `GuiGraphics`, `MinecraftClient`→`Minecraft`, `ClientPlayerEntity`→`LocalPlayer`,
  entity-renderer constructor/`render` signature changes in 26.1.

### D. Mixins & datagen  (`mixin/`, `data/`)
- `mixin/` (8 mixins across enchantment/entity/potion/tree/client). Mixin targets must use
  Mojang class names + descriptors; `@Redirect`/`@Inject` method signatures change. Compat
  level is now `JAVA_25`.
- `data/` (10 datagen providers) + `init/helpers/DesolationBoatDfu`. Fabric datagen API had
  renames in 26.1; loot/tag/model provider base classes changed. Datagen is wired via
  `fabricApi { configureDataGeneration { client = true } }` (unchanged).

---

## 7. Sandbox note: Gradle distribution download

The committed `gradle-wrapper.properties` uses the canonical
`https://services.gradle.org/distributions/gradle-9.5.1-bin.zip` (correct for CI / normal use).
**In THIS proxy sandbox that URL 307-redirects to a GitHub release asset which the egress proxy
gates (403).** If a fresh container needs to download Gradle, temporarily point the wrapper at a
reachable mirror, e.g.:

```
sed -i 's|distributionUrl=.*|distributionUrl=https\\://repo.huaweicloud.com/gradle/gradle-9.5.1-bin.zip|' gradle/wrapper/gradle-wrapper.properties
```

(Do not commit the mirror URL.) The dist is already cached in `~/.gradle/wrapper/dists` for the
mirror URL from Agent 1's verification run. Always build with
`JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64`.
