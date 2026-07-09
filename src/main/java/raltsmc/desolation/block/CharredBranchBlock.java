package raltsmc.desolation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBlockTags;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class CharredBranchBlock extends UntintedParticleLeavesBlock {
    // This has to be at least the maximum placeable taxicab distance in CharredFoliagePlacer.generate()
    // ... as configured in the placer configurations of DesolationConfiguredFeatures
    public static final int SUPPORTED_MAX_TAXICAB_DISTANCE = 13;

    // Overloaded meanings in LeavesBlock.DISTANCE
    public static final int DISTANCE_SUPPORTED = 6;
    public static final int DISTANCE_UNSUPPORTED = 7;

    // These are in ticks; the intended effect is to reduce repeated searches during continuous trunk breaking.
    public static final int MINIMUM_DELAY = 60;
    public static final int DELAY_SPREAD = 100;

    public CharredBranchBlock(Properties properties) {
        super(0.01f, ParticleTypes.ASH, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LeavesBlock.DISTANCE, DISTANCE_SUPPORTED)
                .setValue(LeavesBlock.PERSISTENT, false)
                .setValue(LeavesBlock.WATERLOGGED, false));
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 0.35F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(LeavesBlock.WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.getValue(LeavesBlock.PERSISTENT) &&
                state.getValue(LeavesBlock.DISTANCE) < DISTANCE_UNSUPPORTED &&
                CharredBranchBlock.findSupportingTrunk(world, pos).isEmpty()) {
            world.setBlock(pos, state.setValue(LeavesBlock.DISTANCE, DISTANCE_UNSUPPORTED), 3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(LeavesBlock.PERSISTENT, true)
                .setValue(LeavesBlock.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    protected static void notifyLossOfSupport(Level world, BlockPos trunkPos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        findSupportedBranches(world, trunkPos).forEach((pos, state) -> {
            // Add a delay to reduce repeat checks, and spread the work over many seconds.
            world.scheduleTick(pos, state.getBlock(), MINIMUM_DELAY + random.nextInt(DELAY_SPREAD));
        });
    }

    // Desolation branches are leaves that do not require contiguous support,
    // so we have to search the entire taxicab volume for blocks to notify.
    protected static HashMap<BlockPos, BlockState> findSupportedBranches(Level world, BlockPos trunkPos) {
        HashMap<BlockPos, BlockState> found = new HashMap<>(256);
        int x, y, z, xLimit, zLimit;

        for (y = -SUPPORTED_MAX_TAXICAB_DISTANCE; y <= SUPPORTED_MAX_TAXICAB_DISTANCE; ++y) {
            xLimit = SUPPORTED_MAX_TAXICAB_DISTANCE - Math.abs(y);
            for (x = -xLimit; x <= xLimit; ++x) {
                zLimit = SUPPORTED_MAX_TAXICAB_DISTANCE - Math.abs(x) - Math.abs(y);
                for (z = -zLimit; z <= zLimit; ++z) {
                    BlockPos pos = trunkPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.is(DesolationBlocks.CHARRED_BRANCHES) &&
                            !state.getValue(LeavesBlock.PERSISTENT) &&
                            state.getValue(LeavesBlock.DISTANCE) < DISTANCE_UNSUPPORTED) {
                        found.put(pos, state);
                    }
                }
            }
        }

        return found;
    }

    // Desolation branches are leaves that do not require contiguous support,
    // so we may need to search the entire taxicab volume for a supporting log.
    protected static Optional<BlockPos> findSupportingTrunk(Level world, BlockPos branchPos) {
        int x, y, z, xLimit, zLimit;

        for (y = -SUPPORTED_MAX_TAXICAB_DISTANCE; y <= SUPPORTED_MAX_TAXICAB_DISTANCE; ++y) {
            xLimit = SUPPORTED_MAX_TAXICAB_DISTANCE - Math.abs(y);
            for (x = -xLimit; x <= xLimit; ++x) {
                zLimit = SUPPORTED_MAX_TAXICAB_DISTANCE - Math.abs(x) - Math.abs(y);
                for (z = -zLimit; z <= zLimit; ++z) {
                    BlockPos pos = branchPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.is(DesolationBlockTags.CHARRED_LOGS)) {
                        return Optional.of(pos);
                    }
                }
            }
        }

        return Optional.empty();
    }
}
