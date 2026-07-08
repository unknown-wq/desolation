package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import raltsmc.desolation.registry.DesolationBlocks;

public class CharredSaplingBlock extends SaplingBlock {
    public CharredSaplingBlock(TreeGrower generator, Properties properties) {
        super(generator, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(BlockTags.DIRT) || floor.is(DesolationBlocks.CHARRED_SOIL);
    }
}
