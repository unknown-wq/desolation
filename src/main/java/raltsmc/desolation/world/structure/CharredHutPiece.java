package raltsmc.desolation.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationLootTables;
import raltsmc.desolation.registry.DesolationStructures;

/**
 * A trapper's one-room cabin that the fire found. Built block by block rather than stamped from an
 * NBT template, because everything interesting about the hut is the damage: which walls are left,
 * where the roof came down, how deep the ash drifted, whether the people inside got out.
 *
 * <h2>Local coordinate frame</h2>
 * {@link StructurePiece} lays pieces out in a left-handed local frame: local {@code +X} is
 * {@link Direction#EAST} and local {@code +Z} is {@link Direction#NORTH}, and {@code placeBlock()}
 * transforms block states to match. Every direction written into a block state below therefore uses
 * the {@code LOCAL_*} constants rather than a world direction. Local {@code y = 0} is the ground
 * course — the block flush with the surrounding terrain — so the floor you walk on is {@code y = 1}.
 *
 * <h2>Determinism</h2>
 * The random passed to {@link #postProcess} is re-seeded per chunk, which would make a hut straddling
 * a chunk border disagree with itself. The piece instead carries its own {@link #seed} and rebuilds a
 * {@link RandomSource} from it on every call, so each of the (up to four) chunk passes draws exactly
 * the same numbers in exactly the same order and only the bounding box differs. It also means a hut
 * is reproducible from its saved data, which is what the game tests lean on.
 */
public class CharredHutPiece extends StructurePiece {
    /** Footprint of the whole homestead: the cabin plus the yard around it. */
    public static final int PLOT_SIZE = 9;
    public static final int PLOT_HEIGHT = 8;

    // The cabin itself, inset into the plot so there is a yard on every side.
    public static final int CABIN_MIN_X = 1;
    public static final int CABIN_MAX_X = 7;
    public static final int CABIN_MIN_Z = 1;
    public static final int CABIN_MAX_Z = 6;

    /** Ground course (flush with the terrain); the floorboards sit one block above it. */
    public static final int GROUND_Y = 0;
    public static final int FLOOR_Y = 1;
    public static final int WALL_TOP_Y = 3;
    public static final int ROOF_Y = 4;

    /** The doorway, in the front (high local Z) wall. */
    public static final int DOOR_X = 4;
    /** The chimney runs up the low-X wall; the firebox opens into the room here. */
    public static final int HEARTH_Z = 3;
    /** The shutter, in the high-X wall. */
    public static final int WINDOW_Z = 4;
    /** The two rafters the roof hangs from, and the rows they end up on when it comes down. */
    public static final int RAFTER_NEAR_Z = CABIN_MIN_Z + 1;
    public static final int RAFTER_FAR_Z = CABIN_MIN_Z + 3;

    // Local frame aliases: see the class comment. Using Direction.NORTH to mean "towards +Z" reads
    // wrong out of context, so nothing below spells the world directions out.
    private static final Direction LOCAL_FORWARD = Direction.NORTH;  // +Z, towards the door
    private static final Direction LOCAL_BACK = Direction.SOUTH;     // -Z
    private static final Direction LOCAL_RIGHT = Direction.EAST;     // +X, towards the window
    private static final Direction LOCAL_LEFT = Direction.WEST;      // -X, towards the chimney

    private static final int FOOTING_DEPTH = 4;

    /**
     * How much of the cabin the fire left. Three finds that read differently from a distance, so a
     * player who has seen one still has a reason to walk over to the next.
     */
    public enum Condition {
        /** Walls up, roof holed, door burnt out of its frame. Recognisably a house. */
        STANDING,
        /** Two walls down to waist height, roof on the floor, rafters across the room. */
        COLLAPSED,
        /** Nothing but the footprint, the corner stumps and the chimney standing alone. */
        FOUNDATION;

        private static final Condition[] VALUES = values();

        public static Condition byName(String name) {
            for (Condition condition : VALUES) {
                if (condition.name().equals(name)) {
                    return condition;
                }
            }

            return STANDING;
        }
    }

    /**
     * What happened to whoever lived here. Never stated, only staged: an open gate and a trodden path
     * against a barred door and what the fire left behind it.
     */
    public enum Story {
        /** The gate is open, the door lies flat in the yard, a dropped lantern still burns on the path. */
        FLED,
        /** A burning tree came down across the doorway. The gate is shut. Nobody came back for the chest. */
        TRAPPED;

        private static final Story[] VALUES = values();

        public static Story byName(String name) {
            for (Story story : VALUES) {
                if (story.name().equals(name)) {
                    return story;
                }
            }

            return FLED;
        }
    }

    private final Condition condition;
    private final Story story;
    private final boolean chest;
    private final long seed;

    public CharredHutPiece(BlockPos groundPos, Direction orientation, Condition condition, Story story,
                           boolean chest, long seed) {
        super(DesolationStructures.CHARRED_HUT_PIECE, 0,
                makeBoundingBox(groundPos.getX(), groundPos.getY(), groundPos.getZ(), orientation,
                        PLOT_SIZE, PLOT_HEIGHT, PLOT_SIZE));
        this.setOrientation(orientation);
        this.condition = condition;
        this.story = story;
        this.chest = chest;
        this.seed = seed;
    }

    public CharredHutPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
        super(DesolationStructures.CHARRED_HUT_PIECE, nbt);
        this.condition = Condition.byName(nbt.getStringOr("Condition", Condition.STANDING.name()));
        this.story = Story.byName(nbt.getStringOr("Story", Story.FLED.name()));
        this.chest = nbt.getBooleanOr("Chest", false);
        this.seed = nbt.getLongOr("HutSeed", 0L);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.putString("Condition", this.condition.name());
        nbt.putString("Story", this.story.name());
        nbt.putBoolean("Chest", this.chest);
        nbt.putLong("HutSeed", this.seed);
    }

    public Condition condition() {
        return this.condition;
    }

    public Story story() {
        return this.story;
    }

    public boolean hasChest() {
        return this.chest;
    }

    /**
     * Maps a local build coordinate onto its world position. Exposed so the game tests can look at
     * the doorway or the hearth without having to reimplement the local frame.
     */
    public BlockPos localToWorld(int x, int y, int z) {
        return this.getWorldPos(x, y, z).immutable();
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
                            RandomSource chunkRandom, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        RandomSource random = RandomSource.create(this.seed);

        this.clearPlot(level, box);
        this.layGround(level, box);
        this.layFloor(level, box, random);
        this.raiseWalls(level, box, random);
        this.buildChimney(level, box, random);
        this.raiseRoof(level, box, random);
        this.driftAsh(level, box, random);
        this.furnish(level, box, random);
        this.dressYard(level, box, random);
        this.tellStory(level, box, random);
        this.placeChest(level, box, random);
    }

    /*
     * Site preparation
     */

    /**
     * Clears the volume the hut occupies. The cabin gets its full height; the yard is only cleared to
     * shoulder height so the surrounding forest still crowds in over the fence instead of the hut
     * sitting in a perfect cube of nothing.
     */
    private void clearPlot(WorldGenLevel level, BoundingBox box) {
        BlockState air = Blocks.AIR.defaultBlockState();

        this.generateBox(level, box, 0, FLOOR_Y, 0, PLOT_SIZE - 1, FLOOR_Y + 1, PLOT_SIZE - 1, air, air, false);
        this.generateBox(level, box, CABIN_MIN_X, FLOOR_Y, CABIN_MIN_Z, CABIN_MAX_X, ROOF_Y + 1, CABIN_MAX_Z,
                air, air, false);
        this.generateBox(level, box, 0, FLOOR_Y, HEARTH_Z, 0, PLOT_HEIGHT - 1, HEARTH_Z, air, air, false);
    }

    /** The homestead was cleared ground before the fire and is scoured earth after it. */
    private void layGround(WorldGenLevel level, BoundingBox box) {
        BlockState soil = DesolationBlocks.CHARRED_SOIL.defaultBlockState();

        for (int x = 0; x < PLOT_SIZE; ++x) {
            for (int z = 0; z < PLOT_SIZE; ++z) {
                this.placeBlock(level, soil, x, GROUND_Y, z, box);
                this.fillFooting(level, box, soil, x, z);
            }
        }
    }

    /**
     * Carries the ground course a few blocks down so the plot does not hang off a slope. Bounded on
     * purpose: an unbounded column walk would happily tunnel to bedrock over a cave.
     */
    private void fillFooting(WorldGenLevel level, BoundingBox box, BlockState state, int x, int z) {
        for (int depth = 1; depth <= FOOTING_DEPTH; ++depth) {
            BlockPos pos = this.getWorldPos(x, GROUND_Y - depth, z);

            if (!box.isInside(pos)) {
                return;
            }

            BlockState below = level.getBlockState(pos);

            if (!below.isAir() && !below.liquid()) {
                return;
            }

            level.setBlock(pos, state, 2);
        }
    }

    /*
     * The cabin
     */

    /** Floorboards, burnt through in patches down to bare earth and, here and there, to live coals. */
    private void layFloor(WorldGenLevel level, BoundingBox box, RandomSource random) {
        BlockState planks = DesolationBlocks.CHARRED_PLANKS.defaultBlockState();
        BlockState soil = DesolationBlocks.CHARRED_SOIL.defaultBlockState();
        BlockState cooled = DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState();
        float burntThrough = switch (this.condition) {
            case STANDING -> 0.18F;
            case COLLAPSED -> 0.40F;
            case FOUNDATION -> 0.78F;
        };

        for (int x = CABIN_MIN_X; x <= CABIN_MAX_X; ++x) {
            for (int z = CABIN_MIN_Z; z <= CABIN_MAX_Z; ++z) {
                float roll = random.nextFloat();
                BlockState state = roll < burntThrough * 0.25F ? cooled : roll < burntThrough ? soil : planks;
                this.placeBlock(level, state, x, GROUND_Y, z, box);
            }
        }
    }

    /**
     * Walls of charred planks between charred-log corner posts. How much of them survives is the main
     * thing that separates the three conditions; the openings (doorway, shutter, the hole the fire
     * punched through the back wall) are cut afterwards so they read the same on every hut.
     */
    private void raiseWalls(WorldGenLevel level, BoundingBox box, RandomSource random) {
        BlockState planks = DesolationBlocks.CHARRED_PLANKS.defaultBlockState();
        BlockState post = DesolationBlocks.CHARRED_LOG.defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        BlockState air = Blocks.AIR.defaultBlockState();

        // Which side took it worst. Drawn even when it goes unused so the draw order stays fixed.
        boolean fallenSideIsRight = random.nextBoolean();

        for (int x = CABIN_MIN_X; x <= CABIN_MAX_X; ++x) {
            for (int z = CABIN_MIN_Z; z <= CABIN_MAX_Z; ++z) {
                boolean onWall = x == CABIN_MIN_X || x == CABIN_MAX_X || z == CABIN_MIN_Z || z == CABIN_MAX_Z;

                if (!onWall) {
                    continue;
                }

                boolean corner = (x == CABIN_MIN_X || x == CABIN_MAX_X) && (z == CABIN_MIN_Z || z == CABIN_MAX_Z);
                boolean onFallenSide = fallenSideIsRight ? x == CABIN_MAX_X || z == CABIN_MIN_Z
                        : x == CABIN_MIN_X || z == CABIN_MAX_Z;
                int top = this.wallTop(corner, onFallenSide);

                for (int y = FLOOR_Y; y <= top; ++y) {
                    // Higher courses are the first to go, and a corner post outlasts the panel beside it.
                    float survival = corner ? 0.97F : 1.0F - 0.14F * (y - FLOOR_Y) - (onFallenSide ? 0.22F : 0.0F);

                    if (random.nextFloat() < survival) {
                        this.placeBlock(level, corner ? post : planks, x, y, z, box);
                    }
                }
            }
        }

        // Doorway: burnt out of its frame, always open, always readable from outside.
        this.placeBlock(level, air, DOOR_X, FLOOR_Y, CABIN_MAX_Z, box);
        this.placeBlock(level, air, DOOR_X, FLOOR_Y + 1, CABIN_MAX_Z, box);

        if (this.condition == Condition.STANDING) {
            this.placeBlock(level, planks, DOOR_X, WALL_TOP_Y, CABIN_MAX_Z, box);
            // The shutter survived, hanging open on the one hinge the fire missed.
            this.placeBlock(level, DesolationBlocks.CHARRED_TRAPDOOR.defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_LEFT)
                            .setValue(BlockStateProperties.HALF, Half.BOTTOM)
                            .setValue(BlockStateProperties.OPEN, true),
                    CABIN_MAX_X, FLOOR_Y + 1, WINDOW_Z, box);
        } else {
            this.placeBlock(level, air, CABIN_MAX_X, FLOOR_Y + 1, WINDOW_Z, box);
        }

        // The fire came in through the back and took the wall with it.
        this.placeBlock(level, air, DOOR_X - 1, FLOOR_Y + 1, CABIN_MIN_Z, box);
        this.placeBlock(level, air, DOOR_X, FLOOR_Y + 1, CABIN_MIN_Z, box);
    }

    private int wallTop(boolean corner, boolean onFallenSide) {
        return switch (this.condition) {
            case STANDING -> WALL_TOP_Y;
            case COLLAPSED -> onFallenSide ? FLOOR_Y + (corner ? 1 : 0) : WALL_TOP_Y - 1;
            // Only the corner posts are left, burnt down to knee-high stumps.
            case FOUNDATION -> corner ? FLOOR_Y + 1 : FLOOR_Y - 1;
        };
    }

    /**
     * The chimney: the one part of the hut that was never going to burn, and therefore the part that
     * still marks the spot when everything else is gone.
     */
    private void buildChimney(WorldGenLevel level, BoundingBox box, RandomSource random) {
        BlockState stack = Blocks.COBBLESTONE.defaultBlockState();
        BlockState cracked = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        int top = (this.condition == Condition.COLLAPSED ? 4 : 6) + random.nextInt(2);

        for (int y = GROUND_Y; y <= top; ++y) {
            this.placeBlock(level, random.nextFloat() < 0.28F ? cracked : stack, 0, y, HEARTH_Z, box);
        }

        // The stack lost its cap at some point after the fire.
        if (random.nextBoolean()) {
            this.placeBlock(level, Blocks.AIR.defaultBlockState(), 0, top, HEARTH_Z, box);
        }

        // Firebox: a hole punched through the wall, with a stone apron on the room side of it.
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), CABIN_MIN_X, FLOOR_Y, HEARTH_Z, box);
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), CABIN_MIN_X, FLOOR_Y + 1, HEARTH_Z, box);
        this.placeBlock(level, stack, CABIN_MIN_X, WALL_TOP_Y, HEARTH_Z, box);
        this.placeBlock(level, stack, CABIN_MIN_X + 1, GROUND_Y, HEARTH_Z, box);

        // A hut that still has its roof still has coals under the ash; the ruins have gone cold.
        this.placeBlock(level, this.condition == Condition.STANDING
                        ? DesolationBlocks.EMBER_BLOCK.defaultBlockState()
                        : DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(),
                CABIN_MIN_X, GROUND_Y, HEARTH_Z, box);
        this.placeBlock(level, ashLayer(2 + random.nextInt(3)), CABIN_MIN_X + 1, FLOOR_Y, HEARTH_Z, box);
    }

    /**
     * Rafters first, then whatever is left of the boards on top of them. When the roof came down the
     * rafters came with it, so a collapsed hut gets its logs lying across the floor instead.
     */
    private void raiseRoof(WorldGenLevel level, BoundingBox box, RandomSource random) {
        if (this.condition == Condition.FOUNDATION) {
            return;
        }

        BlockState rafter = DesolationBlocks.CHARRED_LOG.defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X);
        BlockState boards = DesolationBlocks.CHARRED_SLAB.defaultBlockState()
                .setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
        BlockState eave = DesolationBlocks.CHARRED_STAIRS.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_BACK)
                .setValue(BlockStateProperties.HALF, Half.TOP);

        if (this.condition == Condition.STANDING) {
            for (int x = CABIN_MIN_X; x <= CABIN_MAX_X; ++x) {
                for (int z = CABIN_MIN_Z; z <= CABIN_MAX_Z; ++z) {
                    // The two rafters the roof hung from are the last thing to give way, so they are
                    // still up there spanning the holes the boards left behind.
                    if (z == RAFTER_NEAR_Z || z == RAFTER_FAR_Z) {
                        this.placeBlock(level, rafter, x, ROOF_Y, z, box);
                        continue;
                    }

                    if (random.nextFloat() < 0.30F) {
                        continue; // burnt through
                    }

                    this.placeBlock(level, z == CABIN_MAX_Z ? eave : boards, x, ROOF_Y, z, box);
                }
            }

            return;
        }

        // Collapsed: a few boards still cling to the eaves, and the rafters are on the floor.
        for (int x = CABIN_MIN_X; x <= CABIN_MAX_X; ++x) {
            for (int z = CABIN_MIN_Z; z <= CABIN_MAX_Z; ++z) {
                boolean edge = x == CABIN_MIN_X || x == CABIN_MAX_X || z == CABIN_MIN_Z || z == CABIN_MAX_Z;

                if (edge && random.nextFloat() < 0.22F) {
                    this.placeBlock(level, boards, x, ROOF_Y, z, box);
                }
            }
        }

        // The rafters came down still parallel, which is what makes a collapse read as a collapse
        // rather than as a woodpile: three lines across the floor, each broken off at its own length.
        for (int z = CABIN_MIN_Z + 1; z <= CABIN_MIN_Z + 3; ++z) {
            int from = CABIN_MIN_X + random.nextInt(3);
            int to = CABIN_MAX_X - random.nextInt(3);

            for (int x = from; x <= to; ++x) {
                this.placeBlock(level, rafter, x, FLOOR_Y, z, box);
            }
        }
    }

    /*
     * Dressing
     */

    /** Ash drifts into everything that is still standing, and lies deepest where nothing is. */
    private void driftAsh(WorldGenLevel level, BoundingBox box, RandomSource random) {
        float coverage = switch (this.condition) {
            case STANDING -> 0.35F;
            case COLLAPSED -> 0.55F;
            case FOUNDATION -> 0.72F;
        };

        for (int x = 0; x < PLOT_SIZE; ++x) {
            for (int z = 0; z < PLOT_SIZE; ++z) {
                boolean inside = x >= CABIN_MIN_X && x <= CABIN_MAX_X && z >= CABIN_MIN_Z && z <= CABIN_MAX_Z;
                float chance = inside ? coverage : coverage * 0.6F;

                if (random.nextFloat() >= chance) {
                    continue;
                }

                if (!this.getBlock(level, x, FLOOR_Y, z, box).isAir()) {
                    continue;
                }

                // Corners collect the deepest drifts; open ground gets a dusting.
                int depth = 1 + random.nextInt(inside ? 4 : 2);
                this.placeBlock(level, ashLayer(depth), x, FLOOR_Y, z, box);
            }
        }

        // Where the roof came down the fire pooled and is still cooling.
        int emberCount = this.condition == Condition.STANDING ? 1 : 3;

        for (int i = 0; i < emberCount; ++i) {
            int x = CABIN_MIN_X + 1 + random.nextInt(CABIN_MAX_X - CABIN_MIN_X - 1);
            int z = CABIN_MIN_Z + 1 + random.nextInt(CABIN_MAX_Z - CABIN_MIN_Z - 1);
            this.placeBlock(level, DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(), x, GROUND_Y, z, box);
        }
    }

    /**
     * What was in the room. Wood did not survive the fire, so the furniture is only readable as its
     * wreckage: an upended table top, the two posts a bed used to hang between, a shelf that somehow
     * kept its bracket.
     */
    private void furnish(WorldGenLevel level, BoundingBox box, RandomSource random) {
        if (this.condition == Condition.FOUNDATION) {
            return;
        }

        BlockState fence = DesolationBlocks.CHARRED_FENCE.defaultBlockState();

        // The table went over: the top is lying on the floor and one leg is still pointing at the roof.
        this.placeBlock(level, DesolationBlocks.CHARRED_SLAB.defaultBlockState()
                .setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM), DOOR_X, FLOOR_Y, CABIN_MAX_Z - 2, box);
        this.placeBlock(level, fence, DOOR_X + 1, FLOOR_Y, CABIN_MAX_Z - 2, box);

        // The bed burned out from between its posts.
        this.placeBlock(level, fence, CABIN_MAX_X - 1, FLOOR_Y, CABIN_MIN_Z + 1, box);
        this.placeBlock(level, fence, CABIN_MAX_X - 1, FLOOR_Y, CABIN_MIN_Z + 3, box);
        this.placeBlock(level, ashLayer(1 + random.nextInt(3)), CABIN_MAX_X - 1, FLOOR_Y, CABIN_MIN_Z + 2, box);

        if (this.condition == Condition.STANDING) {
            // A shelf on the back wall, and the pot that hung over the fire, which metal being metal
            // is the only piece of kitchen left.
            this.placeBlock(level, DesolationBlocks.CHARRED_SLAB.defaultBlockState()
                            .setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP),
                    DOOR_X + 1, FLOOR_Y + 1, CABIN_MIN_Z + 1, box);
            this.placeBlock(level, Blocks.CAULDRON.defaultBlockState(), CABIN_MIN_X + 1, FLOOR_Y, HEARTH_Z + 1, box);
        }
    }

    /** The yard: a firewood stack, the cooking fire, a fence that mostly is not there any more. */
    private void dressYard(WorldGenLevel level, BoundingBox box, RandomSource random) {
        BlockState fence = DesolationBlocks.CHARRED_FENCE.defaultBlockState();
        BlockState cordwood = DesolationBlocks.CHARRED_LOG.defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.Z);

        // Firewood stacked against the gable end, with the top course knocked off it.
        for (int z = CABIN_MIN_Z + 1; z <= CABIN_MIN_Z + 3; ++z) {
            this.placeBlock(level, cordwood, PLOT_SIZE - 1, FLOOR_Y, z, box);

            if (random.nextFloat() < 0.6F) {
                this.placeBlock(level, cordwood, PLOT_SIZE - 1, FLOOR_Y + 1, z, box);
            }
        }

        this.placeBlock(level, cordwood, PLOT_SIZE - 1, FLOOR_Y, CABIN_MIN_Z + 4, box);

        // The cooking fire, long out, still ringed by its hearth stones. Kept off to the side of the
        // door so that whatever ends up staged across the threshold does not bury it.
        int fireX = CABIN_MIN_X;
        int fireZ = PLOT_SIZE - 2;
        this.placeBlock(level, Blocks.CAMPFIRE.defaultBlockState()
                        .setValue(BlockStateProperties.LIT, false)
                        .setValue(BlockStateProperties.SIGNAL_FIRE, false)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_FORWARD),
                fireX, FLOOR_Y, fireZ, box);
        this.placeBlock(level, DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(), fireX - 1, GROUND_Y, fireZ, box);
        this.placeBlock(level, DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(), fireX + 1, GROUND_Y, fireZ, box);
        this.placeBlock(level, DesolationBlocks.COOLED_EMBER_BLOCK.defaultBlockState(), fireX, GROUND_Y, fireZ + 1, box);

        // The yard fence, burnt down to the odd post. The corner posts are kept so the line still reads.
        for (int x = CABIN_MIN_X; x <= CABIN_MAX_X; ++x) {
            if (x == DOOR_X) {
                continue;
            }

            boolean cornerPost = x == CABIN_MIN_X || x == CABIN_MAX_X;

            if (cornerPost || random.nextFloat() < 0.45F) {
                this.placeBlock(level, fence, x, FLOOR_Y, PLOT_SIZE - 1, box);
            }
        }

        // Something is always growing back at the edges.
        for (int i = 0; i < 5; ++i) {
            int x = random.nextInt(PLOT_SIZE);
            int z = random.nextInt(PLOT_SIZE);

            if (x >= CABIN_MIN_X && x <= CABIN_MAX_X && z >= CABIN_MIN_Z && z <= CABIN_MAX_Z) {
                continue;
            }

            if (this.getBlock(level, x, FLOOR_Y, z, box).isAir()) {
                this.placeBlock(level, DesolationBlocks.SCORCHED_TUFT.defaultBlockState(), x, FLOOR_Y, z, box);
            }
        }
    }

    /**
     * The one thing in the hut that is not scenery. Everything staged here answers the same question
     * from opposite directions, and it is the only difference a player is meant to actually notice.
     */
    private void tellStory(WorldGenLevel level, BoundingBox box, RandomSource random) {
        int gateZ = PLOT_SIZE - 1;
        int outsideDoorZ = CABIN_MAX_Z + 1;

        if (this.story == Story.FLED) {
            // The gate stands open on a path trodden clear of ash, the door is lying flat where it
            // was shouldered out of the frame, and a lantern went down on the way out and is burning
            // yet, which is the only light for a very long way.
            for (int z = outsideDoorZ; z < PLOT_SIZE; ++z) {
                this.placeBlock(level, Blocks.AIR.defaultBlockState(), DOOR_X, FLOOR_Y, z, box);
                this.placeBlock(level, DesolationBlocks.CHARRED_SOIL.defaultBlockState(), DOOR_X, GROUND_Y, z, box);
            }

            this.placeBlock(level, DesolationBlocks.CHARRED_FENCE_GATE.defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_RIGHT)
                            .setValue(BlockStateProperties.OPEN, true),
                    DOOR_X, FLOOR_Y, gateZ, box);

            this.placeBlock(level, DesolationBlocks.CHARRED_TRAPDOOR.defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_FORWARD)
                            .setValue(BlockStateProperties.HALF, Half.BOTTOM)
                            .setValue(BlockStateProperties.OPEN, false),
                    DOOR_X + 1, FLOOR_Y, outsideDoorZ, box);
            this.placeBlock(level, Blocks.LANTERN.defaultBlockState()
                            .setValue(BlockStateProperties.HANGING, false),
                    DOOR_X - 1, FLOOR_Y, PLOT_SIZE - 1, box);

            return;
        }

        // Trapped: a tree came down across the door from the outside and the gate was never opened.
        this.placeBlock(level, DesolationBlocks.CHARRED_FENCE_GATE.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, LOCAL_FORWARD)
                        .setValue(BlockStateProperties.OPEN, false),
                DOOR_X, FLOOR_Y, gateZ, box);

        BlockState fallenTrunk = DesolationBlocks.CHARRED_LOG.defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Direction.Axis.X);

        for (int x = DOOR_X - 2; x <= DOOR_X + 2; ++x) {
            this.placeBlock(level, fallenTrunk, x, FLOOR_Y, outsideDoorZ, box);
        }

        this.placeBlock(level, fallenTrunk, DOOR_X, FLOOR_Y + 1, outsideDoorZ, box);

        // And on the inside of that door, half under the ash.
        this.placeBlock(level, Blocks.SKELETON_SKULL.defaultBlockState()
                        .setValue(BlockStateProperties.ROTATION_16, random.nextInt(16)),
                DOOR_X, FLOOR_Y, CABIN_MAX_Z - 1, box);
    }

    /**
     * Whatever was worth keeping and not worth carrying. Whether there is anything here at all is
     * decided in {@link CharredHutStructure}, which weighs it by how the story ended.
     */
    private void placeChest(WorldGenLevel level, BoundingBox box, RandomSource random) {
        if (!this.chest) {
            return;
        }

        int x = CABIN_MIN_X + 1;
        int z = CABIN_MIN_Z + 1;
        this.placeBlock(level, Blocks.AIR.defaultBlockState(), x, FLOOR_Y, z, box);
        this.createChest(level, box, random, x, FLOOR_Y, z, DesolationLootTables.CHARRED_HUT);

        // Half the reason to bring a pick: in a collapsed hut the chest is under a rafter.
        if (this.condition == Condition.COLLAPSED) {
            this.placeBlock(level, DesolationBlocks.CHARRED_LOG.defaultBlockState()
                            .setValue(BlockStateProperties.AXIS, Direction.Axis.Z),
                    x, FLOOR_Y + 1, z, box);
        }
    }

    private static BlockState ashLayer(int depth) {
        return DesolationBlocks.ASH_LAYER_BLOCK.defaultBlockState()
                .setValue(BlockStateProperties.LAYERS, Mth.clamp(depth, 1, 8));
    }
}
