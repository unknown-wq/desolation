package raltsmc.desolation.block;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    // Every offset inside the supported taxicab volume (3303 of them), ordered by ascending
    // distance so a search that only needs one hit usually stops after a handful of lookups.
    private static final Vec3i[] SUPPORT_VOLUME_OFFSETS = buildSupportVolumeOffsets();

    // The 26 face, edge and corner neighbours; branches placed by CharredFoliagePlacer touch each
    // other (and the trunk) on at least one of those, so this is the adjacency used to walk a tree.
    private static final Vec3i[] ADJACENT_OFFSETS = buildAdjacentOffsets();

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

    // Desolation branches are leaves that do not require contiguous support, but everything a
    // charred tree puts down is reachable from its trunk by hopping between touching branch and
    // log blocks. Flooding the tree costs a fraction of sweeping the whole taxicab volume, so the
    // sweep is only kept as a fallback for the rare log that has no branch attached to it at all.
    protected static HashMap<BlockPos, BlockState> findSupportedBranches(Level world, BlockPos trunkPos) {
        HashMap<BlockPos, BlockState> found = floodSupportedBranches(world, trunkPos);

        return found.isEmpty() ? sweepSupportedBranches(world, trunkPos) : found;
    }

    // Breadth-first walk over the branches and logs touching trunkPos, bounded by the same taxicab
    // volume the support rules use. Positions are visited through packed longs so the search does
    // not allocate a BlockPos for every air block it looks at.
    private static HashMap<BlockPos, BlockState> floodSupportedBranches(Level world, BlockPos trunkPos) {
        HashMap<BlockPos, BlockState> found = new HashMap<>(256);
        LongSet visited = new LongOpenHashSet(512);
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();

        visited.add(trunkPos.asLong());
        queue.enqueue(trunkPos.asLong());

        BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        while (!queue.isEmpty()) {
            current.set(queue.dequeueLong());

            for (Vec3i offset : ADJACENT_OFFSETS) {
                neighbor.setWithOffset(current, offset);

                if (taxicabDistance(trunkPos, neighbor) > SUPPORTED_MAX_TAXICAB_DISTANCE ||
                        !visited.add(neighbor.asLong())) {
                    continue;
                }

                BlockState state = world.getBlockState(neighbor);

                if (state.is(DesolationBlocks.CHARRED_BRANCHES)) {
                    queue.enqueue(neighbor.asLong());

                    if (!state.getValue(LeavesBlock.PERSISTENT) &&
                            state.getValue(LeavesBlock.DISTANCE) < DISTANCE_UNSUPPORTED) {
                        found.put(neighbor.immutable(), state);
                    }
                } else if (state.is(DesolationBlockTags.CHARRED_LOGS)) {
                    // Keep walking the rest of the trunk so branches attached higher up are notified.
                    queue.enqueue(neighbor.asLong());
                }
            }
        }

        return found;
    }

    private static HashMap<BlockPos, BlockState> sweepSupportedBranches(Level world, BlockPos trunkPos) {
        HashMap<BlockPos, BlockState> found = new HashMap<>(256);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (Vec3i offset : SUPPORT_VOLUME_OFFSETS) {
            mutable.setWithOffset(trunkPos, offset);
            BlockState state = world.getBlockState(mutable);

            if (state.is(DesolationBlocks.CHARRED_BRANCHES) &&
                    !state.getValue(LeavesBlock.PERSISTENT) &&
                    state.getValue(LeavesBlock.DISTANCE) < DISTANCE_UNSUPPORTED) {
                found.put(mutable.immutable(), state);
            }
        }

        return found;
    }

    // Desolation branches are leaves that do not require contiguous support, so we may need to
    // search the entire taxicab volume for a supporting log. The offsets are ordered by distance
    // and the search returns on the first hit, so a branch that is still attached to its tree only
    // costs a few lookups; only a branch that really lost its support pays for the full volume,
    // and it does so exactly once because it is left at DISTANCE_UNSUPPORTED afterwards.
    protected static Optional<BlockPos> findSupportingTrunk(Level world, BlockPos branchPos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (Vec3i offset : SUPPORT_VOLUME_OFFSETS) {
            mutable.setWithOffset(branchPos, offset);

            if (world.getBlockState(mutable).is(DesolationBlockTags.CHARRED_LOGS)) {
                return Optional.of(mutable.immutable());
            }
        }

        return Optional.empty();
    }

    private static int taxicabDistance(Vec3i from, Vec3i to) {
        return Math.abs(to.getX() - from.getX())
                + Math.abs(to.getY() - from.getY())
                + Math.abs(to.getZ() - from.getZ());
    }

    private static Vec3i[] buildSupportVolumeOffsets() {
        List<Vec3i> offsets = new ArrayList<>(3303);

        for (int y = -SUPPORTED_MAX_TAXICAB_DISTANCE; y <= SUPPORTED_MAX_TAXICAB_DISTANCE; ++y) {
            int xLimit = SUPPORTED_MAX_TAXICAB_DISTANCE - Math.abs(y);
            for (int x = -xLimit; x <= xLimit; ++x) {
                int zLimit = xLimit - Math.abs(x);
                for (int z = -zLimit; z <= zLimit; ++z) {
                    offsets.add(new Vec3i(x, y, z));
                }
            }
        }

        offsets.sort(Comparator.comparingInt(offset ->
                Math.abs(offset.getX()) + Math.abs(offset.getY()) + Math.abs(offset.getZ())));

        return offsets.toArray(new Vec3i[0]);
    }

    private static Vec3i[] buildAdjacentOffsets() {
        List<Vec3i> offsets = new ArrayList<>(26);

        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    if (x != 0 || y != 0 || z != 0) {
                        offsets.add(new Vec3i(x, y, z));
                    }
                }
            }
        }

        return offsets.toArray(new Vec3i[0]);
    }
}
