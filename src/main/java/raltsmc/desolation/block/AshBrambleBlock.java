package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AshBrambleBlock extends Block {
    public AshBrambleBlock(Properties properties) { super(properties); }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean flag) {
        if (!world.isClientSide() && world.getRandom().nextInt(25) == 0 && entity instanceof Player && entity.getDeltaMovement().length() > 0.05f) {
            //world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.2F, 0.9F);
            //world.removeBlock(pos, false);
            world.destroyBlock(pos, false, entity, 512);
        }
    }
}
