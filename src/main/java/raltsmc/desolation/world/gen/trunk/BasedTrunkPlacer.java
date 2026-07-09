package raltsmc.desolation.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BasedTrunkPlacer extends StraightTrunkPlacer {
    public static Predicate<BlockState> REPLACEABLE_PREDICATE = BlockStatePredicate.forBlock(DesolationBlocks.ASH_BLOCK)
            .or(BlockStatePredicate.forBlock(DesolationBlocks.ASH_LAYER_BLOCK))
            .or(BlockStatePredicate.forBlock(DesolationBlocks.EMBER_BLOCK));

    public BasedTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    public static final MapCodec<BasedTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> trunkPlacerParts(instance).apply(instance, BasedTrunkPlacer::new));

    @Override
    protected TrunkPlacerType<?> type() {
        return DesolationTrunkPlacerTypes.BASED;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel world, BiConsumer<BlockPos, BlockState> replacer,
                                                            RandomSource random, int height, BlockPos startPos,
                                                            TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> treeNodes = Lists.newArrayList();

        BlockPos.MutableBlockPos currentPos = startPos.mutable();
        int maxBaseHeight = 2;

        for (int i = 0; i < height; ++i) {
            placeTrunkBlock(world, replacer, random, currentPos, config, Direction.UP.getAxis(), treeNodes);
            if (i == 0) {
                generateBase(world, replacer, random, currentPos, config, maxBaseHeight, treeNodes);
            }
            currentPos.move(Direction.UP);
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(currentPos, 0, false));
    }

    protected void generateBase(WorldGenLevel world, BiConsumer<BlockPos, BlockState> replacer,
                                       RandomSource random, BlockPos pos, TreeConfiguration config, int maxBaseHeight,
                                       List<FoliagePlacer.FoliageAttachment> treeNodes) {
        List<Direction> dirs = Arrays.asList(Direction.values());
        Collections.shuffle(dirs);
        for (Direction dir : dirs) {
            int height = random.nextInt(maxBaseHeight + 1);
            if (height > 0 && dir != Direction.UP && dir != Direction.DOWN && random.nextInt(3) > 0) {
                BlockPos.MutableBlockPos startPos = pos.relative(dir).mutable();
                if (!validTreePos(world, startPos.below())) {
                    for (int j = 0; j < height; j++) {
                        placeTrunkBlock(world, replacer, random, startPos.above(j), config, Direction.UP.getAxis(), treeNodes);
                    }
                    if (random.nextBoolean()) {
                        maxBaseHeight--;
                    }
                }
            }
        }
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
