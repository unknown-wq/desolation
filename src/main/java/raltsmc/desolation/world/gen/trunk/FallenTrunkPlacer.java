package raltsmc.desolation.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationTrunkPlacerTypes;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class FallenTrunkPlacer extends StraightTrunkPlacer {
    public static Predicate<BlockState> REPLACEABLE_PREDICATE = BlockStatePredicate.forBlock(DesolationBlocks.ASH_BLOCK)
            .or(BlockStatePredicate.forBlock(DesolationBlocks.ASH_LAYER_BLOCK))
            .or(BlockStatePredicate.forBlock(DesolationBlocks.EMBER_BLOCK));

    public FallenTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    public static final MapCodec<FallenTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> trunkPlacerParts(instance).apply(instance, FallenTrunkPlacer::new));

    @Override
    protected TrunkPlacerType<?> type() {
        return DesolationTrunkPlacerTypes.FALLEN;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel world, BiConsumer<BlockPos, BlockState> replacer,
                                                            RandomSource random, int height, BlockPos startPos,
                                                            TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> treeNodes = Lists.newArrayList();

        Direction.Axis placementAxis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction placementDirection = Direction.fromAxisAndDirection(placementAxis, random.nextBoolean() ?
                Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);

        BlockPos.MutableBlockPos currentPos = startPos.mutable();

        int supportedAndFree = 0;
        for (int i = 0; i < height; ++i) {
            if (!world.isStateAtPosition(startPos.relative(placementDirection, i).below(),
                    BlockStatePredicate.forBlock(Blocks.AIR).or(BlockStatePredicate.forBlock(Blocks.WATER)))
                    && !world.isStateAtPosition(startPos.relative(placementDirection, i).below(),
                            state -> state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES))
                    && validTreePos(world, startPos)) {
                supportedAndFree++;
            }
        }

        if (supportedAndFree / (float)height > 0.6) {
            for (int i = 0; i < height; ++i) {
                currentPos.move(placementDirection);
                if (world.isStateAtPosition(currentPos, BlockStatePredicate.forBlock(DesolationBlocks.CHARRED_SOIL)
                        .or(BlockStatePredicate.forBlock(DesolationBlocks.CHARRED_LOG)))) { break; }
                placeTrunkBlock(world, replacer, random, currentPos, config, placementAxis, treeNodes);
            }
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(currentPos, 0, false));
    }

    protected static boolean placeTrunkBlock(WorldGenLevel world, BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos blockPos, TreeConfiguration treeFeatureConfig, Direction.Axis axis, List<FoliagePlacer.FoliageAttachment> treeNodes) {
        if (TreeFeature.validTreePos(world, blockPos)) {
            replacer.accept(blockPos, treeFeatureConfig.trunkProvider.getState(world, random, blockPos).setValue(RotatedPillarBlock.AXIS, axis));
            treeNodes.add(new FoliagePlacer.FoliageAttachment(blockPos.immutable(), 0, false));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean validTreePos(WorldGenLevel world, BlockPos pos) {
        return TreeFeature.validTreePos(world, pos) || world.isStateAtPosition(pos, REPLACEABLE_PREDICATE);
    }

}
