package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import raltsmc.desolation.registry.DesolationBlocks;

public class CooledEmberBlock extends Block {
    public CooledEmberBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() == Items.FLINT_AND_STEEL) {
            world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
            world.setBlockAndUpdate(pos, DesolationBlocks.EMBER_BLOCK.defaultBlockState());
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            stack.hurtAndBreak(1, player, hand);

            return InteractionResult.SUCCESS;
        } else if (stack.getItem() == Items.FIRE_CHARGE) {
            ((FireChargeItem) Items.FIRE_CHARGE).playSound(world, pos);
            world.setBlockAndUpdate(pos, DesolationBlocks.EMBER_BLOCK.defaultBlockState());
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            stack.shrink(1);

            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }
}
