package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import raltsmc.desolation.registry.DesolationParticles;
import raltsmc.desolation.registry.DesolationSounds;

// TODO:  Consider merging CooledEmberBlock into EmberBlock,
//        using Properties.LIT,
//        and mixing in to tools (flint_and_steel, fire_charge, shovels) to change state.
public class EmberBlock extends Block {
    private final BlockState cooledState;

    public EmberBlock(Block cooled, Properties properties) {
        super(properties);
        this.cooledState = cooled.defaultBlockState();
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof ShovelItem) {
            if (world.isClientSide()) {
                for (int i = 0; i < 20; ++i) {
                    CampfireBlock.makeParticles(world, pos, false, true);
                }
            } else {
                world.levelEvent(null, 1009, pos, 0);
                stack.hurtAndBreak(1, player, hand);
            }
            world.playSound(player, pos, SoundEvents.BASALT_HIT, SoundSource.BLOCKS, 1f, 1f);
            world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3f, 1f);
            world.setBlockAndUpdate(pos, this.cooledState);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (world instanceof ServerLevel serverLevel && !entity.fireImmune()) {
            DamageSource hotFloor = world.damageSources().hotFloor();

            if (entity instanceof LivingEntity livingEntity && !livingEntity.isInvulnerableTo(serverLevel, hotFloor)) {
                livingEntity.hurtServer(serverLevel, hotFloor, 1.0F);

                if (serverLevel.getRandom().nextFloat() < 0.1F) {
                    livingEntity.setRemainingFireTicks(120);
                }
            }
        }

        super.stepOn(world, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
        double d = (double)pos.getX() + 0.5D;
        double e = (double)pos.getY();
        double f = (double)pos.getZ() + 0.5D;

        double g = random.nextDouble() * 0.6D - 0.3D;
        double h = random.nextDouble() * 6.0D / 16.0D;
        double i = random.nextDouble() * 0.6D - 0.3D;

        double j = random.nextDouble() * 0.6D - 0.3D;
        double k = random.nextDouble() * 6.0D / 16.0D;
        double l = random.nextDouble() * 0.6D - 0.3D;

        double rdY = (random.nextDouble() - 0.5D) / 5.0D;

        if (random.nextBoolean()) {
            world.addParticle(ParticleTypes.LARGE_SMOKE, d + g, e + h, f + i, 0.0D, 0.1D + rdY, 0.0D);
        }
        if (random.nextFloat() < 0.3f) {
            world.addParticle((ParticleOptions) DesolationParticles.SPARK, d + j, e + k, f + l, 0.0D, random.nextDouble() * 0.3D + 0.1D, 0.0D);
            if (random.nextFloat() < 0.05f) {
                int index = random.nextInt(4);
                SoundEvent popSound = switch (index) {
                    case 0 -> DesolationSounds.EMBER_BLOCK_POP_1;
                    case 1 -> DesolationSounds.EMBER_BLOCK_POP_2;
                    case 2 -> DesolationSounds.EMBER_BLOCK_POP_3;
                    case 3 -> DesolationSounds.EMBER_BLOCK_POP_4;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
                world.playLocalSound(d + j, e + k, f + l, popSound, SoundSource.BLOCKS, random.nextFloat() * 0.2F + 0.8F, 1.0F, true);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        BlockState blockState = blockView.getBlockState(blockPos);
        return shouldCool(blockView, blockPos, blockState) ? this.cooledState : super.getStateForPlacement(ctx);
    }

    private static boolean shouldCool(BlockGetter world, BlockPos pos, BlockState state) {
        return coolsIn(state) || coolsOnAnySide(world, pos) != CoolType.NONE;
    }

    private enum CoolType {
        NONE,
        TOUCHED_WATER,
        SMOTHERED
    }

    private static CoolType coolsOnAnySide(BlockGetter world, BlockPos pos) {
        boolean isSmothered = true;
        BlockPos.MutableBlockPos mutable = pos.mutable();
        // As in ConcretePowderBlock.touchesLiquid(), the block below only counts once the ember is
        // already sitting in water. That check used to read the state through the shared cursor
        // before it was offset, which only did the right thing because Direction.values() happens
        // to start with DOWN; read the state at pos explicitly instead.
        boolean isWaterlogged = coolsIn(world.getBlockState(pos));

        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN && !isWaterlogged) {
                continue;
            }

            mutable.setWithOffset(pos, direction);
            BlockState neighborState = world.getBlockState(mutable);

            if (coolsIn(neighborState) && !neighborState.isFaceSturdy(world, pos, direction.getOpposite())) {
                return CoolType.TOUCHED_WATER;
            } else if (!neighborState.isSolidRender()) {
                isSmothered = false;
            }
        }

        return isSmothered ? CoolType.SMOTHERED : CoolType.NONE;
    }

    private static boolean coolsIn(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        CoolType coolType = coolsOnAnySide(world, pos);

        if (coolType != CoolType.NONE) {
            if (world instanceof ServerLevel serverLevel && coolType == CoolType.TOUCHED_WATER) {
                serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1f, 1f);
            }

            return this.cooledState;
        }

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
}
