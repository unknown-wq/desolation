package raltsmc.desolation.world.feature;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

import java.util.HashSet;

public class ScatteredFeature extends Feature<ScatteredFeatureConfig> {
    public ScatteredFeature(Codec<ScatteredFeatureConfig> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<ScatteredFeatureConfig> context) {
        final HashSet<BlockPos> blockPlacements = Sets.newHashSet();
        final WorldGenLevel world = context.level();
        FoliagePlacer.FoliageSetter blockPlacer = new FoliagePlacer.FoliageSetter() {
            @Override
            public void set(BlockPos pos, BlockState state) {
                blockPlacements.add(pos.immutable());
                world.setBlock(pos, state, 19);
            }

            @Override
            public boolean isSet(BlockPos pos) {
                return blockPlacements.contains(pos);
            }
        };
        return this.generate(context.level(), context.random(), context.origin(), blockPlacer, context.config());
    }

    private boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos, FoliagePlacer.FoliageSetter blockPlacer, ScatteredFeatureConfig config) {
        if (random.nextDouble() > config.failChance) {
            BlockState blockState = config.stateProvider.getState(world, random, pos);
            BlockPos blockPos3;
            if (config.project) {
                blockPos3 = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
            } else {
                blockPos3 = pos;
            }

            int i = 0;
            MutableBlockPos mutable = new MutableBlockPos();
            mutable.set(blockPos3);

            for (int j = 0; j < config.tries; ++j) {
                boolean bl1 = random.nextInt(4) > 0;
                boolean bl2 = false;
                mutable.setWithOffset(mutable,
                        random.nextInt(2) - random.nextInt(2),
                        random.nextInt(2) - random.nextInt(2),
                        random.nextInt(config.spreadZ + 1) - random.nextInt(config.spreadZ + 1))
                        .clamp(Axis.X, blockPos3.getX() - config.spreadX, blockPos3.getX() + config.spreadX)
                        .clamp(Axis.Y, blockPos3.getY() - config.spreadY, blockPos3.getY() + config.spreadY)
                        .clamp(Axis.Z, blockPos3.getZ() - config.spreadZ, blockPos3.getZ() + config.spreadZ);
                BlockPos blockPos4;

                if (bl1 && config.modifyGround && mutable.equals(world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, mutable).mutable())) {
                    mutable.set(world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, mutable).offset(0, -1, 0));
                    blockPos4 = mutable;
                    bl2 = true;
                } else {
                    blockPos4 = mutable.below();
                }
                BlockState blockState2 = world.getBlockState(blockPos4);

                if ((world.isEmptyBlock(mutable) || (config.canReplace && world.getBlockState(mutable).canBeReplaced()) || bl2)
                        && blockState.canSurvive(world, mutable)
                        && (config.whitelist.isEmpty() || config.whitelist.contains(blockState2))
                        && !config.blacklist.contains(blockState2)
                        && (!config.needsWater || world.getFluidState(blockPos4.west()).is(FluidTags.WATER) || world.getFluidState(blockPos4.east()).is(FluidTags.WATER) || world.getFluidState(blockPos4.north()).is(FluidTags.WATER) || world.getFluidState(blockPos4.south()).is(FluidTags.WATER))
                        && (config.genInWater || !(
                        world.getFluidState(mutable).is(FluidTags.WATER) ||
                                world.getFluidState(mutable.north()).is(FluidTags.WATER) ||
                                world.getFluidState(mutable.south()).is(FluidTags.WATER) ||
                                world.getFluidState(mutable.east()).is(FluidTags.WATER) ||
                                world.getFluidState(mutable.west()).is(FluidTags.WATER)
                ))
                ) {
                    blockPlacer.set(mutable, config.stateProvider.getState(world, random, mutable));
                    ++i;
                }

            }
            return i > 0;
        } else {
            return false;
        }
    }
}
