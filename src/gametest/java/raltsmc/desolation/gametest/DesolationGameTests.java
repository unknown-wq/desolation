package raltsmc.desolation.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import raltsmc.desolation.block.CharredBranchBlock;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationEntities;
import raltsmc.desolation.world.feature.DesolationConfiguredFeatures;
import raltsmc.desolation.world.feature.DesolationPlacedFeatures;

/**
 * Server game tests covering the mechanics that are easy to break from the outside: branch
 * support, ember cooling, mob registration and the data-generated worldgen files.
 *
 * <p>The branch tests are the regression net for the support search in
 * {@link CharredBranchBlock}: it no longer sweeps the whole taxicab volume looking for
 * neighbours, so a mistake there is invisible until branches stop falling (or start falling off
 * healthy trees).
 */
public class DesolationGameTests {
    // notifyLossOfSupport() delays each branch by MINIMUM_DELAY + rand(DELAY_SPREAD) ticks.
    private static final int SUPPORT_CHECK_TICKS =
            CharredBranchBlock.MINIMUM_DELAY + CharredBranchBlock.DELAY_SPREAD;

    @GameTest(maxTicks = SUPPORT_CHECK_TICKS * 3)
    public void charredBranchesFallWhenTheirTrunkIsBroken(GameTestHelper helper) {
        BlockPos trunk = new BlockPos(1, 1, 1);
        BlockPos branch = new BlockPos(1, 2, 1);

        helper.setBlock(trunk, DesolationBlocks.CHARRED_LOG.defaultBlockState());
        helper.setBlock(branch, DesolationBlocks.CHARRED_BRANCHES.defaultBlockState());
        helper.assertBlockProperty(branch, LeavesBlock.DISTANCE, CharredBranchBlock.DISTANCE_SUPPORTED);

        helper.destroyBlock(trunk);

        helper.succeedWhen(() -> {
            BlockState state = helper.getBlockState(branch);

            // Losing support flips the branch to DISTANCE_UNSUPPORTED, after which it decays on a
            // random tick; either state means it stopped hanging in the air.
            if (state.is(DesolationBlocks.CHARRED_BRANCHES)) {
                helper.assertBlockProperty(branch, LeavesBlock.DISTANCE, CharredBranchBlock.DISTANCE_UNSUPPORTED);
            }
        });
    }

    @GameTest(maxTicks = SUPPORT_CHECK_TICKS * 3)
    public void charredBranchesSurviveWhileAnyTrunkStands(GameTestHelper helper) {
        BlockPos lowerTrunk = new BlockPos(1, 1, 1);
        BlockPos upperTrunk = new BlockPos(1, 2, 1);
        BlockPos branch = new BlockPos(1, 3, 1);

        helper.setBlock(lowerTrunk, DesolationBlocks.CHARRED_LOG.defaultBlockState());
        helper.setBlock(upperTrunk, DesolationBlocks.CHARRED_LOG.defaultBlockState());
        helper.setBlock(branch, DesolationBlocks.CHARRED_BRANCHES.defaultBlockState());

        // Breaking the lower log notifies the branch through the log above it, and that log is
        // still there to support it.
        helper.destroyBlock(lowerTrunk);

        helper.runAtTickTime(SUPPORT_CHECK_TICKS * 2, () -> {
            helper.assertBlockPresent(DesolationBlocks.CHARRED_BRANCHES, branch);
            helper.assertBlockProperty(branch, LeavesBlock.DISTANCE, CharredBranchBlock.DISTANCE_SUPPORTED);
            helper.succeed();
        });
    }

    @GameTest
    public void emberBlockCoolsWhenWaterTouchesIt(GameTestHelper helper) {
        BlockPos ember = new BlockPos(1, 1, 1);

        helper.setBlock(ember, DesolationBlocks.EMBER_BLOCK.defaultBlockState());
        helper.assertBlockPresent(DesolationBlocks.EMBER_BLOCK, ember);

        helper.setBlock(ember.above(), Blocks.WATER.defaultBlockState());

        helper.succeedWhenBlockPresent(DesolationBlocks.COOLED_EMBER_BLOCK, ember);
    }

    @GameTest
    public void desolationMobsCanBeSpawned(GameTestHelper helper) {
        BlockPos blackened = new BlockPos(1, 1, 1);
        BlockPos scuttler = new BlockPos(4, 1, 4);

        helper.spawnWithNoFreeWill(DesolationEntities.BLACKENED, blackened);
        helper.spawnWithNoFreeWill(DesolationEntities.ASH_SCUTTLER, scuttler);

        helper.succeedIf(() -> {
            helper.assertEntityPresent(DesolationEntities.BLACKENED, blackened);
            helper.assertEntityPresent(DesolationEntities.ASH_SCUTTLER, scuttler);
        });
    }

    // The worldgen files are produced by runDatagen into a git-ignored directory, so this fails
    // loudly if a build ever packages the mod without them.
    @GameTest
    public void charredForestWorldgenIsLoaded(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(DesolationConfiguredFeatures.TREE_CHARRED);
        level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
                .getOrThrow(DesolationPlacedFeatures.TREES_CHARRED_LARGE);

        Biome charredForest = level.registryAccess().lookupOrThrow(Registries.BIOME)
                .getOrThrow(DesolationBiomes.CHARRED_FOREST).value();

        boolean placesCharredTrees = charredForest.getGenerationSettings().features().stream()
                .flatMap(HolderSet::stream)
                .anyMatch(feature -> feature.is(DesolationPlacedFeatures.TREES_CHARRED_LARGE));

        helper.assertTrue(placesCharredTrees, "the charred forest biome does not place charred trees");
        helper.succeed();
    }

    // The tree feature picks its trunk height and scatters its branches at random, so give it a
    // few goes before calling a failure a failure.
    @GameTest(maxAttempts = 4, requiredSuccesses = 1)
    public void charredTreeFeatureGrowsLogsAndBranches(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos soil = new BlockPos(3, 0, 3);

        helper.setBlock(soil, Blocks.DIRT.defaultBlockState());

        ConfiguredFeature<?, ?> tree = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(DesolationConfiguredFeatures.TREE_CHARRED_SMALL).value();

        helper.assertTrue(
                tree.place(level, level.getChunkSource().getGenerator(), level.getRandom(), helper.absolutePos(soil.above())),
                "the small charred tree feature refused to generate");

        helper.assertBlockPresent(DesolationBlocks.CHARRED_LOG, soil.above());
        helper.assertBlockPresent(DesolationBlocks.CHARRED_BRANCHES);
        helper.succeed();
    }
}
