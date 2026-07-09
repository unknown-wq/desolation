package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AshLayerBlock extends SnowLayerBlock {
    public AshLayerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.getValue(LAYERS) > 1) {
            world.setBlock(pos, this.defaultBlockState().setValue(LAYERS, state.getValue(LAYERS) - 1), 1);
        }
    }
}
