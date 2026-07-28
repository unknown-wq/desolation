package raltsmc.desolation.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import raltsmc.desolation.registry.DesolationBlocks;

public class GiantBoulderFeature extends Feature<BlockStateConfiguration> {
    /**
     * How far the boulder may sink looking for charred soil. The placed feature already filters for
     * a charred-soil surface, so this only absorbs the odd one-block ledge; without a bound the old
     * code tunnelled all the way down to y=3 and buried boulders deep underground.
     */
    private static final int MAX_DESCENT = 4;

    public GiantBoulderFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        return this.generate(context.level(), context.chunkGenerator(), context.random(), context.origin(),
                context.config());
    }

    public boolean generate(WorldGenLevel world, ChunkGenerator chunkGenerator, RandomSource random,
                            BlockPos blockPos, BlockStateConfiguration config) {

        boolean grounded = false;
        for (int i = 0; i <= MAX_DESCENT; ++i) {
            Block block = world.getBlockState(blockPos.below()).getBlock();
            if (block == DesolationBlocks.CHARRED_SOIL) {
                grounded = true;
                break;
            }
            if (blockPos.getY() <= world.getMinY() + 1) {
                break;
            }
            blockPos = blockPos.below();
        }

        if (!grounded) {
            return false;
        } else if (world.isFluidAtPosition(blockPos, fluidState -> !fluidState.isEmpty())) {
            return false;
        } else {
            for (int i = 0; i < 18; ++i) {
                int j = (int)(random.nextInt(4) * random.nextDouble());
                int k = (int)(random.nextInt(4) * random.nextDouble());
                int l = (int)(random.nextInt(4) * random.nextDouble());
                float f = (float)(j+k+l) * random.nextFloat() + 0.5F;

                for (BlockPos blockPos2 : BlockPos.betweenClosed(
                        blockPos.offset((int) (-j * (0.5 + 0.75 * random.nextDouble())), (int) (-k * (0.5 + 0.75 * random.nextDouble())), (int) (-l * (0.5 + 0.75 * random.nextDouble()))),
                        blockPos.offset((int) (j * (0.5 + 0.75 * random.nextDouble())), (int) (k * (0.5 + 0.75 * random.nextDouble())), (int) (l * (0.5 + 0.75 * random.nextDouble()))))) {
                    if (blockPos2.distSqr(blockPos) <= (double) (f * f)) {
                        world.setBlock(blockPos2, config.state, 4);
                    }
                }
            }

            for (int i = 0; i < 54; ++i) {
                int j = (int)(random.nextInt(5) * random.nextDouble());
                int k = (int)(random.nextInt(5) * random.nextDouble());
                int l = (int)(random.nextInt(5) * random.nextDouble());
                float f = (float)(j+k+l) * random.nextFloat() + 0.5F;

                for (BlockPos pos : BlockPos.betweenClosed(
                        blockPos.offset((int) (-j * (0.5 + 0.75 * random.nextDouble())), (int) (-k * (0.5 + 0.75 * random.nextDouble())), (int) (-l * (0.5 + 0.75 * random.nextDouble()))),
                        blockPos.offset((int) (j * (0.5 + 0.75 * random.nextDouble())), (int) (k * (0.5 + 0.75 * random.nextDouble())), (int) (l * (0.5 + 0.75 * random.nextDouble())))
                )) {
                    BlockPos blockPosB2 = pos.offset(-1 + random.nextInt(2), -1 + random.nextInt(2), -1 + random.nextInt(2));
                    if (blockPosB2.distSqr(blockPos) <= (double) (f * f)) {
                        world.setBlock(blockPosB2, config.state, 4);
                    }
                }
            }

            return true;
        }
    }
}
