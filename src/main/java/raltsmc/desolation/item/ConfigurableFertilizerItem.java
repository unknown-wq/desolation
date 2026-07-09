package raltsmc.desolation.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ConfigurableFertilizerItem extends Item {
    private double growChance;
    private int growTries;

    public ConfigurableFertilizerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        if (useOnFertilizable(context.getItemInHand(), world, blockPos, growChance, growTries)) {
            if (!world.isClientSide()) {
                world.levelEvent(2005, blockPos, 0);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public static boolean useOnFertilizable(ItemStack stack, Level world, BlockPos pos, double growChance, int growTries) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof BonemealableBlock fertilizable) {
            if (fertilizable.isValidBonemealTarget(world, pos, blockState)) {
                if (world instanceof ServerLevel) {
                    for (int i=0; i<growTries; i++) {
                        if (fertilizable.isBonemealSuccess(world, world.getRandom(), pos, blockState) && world.getRandom().nextDouble() < growChance) {
                            fertilizable.performBonemeal((ServerLevel)world, world.getRandom(), pos, blockState);
                        }
                    }
                    stack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    public void setGrowChance(double growChance) {
        this.growChance = growChance;
    }

    public double getGrowChance() {
        return this.growChance;
    }

    public void setGrowTries(int growTries) {
        this.growTries = growTries;
    }

    public int getGrowTries() {
        return this.growTries;
    }
}
