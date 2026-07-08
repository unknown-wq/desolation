package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CharredLogBlock extends RotatedPillarBlock {
    public CharredLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, world, pos, moved);

        if (!state.is(world.getBlockState(pos).getBlock())) {
            CharredBranchBlock.notifyLossOfSupport(world, pos);
        }
    }
}
