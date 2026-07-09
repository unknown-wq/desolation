package raltsmc.desolation.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.world.gen.foliage.CharredFoliagePlacer;
import raltsmc.desolation.world.gen.trunk.BasedTrunkPlacer;
import raltsmc.desolation.world.gen.trunk.FallenTrunkPlacer;

import java.util.List;

import static net.minecraft.world.level.block.SnowLayerBlock.LAYERS;
import static raltsmc.desolation.block.CinderfruitPlantBlock.AGE;

public final class DesolationConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE_CHARRED = createRegistryKey("tree_charred");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE_CHARRED_SMALL = createRegistryKey("tree_charred_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE_CHARRED_FALLEN = createRegistryKey("tree_charred_fallen");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE_CHARRED_FALLEN_SMALL = createRegistryKey("tree_charred_fallen_small");

    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_CHARRED_SAPLING = createRegistryKey("patch_charred_sapling");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_SCORCHED_TUFT = createRegistryKey("patch_scorched_tuft");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_ASH_LAYER = createRegistryKey("patch_ash_layer");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_EMBER_CHUNK = createRegistryKey("patch_ember_chunk");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_ASH_BRAMBLE = createRegistryKey("patch_ash_bramble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANT_CINDERFRUIT = createRegistryKey("plant_cinderfruit");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_BOULDER = createRegistryKey("giant_boulder");

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationConfiguredFeatures() {
        return;
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, TREE_CHARRED, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(DesolationBlocks.CHARRED_LOG.defaultBlockState()),
                new BasedTrunkPlacer(6, 10, 1),
                BlockStateProvider.simple(DesolationBlocks.CHARRED_BRANCHES.defaultBlockState()),
                new CharredFoliagePlacer(UniformInt.of(3, 5), ConstantInt.of(0),
                        UniformInt.of(3, 4)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());

        FeatureUtils.register(context, TREE_CHARRED_SMALL, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(DesolationBlocks.CHARRED_LOG.defaultBlockState()),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(DesolationBlocks.CHARRED_BRANCHES.defaultBlockState()),
                new CharredFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0),
                        ConstantInt.of(2)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());

        FeatureUtils.register(context, TREE_CHARRED_FALLEN, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(DesolationBlocks.CHARRED_LOG.defaultBlockState()),
                new FallenTrunkPlacer(6, 10, 1),
                BlockStateProvider.simple(DesolationBlocks.CHARRED_BRANCHES.defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 0),
                new TwoLayersFeatureSize(0,0,0))
                .ignoreVines()
                .build());

        FeatureUtils.register(context, TREE_CHARRED_FALLEN_SMALL, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(DesolationBlocks.CHARRED_LOG.defaultBlockState()),
                new FallenTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(DesolationBlocks.CHARRED_BRANCHES.defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 0),
                new TwoLayersFeatureSize(0,0,0))
                .ignoreVines()
                .build());

        // In 26.1 the RANDOM_PATCH feature was removed. Patches are now a SIMPLE_BLOCK configured
        // feature scattered via placement modifiers (see DesolationPlacedFeatures).
        FeatureUtils.register(context, PATCH_CHARRED_SAPLING, Feature.SIMPLE_BLOCK, Configs.CHARRED_SAPLING_CONFIG);

        FeatureUtils.register(context, PATCH_SCORCHED_TUFT, Feature.SIMPLE_BLOCK, Configs.SCORCHED_TUFT_CONFIG);

        FeatureUtils.register(context, PATCH_ASH_LAYER, Feature.SIMPLE_BLOCK, Configs.ASH_LAYER_CONFIG);

        FeatureUtils.register(context, PATCH_EMBER_CHUNK, DesolationFeatures.SCATTERED, Configs.EMBER_CHUNK_CONFIG);

        FeatureUtils.register(context, PATCH_ASH_BRAMBLE, Feature.SIMPLE_BLOCK, Configs.ASH_BRAMBLE_CONFIG);

        FeatureUtils.register(context, PLANT_CINDERFRUIT, DesolationFeatures.SCATTERED, Configs.PLANT_CINDERFRUIT_CONFIG);

        FeatureUtils.register(context, GIANT_BOULDER, DesolationFeatures.GIANT_BOULDER, Configs.GIANT_BOULDER_CONFIG);
    }

    public static final class Configs {
        public static final SimpleBlockConfiguration CHARRED_SAPLING_CONFIG = new SimpleBlockConfiguration(
                BlockStateProvider.simple(DesolationBlocks.CHARRED_SAPLING));

        public static final SimpleBlockConfiguration SCORCHED_TUFT_CONFIG = new SimpleBlockConfiguration(
                BlockStateProvider.simple(DesolationBlocks.SCORCHED_TUFT));

        public static final SimpleBlockConfiguration ASH_LAYER_CONFIG = new SimpleBlockConfiguration(
                new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 1), 30)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 2), 20)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 3), 15)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 4), 13)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 5), 10)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 6), 7)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 7), 3)
                        .add(DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState().setValue(LAYERS, 8), 2)
                        .build()));

        public static final ScatteredFeatureConfig EMBER_CHUNK_CONFIG = new ScatteredFeatureConfig.Builder(
                new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(DesolationBlocks.EMBER_BLOCK.defaultBlockState(), 1)
                        .add(DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(), 1)
                        .build()))
                .tries(5)
                .spreadX(3)
                .spreadY(0)
                .spreadZ(3)
                .whitelist(List.of(DesolationBlocks.CHARRED_SOIL.defaultBlockState(),
                        DesolationBlocks.EMBER_BLOCK.defaultBlockState(),
                        DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState()))
                .canReplace()
                .modifyGround()
                .build();

        public static final SimpleBlockConfiguration ASH_BRAMBLE_CONFIG = new SimpleBlockConfiguration(
                BlockStateProvider.simple(DesolationBlocks.ASH_BRAMBLE));

        public static final ScatteredFeatureConfig PLANT_CINDERFRUIT_CONFIG = new ScatteredFeatureConfig.Builder(
                BlockStateProvider.simple(DesolationBlocks.CINDERFRUIT_PLANT.defaultBlockState().setValue(AGE, 1)))
                .tries(1)
                .spreadX(0)
                .spreadY(0)
                .spreadZ(0)
                .failChance(0.92D)
                .whitelist(List.of(DesolationBlocks.CHARRED_SOIL.defaultBlockState()))
                .blacklist(List.of(Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState()))
                .build();

        public static final BlockStateConfiguration GIANT_BOULDER_CONFIG = new BlockStateConfiguration(Blocks.STONE.defaultBlockState());
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createRegistryKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name));
    }
}
