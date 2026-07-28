package raltsmc.desolation.world.feature;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBlockTags;
import raltsmc.desolation.world.gen.placement.HotspotPlacement;

import java.util.List;

public class DesolationPlacedFeatures {
    public static final ResourceKey<PlacedFeature> TREES_CHARRED_LARGE = createRegistryKey("trees_charred_large");
    public static final ResourceKey<PlacedFeature> TREES_CHARRED_SMALL = createRegistryKey("trees_charred_small");
    public static final ResourceKey<PlacedFeature> TREES_CHARRED_FALLEN_LARGE = createRegistryKey("trees_charred_fallen_large");
    public static final ResourceKey<PlacedFeature> TREES_CHARRED_FALLEN_SMALL = createRegistryKey("trees_charred_fallen_small");

    public static final ResourceKey<PlacedFeature> PATCH_CHARRED_SAPLING = createRegistryKey("patch_charred_sapling");
    public static final ResourceKey<PlacedFeature> PATCH_SCORCHED_TUFT = createRegistryKey("patch_scorched_tuft");
    public static final ResourceKey<PlacedFeature> PATCH_ASH_LAYER = createRegistryKey("patch_ash_layer");
    public static final ResourceKey<PlacedFeature> PATCH_EMBER_CHUNK = createRegistryKey("patch_ember_chunk");
    public static final ResourceKey<PlacedFeature> PATCH_ASH_BRAMBLE = createRegistryKey("patch_ash_bramble");
    public static final ResourceKey<PlacedFeature> PLANT_CINDERFRUIT = createRegistryKey("plant_cinderfruit");
    public static final ResourceKey<PlacedFeature> GIANT_BOULDER = createRegistryKey("giant_boulder");

    // Per-biome-variant ash and ember densities. The clearing is where the fire burned hottest and
    // longest, so it drowns in ash and glowing coals; the small forest is the thinning edge of the
    // burn and barely shows any.
    public static final ResourceKey<PlacedFeature> PATCH_ASH_LAYER_DEEP = createRegistryKey("patch_ash_layer_deep");
    public static final ResourceKey<PlacedFeature> PATCH_ASH_LAYER_SPARSE = createRegistryKey("patch_ash_layer_sparse");
    public static final ResourceKey<PlacedFeature> PATCH_EMBER_CHUNK_DEEP = createRegistryKey("patch_ember_chunk_deep");
    public static final ResourceKey<PlacedFeature> PATCH_EMBER_CHUNK_SPARSE = createRegistryKey("patch_ember_chunk_sparse");

    /** Shared noise offset so all burn-scar features (embers, ash, tufts, brambles, boulders) co-locate. */
    private static final double HOTSPOT_SALT = 0.0D;

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationPlacedFeatures() {
		return;
	}

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        final BlockPredicate ON_CHARRED_SOIL = BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(), DesolationBlocks.CHARRED_SOIL);
        final BlockPredicate ON_SCORCHED_EARTH = BlockPredicate.matchesTag(Direction.DOWN.getUnitVec3i(), DesolationBlockTags.SCORCHED_EARTH);
        final BlockPredicate ON_ASH_BRAMBLE_GROUND = BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(),
                DesolationBlocks.CHARRED_SOIL, DesolationBlocks.CHARRED_LOG, DesolationBlocks.ASH_BRAMBLE);

        registerTreeFeature(context, configuredFeatures, TREES_CHARRED_LARGE, 15, ON_SCORCHED_EARTH, DesolationConfiguredFeatures.TREE_CHARRED);

        registerTreeFeature(context, configuredFeatures, TREES_CHARRED_SMALL, 10, ON_SCORCHED_EARTH, DesolationConfiguredFeatures.TREE_CHARRED_SMALL);

        registerTreeFeature(context, configuredFeatures, TREES_CHARRED_FALLEN_LARGE, 4, ON_SCORCHED_EARTH, DesolationConfiguredFeatures.TREE_CHARRED_FALLEN);

        registerTreeFeature(context, configuredFeatures, TREES_CHARRED_FALLEN_SMALL, 3, ON_SCORCHED_EARTH, DesolationConfiguredFeatures.TREE_CHARRED_FALLEN_SMALL);

        // Former RANDOM_PATCH features: SIMPLE_BLOCK scattered via placement modifiers.
        // Saplings stay uniformly spread — they are not tied to the burn-scar hotspots.
        registerPatchFeature(context, configuredFeatures, PATCH_CHARRED_SAPLING, DesolationConfiguredFeatures.PATCH_CHARRED_SAPLING,
                2, 1, 7, 3, ON_CHARRED_SOIL);

        // Ash, tufts and brambles cluster into hotspots. They share HOTSPOT_SALT so they pile up in
        // the same regions (see HotspotPlacement).
        registerHotspotPatchFeature(context, configuredFeatures, PATCH_SCORCHED_TUFT, DesolationConfiguredFeatures.PATCH_SCORCHED_TUFT,
                2, 8, 96, 7, 3, ON_CHARRED_SOIL);

        registerHotspotPatchFeature(context, configuredFeatures, PATCH_ASH_LAYER, DesolationConfiguredFeatures.PATCH_ASH_LAYER,
                1, 4, 128, 11, 3, ON_SCORCHED_EARTH);

        registerHotspotPatchFeature(context, configuredFeatures, PATCH_ASH_LAYER_DEEP, DesolationConfiguredFeatures.PATCH_ASH_LAYER,
                2, 7, 176, 13, 3, ON_SCORCHED_EARTH);

        registerHotspotPatchFeature(context, configuredFeatures, PATCH_ASH_LAYER_SPARSE, DesolationConfiguredFeatures.PATCH_ASH_LAYER,
                1, 2, 72, 9, 3, ON_SCORCHED_EARTH);

        registerEmberFeature(context, configuredFeatures, PATCH_EMBER_CHUNK, 1, 6);

        registerEmberFeature(context, configuredFeatures, PATCH_EMBER_CHUNK_DEEP, 2, 11);

        registerEmberFeature(context, configuredFeatures, PATCH_EMBER_CHUNK_SPARSE, 0, 3);

        registerHotspotPatchFeature(context, configuredFeatures, PATCH_ASH_BRAMBLE, DesolationConfiguredFeatures.PATCH_ASH_BRAMBLE,
                2, 4, 8, 6, 2, ON_ASH_BRAMBLE_GROUND);

        register(context, PLANT_CINDERFRUIT, configuredFeatures, DesolationConfiguredFeatures.PLANT_CINDERFRUIT,
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());

        // The boulder used to walk downwards from the heightmap until it found charred soil, which
        // meant a column without any would tunnel to bedrock height and bury the rock. Gate on the
        // surface block here instead, in data, where a datapack can retarget it.
        register(context, GIANT_BOULDER, configuredFeatures, DesolationConfiguredFeatures.GIANT_BOULDER,
                HotspotPlacement.of(HOTSPOT_SALT, 0, 2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BlockPredicateFilter.forPredicate(ON_CHARRED_SOIL),
                BiomeFilter.biome());
    }

    private static ResourceKey<PlacedFeature> createRegistryKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                 ResourceKey<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
        PlacementUtils.register(context, key, configuredFeatures.getOrThrow(feature), modifiers);
    }

    private static void registerTreeFeature(BootstrapContext<PlacedFeature> context,
                                            HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                            ResourceKey<PlacedFeature> key, int count, BlockPredicate predicate,
                                            ResourceKey<ConfiguredFeature<?, ?>> feature) {
        register(context, key, configuredFeatures, feature,
                PlacementUtils.countExtra(count, 0.1f, 1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BlockPredicateFilter.forPredicate(predicate),
                BiomeFilter.biome());
    }

    private static void registerPatchFeature(BootstrapContext<PlacedFeature> context,
                                             HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                             ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> feature,
                                             int count, int tries, int xzSpread, int ySpread, BlockPredicate groundPredicate) {
        register(context, key, configuredFeatures, feature,
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                CountPlacement.of(tries),
                RandomOffsetPlacement.of(UniformInt.of(-xzSpread, xzSpread), UniformInt.of(-ySpread, ySpread)),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
                BlockPredicateFilter.forPredicate(groundPredicate),
                BiomeFilter.biome());
    }

    /** Ember chunks: scattered by the feature itself, so only the hotspot count varies per variant. */
    private static void registerEmberFeature(BootstrapContext<PlacedFeature> context,
                                             HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                             ResourceKey<PlacedFeature> key, int baseCount, int hotspotCount) {
        register(context, key, configuredFeatures, DesolationConfiguredFeatures.PATCH_EMBER_CHUNK,
                HotspotPlacement.of(HOTSPOT_SALT, baseCount, hotspotCount),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());
    }

    /**
     * Like {@link #registerPatchFeature} but the per-chunk point count is driven by {@link HotspotPlacement}
     * instead of a flat {@link CountPlacement}, so the patches cluster into burn-scar hotspots. The internal
     * {@code tries}/spread (density within each point) is unchanged.
     */
    private static void registerHotspotPatchFeature(BootstrapContext<PlacedFeature> context,
                                                    HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                                    ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> feature,
                                                    int baseCount, int hotspotCount, int tries, int xzSpread, int ySpread,
                                                    BlockPredicate groundPredicate) {
        register(context, key, configuredFeatures, feature,
                HotspotPlacement.of(HOTSPOT_SALT, baseCount, hotspotCount),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                CountPlacement.of(tries),
                RandomOffsetPlacement.of(UniformInt.of(-xzSpread, xzSpread), UniformInt.of(-ySpread, ySpread)),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
                BlockPredicateFilter.forPredicate(groundPredicate),
                BiomeFilter.biome());
    }
}
