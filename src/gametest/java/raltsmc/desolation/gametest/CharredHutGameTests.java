package raltsmc.desolation.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.storage.loot.LootTable;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationLootTables;
import raltsmc.desolation.tag.DesolationBiomeTags;
import raltsmc.desolation.world.structure.CharredHutPiece;
import raltsmc.desolation.world.structure.CharredHutStructure;
import raltsmc.desolation.world.structure.DesolationStructureFeatures;
import raltsmc.desolation.world.structure.DesolationStructureSets;

/**
 * Game tests for the Charred Hut. These live apart from {@code DesolationGameTests} because they all
 * need an arena the default 8x8x8 one cannot provide: the hut's plot is 9x9x8, and a structure that
 * does not fit in its arena is exactly how a test ends up passing on Tuesday and failing on Wednesday.
 * {@code desolation-gametest:charred_hut_arena} is a 10x10x10 empty template that does fit.
 *
 * <p>Nothing here waits on a tick or on a random roll. {@link CharredHutPiece} builds itself from a
 * seed it carries rather than from the random the world hands it, so every test drives the piece
 * directly with a fixed seed and asserts only on the parts of the layout that are placed
 * unconditionally: the hearth, the doorway, the chimney, and the staging that tells the story. The
 * randomised parts — which planks burnt through, where the ash drifted — are deliberately never
 * asserted on, so tuning them cannot break the suite.
 *
 * <p>Assertions go through {@link ServerLevel#getBlockState} on absolute positions rather than
 * through the arena-relative helpers, because {@code GameTestHelper.relativePos()} is not the inverse
 * of {@code absolutePos()} for an unrotated arena — it rotates by a further 180 degrees — and a
 * structure piece necessarily works in world coordinates.
 */
public class CharredHutGameTests {
    private static final String ARENA = "desolation-gametest:charred_hut_arena";

    /** Height, in arena coordinates, of the hut's ground course. One up, so its footing has room. */
    private static final int ARENA_GROUND_Y = 1;

    private static final long SEED = 0x0DE5_01A7_1017L;

    /*
     * The three conditions
     */

    @GameTest(structure = ARENA)
    public void standingHutKeepsItsRoofDoorwayAndLiveHearth(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.STANDING,
                CharredHutPiece.Story.FLED, false, SEED);

        // A hut that still has a roof still has coals banked under the ash.
        assertBlock(helper, hut, DesolationBlocks.EMBER_BLOCK,
                CharredHutPiece.CABIN_MIN_X, CharredHutPiece.GROUND_Y, CharredHutPiece.HEARTH_Z);

        // The firebox opens into the room through the wall.
        assertBlock(helper, hut, Blocks.AIR,
                CharredHutPiece.CABIN_MIN_X, CharredHutPiece.FLOOR_Y + 1, CharredHutPiece.HEARTH_Z);

        // The doorway is burnt open at head height, whatever the ash did to the threshold.
        assertBlock(helper, hut, Blocks.AIR,
                CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y + 1, CharredHutPiece.CABIN_MAX_Z);

        // The rafters span the roof, holes in the boards or no holes in the boards.
        assertBlock(helper, hut, DesolationBlocks.CHARRED_LOG,
                CharredHutPiece.DOOR_X, CharredHutPiece.ROOF_Y, CharredHutPiece.RAFTER_NEAR_Z);
        assertBlock(helper, hut, DesolationBlocks.CHARRED_LOG,
                CharredHutPiece.DOOR_X, CharredHutPiece.ROOF_Y, CharredHutPiece.RAFTER_FAR_Z);

        // And the shutter is still hanging off the window frame.
        assertBlock(helper, hut, DesolationBlocks.CHARRED_TRAPDOOR,
                CharredHutPiece.CABIN_MAX_X, CharredHutPiece.FLOOR_Y + 1, CharredHutPiece.WINDOW_Z);
        helper.assertTrue(stateAt(helper, hut, CharredHutPiece.CABIN_MAX_X, CharredHutPiece.FLOOR_Y + 1,
                        CharredHutPiece.WINDOW_Z).getValue(BlockStateProperties.OPEN),
                "the window shutter should have been left hanging open");

        helper.succeed();
    }

    @GameTest(structure = ARENA)
    public void collapsedHutPutsItsRoofOnTheFloorAndItsFireOut(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.COLLAPSED,
                CharredHutPiece.Story.FLED, false, SEED);

        // Nothing left overhead in the middle of the room.
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_LOG,
                CharredHutPiece.DOOR_X, CharredHutPiece.ROOF_Y, CharredHutPiece.RAFTER_NEAR_Z);
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_SLAB,
                CharredHutPiece.DOOR_X, CharredHutPiece.ROOF_Y, CharredHutPiece.RAFTER_NEAR_Z);

        // The hearth went cold a long time ago.
        assertBlock(helper, hut, DesolationBlocks.COOLED_EMBER_BLOCK,
                CharredHutPiece.CABIN_MIN_X, CharredHutPiece.GROUND_Y, CharredHutPiece.HEARTH_Z);

        // Three rafters lying across the floor. Each is broken off at its own length, but every one
        // of them reaches the middle three columns of the room.
        int onTheFloor = 0;

        for (int z = CharredHutPiece.CABIN_MIN_Z + 1; z <= CharredHutPiece.CABIN_MIN_Z + 3; ++z) {
            for (int x = CharredHutPiece.DOOR_X - 1; x <= CharredHutPiece.DOOR_X + 1; ++x) {
                if (stateAt(helper, hut, x, CharredHutPiece.FLOOR_Y, z).is(DesolationBlocks.CHARRED_LOG)) {
                    ++onTheFloor;
                }
            }
        }

        helper.assertTrue(onTheFloor >= 4,
                "expected the fallen rafters to cross the middle of the room, found " + onTheFloor + " logs");
        helper.succeed();
    }

    @GameTest(structure = ARENA)
    public void foundationHutIsNothingButItsPlanAndItsChimney(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.FOUNDATION,
                CharredHutPiece.Story.FLED, false, SEED);

        // The chimney is the whole point of this variant: it is what still marks the spot. The stack
        // is a mix of cobble and cracked brick, so this only insists that it is masonry.
        BlockState stack = stateAt(helper, hut, 0, CharredHutPiece.GROUND_Y + 3, CharredHutPiece.HEARTH_Z);
        helper.assertTrue(stack.is(Blocks.COBBLESTONE) || stack.is(Blocks.CRACKED_STONE_BRICKS),
                "expected the chimney stack to still be standing, found " + stack);

        // No walls above the stumps, and no roof at all.
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_PLANKS,
                CharredHutPiece.DOOR_X - 1, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MIN_Z);
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_SLAB,
                CharredHutPiece.DOOR_X, CharredHutPiece.ROOF_Y, CharredHutPiece.RAFTER_NEAR_Z);

        // No furniture either: there is no room left to have furnished.
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_FENCE,
                CharredHutPiece.DOOR_X + 1, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z - 2);

        helper.succeed();
    }

    /*
     * The story
     */

    @GameTest(structure = ARENA)
    public void aHutTheyEscapedFromLeavesTheGateOpenAndThePathClear(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.STANDING,
                CharredHutPiece.Story.FLED, false, SEED);

        assertBlock(helper, hut, DesolationBlocks.CHARRED_FENCE_GATE,
                CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.PLOT_SIZE - 1);
        helper.assertTrue(stateAt(helper, hut, CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y,
                        CharredHutPiece.PLOT_SIZE - 1).getValue(BlockStateProperties.OPEN),
                "the yard gate should have been left standing open");

        // The way out of the door was trodden clear of ash.
        for (int z = CharredHutPiece.CABIN_MAX_Z + 1; z < CharredHutPiece.PLOT_SIZE - 1; ++z) {
            assertBlock(helper, hut, Blocks.AIR, CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, z);
            assertBlock(helper, hut, DesolationBlocks.CHARRED_SOIL, CharredHutPiece.DOOR_X, CharredHutPiece.GROUND_Y, z);
        }

        // Somebody went down on the way out and did not stop to pick the lantern back up.
        assertBlock(helper, hut, Blocks.LANTERN,
                CharredHutPiece.DOOR_X - 1, CharredHutPiece.FLOOR_Y, CharredHutPiece.PLOT_SIZE - 1);

        // And there is nothing across the doorway.
        assertNotBlock(helper, hut, DesolationBlocks.CHARRED_LOG,
                CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z + 1);

        helper.succeed();
    }

    @GameTest(structure = ARENA)
    public void aHutNobodyEscapedFromIsBarredFromTheOutside(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.STANDING,
                CharredHutPiece.Story.TRAPPED, false, SEED);

        // A tree came down across the door, from the outside, and the gate was never touched.
        for (int x = CharredHutPiece.DOOR_X - 2; x <= CharredHutPiece.DOOR_X + 2; ++x) {
            assertBlock(helper, hut, DesolationBlocks.CHARRED_LOG,
                    x, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z + 1);
        }

        assertBlock(helper, hut, DesolationBlocks.CHARRED_FENCE_GATE,
                CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.PLOT_SIZE - 1);
        helper.assertFalse(stateAt(helper, hut, CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y,
                        CharredHutPiece.PLOT_SIZE - 1).getValue(BlockStateProperties.OPEN),
                "the yard gate of a hut nobody got out of should still be shut");

        // On the inside of that door.
        assertBlock(helper, hut, Blocks.SKELETON_SKULL,
                CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z - 1);

        // No lantern out on the path, because nobody ever carried one out there.
        assertNotBlock(helper, hut, Blocks.LANTERN,
                CharredHutPiece.DOOR_X - 1, CharredHutPiece.FLOOR_Y, CharredHutPiece.PLOT_SIZE - 1);

        helper.succeed();
    }

    /*
     * Loot
     */

    @GameTest(structure = ARENA)
    public void aHutWithAChestPointsItAtTheHutLootTable(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.STANDING,
                CharredHutPiece.Story.FLED, true, SEED);

        int x = CharredHutPiece.CABIN_MIN_X + 1;
        int z = CharredHutPiece.CABIN_MIN_Z + 1;
        assertBlock(helper, hut, Blocks.CHEST, x, CharredHutPiece.FLOOR_Y, z);

        BlockEntity blockEntity = helper.getLevel().getBlockEntity(hut.localToWorld(x, CharredHutPiece.FLOOR_Y, z));
        helper.assertTrue(blockEntity instanceof ChestBlockEntity, "the hut chest has no block entity");
        helper.assertValueEqual(((ChestBlockEntity) blockEntity).getLootTable(), DesolationLootTables.CHARRED_HUT,
                "charred hut chest loot table");

        helper.succeed();
    }

    @GameTest(structure = ARENA)
    public void aHutWithoutAChestGetsNoChest(GameTestHelper helper) {
        CharredHutPiece hut = build(helper, CharredHutPiece.Condition.STANDING,
                CharredHutPiece.Story.FLED, false, SEED);

        assertNotBlock(helper, hut, Blocks.CHEST,
                CharredHutPiece.CABIN_MIN_X + 1, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MIN_Z + 1);
        helper.succeed();
    }

    /*
     * Cross-cutting
     */

    /**
     * Every combination of condition, story and chest, over a spread of seeds, with a write window one
     * block wide. Almost nothing lands in the arena, but every branch of the generator still runs, so
     * an out-of-range roll or an illegal block state surfaces here rather than in somebody's world.
     */
    @GameTest(structure = ARENA)
    public void everyCombinationGeneratesWithoutBlowingUp(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos ground = groundCorner(helper);

        for (CharredHutPiece.Condition condition : CharredHutPiece.Condition.values()) {
            for (CharredHutPiece.Story story : CharredHutPiece.Story.values()) {
                for (int seed = 0; seed < 40; ++seed) {
                    CharredHutPiece hut = new CharredHutPiece(ground, Direction.NORTH, condition, story,
                            (seed & 1) == 0, seed);

                    hut.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                            RandomSource.create(seed), BoundingBox.fromCorners(ground, ground),
                            new ChunkPos(ground.getX() >> 4, ground.getZ() >> 4), ground);
                }
            }
        }

        helper.succeed();
    }

    /** A piece that has been through NBT has to come out the other side building the same hut. */
    @GameTest(structure = ARENA)
    public void aHutSurvivesBeingSavedAndReloaded(GameTestHelper helper) {
        BlockPos ground = groundCorner(helper);
        CharredHutPiece hut = new CharredHutPiece(ground, Direction.NORTH, CharredHutPiece.Condition.COLLAPSED,
                CharredHutPiece.Story.TRAPPED, true, SEED);

        StructurePieceSerializationContext context = StructurePieceSerializationContext.fromLevel(helper.getLevel());
        CompoundTag tag = hut.createTag(context);
        CharredHutPiece reloaded = new CharredHutPiece(context, tag);

        helper.assertValueEqual(reloaded.condition(), hut.condition(), "condition");
        helper.assertValueEqual(reloaded.story(), hut.story(), "story");
        helper.assertValueEqual(reloaded.hasChest(), hut.hasChest(), "chest");
        helper.assertValueEqual(reloaded.getBoundingBox(), hut.getBoundingBox(), "bounding box");
        helper.assertValueEqual(
                reloaded.localToWorld(CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z),
                hut.localToWorld(CharredHutPiece.DOOR_X, CharredHutPiece.FLOOR_Y, CharredHutPiece.CABIN_MAX_Z),
                "doorway position");

        helper.succeed();
    }

    /**
     * The structure, the structure set and the biome tag are all produced by {@code runDatagen} into a
     * git-ignored directory, so this is what catches a build that packaged the mod without them.
     */
    @GameTest(structure = ARENA)
    public void charredHutWorldgenIsLoaded(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        Structure structure = level.registryAccess().lookupOrThrow(Registries.STRUCTURE)
                .getOrThrow(DesolationStructureFeatures.CHARRED_HUT).value();
        StructureSet set = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET)
                .getOrThrow(DesolationStructureSets.CHARRED_HUTS).value();

        // Reaching this instance at all means the generated JSON parsed through our structure type's
        // codec, which is the half of the registration that a compile cannot check.
        helper.assertTrue(structure instanceof CharredHutStructure,
                "desolation:charred_hut did not deserialise into a CharredHutStructure");
        helper.assertValueEqual(structure.step(), GenerationStep.Decoration.SURFACE_STRUCTURES, "charred hut step");
        helper.assertValueEqual(structure.terrainAdaptation(), TerrainAdjustment.BEARD_THIN,
                "charred hut terrain adaptation");

        helper.assertTrue(set.placement() instanceof RandomSpreadStructurePlacement,
                "the charred hut structure set is not spread at random");
        RandomSpreadStructurePlacement placement = (RandomSpreadStructurePlacement) set.placement();
        helper.assertValueEqual(placement.spacing(), DesolationStructureSets.SPACING, "charred hut spacing");
        helper.assertValueEqual(placement.separation(), DesolationStructureSets.SEPARATION, "charred hut separation");

        // Huts belong under the trees and on the fringe, never in the swept-out clearing.
        HolderSet<Biome> biomes = structure.biomes();
        helper.assertTrue(biomes.stream().anyMatch(biome -> biome.is(DesolationBiomes.CHARRED_FOREST)),
                "charred huts do not generate in the charred forest");
        helper.assertTrue(biomes.stream().anyMatch(biome -> biome.is(DesolationBiomes.CHARRED_FOREST_SMALL)),
                "charred huts do not generate in the small charred forest");
        helper.assertFalse(biomes.stream().anyMatch(biome -> biome.is(DesolationBiomes.CHARRED_FOREST_CLEARING)),
                "charred huts should not generate in the clearing");
        helper.assertTrue(level.registryAccess().lookupOrThrow(Registries.BIOME)
                        .get(DesolationBiomeTags.CHARRED_HUT_HAS_STRUCTURE).isPresent(),
                "the charred hut biome tag was not loaded");

        LootTable loot = level.getServer().reloadableRegistries().getLootTable(DesolationLootTables.CHARRED_HUT);
        helper.assertTrue(loot != LootTable.EMPTY, "the charred hut loot table was not loaded");

        helper.succeed();
    }

    /*
     * Plumbing
     */

    private static CharredHutPiece build(GameTestHelper helper, CharredHutPiece.Condition condition,
                                         CharredHutPiece.Story story, boolean chest, long seed) {
        ServerLevel level = helper.getLevel();
        BlockPos ground = groundCorner(helper);
        CharredHutPiece hut = new CharredHutPiece(ground, Direction.NORTH, condition, story, chest, seed);

        // Writing through the piece's own bounding box keeps everything inside the arena; the footing
        // it would otherwise sink below the floor is the only thing that gets clipped.
        hut.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                RandomSource.create(seed), hut.getBoundingBox(),
                new ChunkPos(ground.getX() >> 4, ground.getZ() >> 4), ground);

        return hut;
    }

    /**
     * The world-space corner the hut is anchored to. An arena is placed with a rotation of the
     * framework's choosing, so {@code absolutePos(0, y, 0)} is not necessarily its low corner in world
     * coordinates — and a structure piece builds towards {@code +X/+Z} in world coordinates whatever
     * else is going on. Taking the minimum of two opposite corners lands the plot inside the arena
     * under any rotation.
     */
    private static BlockPos groundCorner(GameTestHelper helper) {
        BlockPos near = helper.absolutePos(new BlockPos(0, ARENA_GROUND_Y, 0));
        BlockPos far = helper.absolutePos(
                new BlockPos(CharredHutPiece.PLOT_SIZE, ARENA_GROUND_Y, CharredHutPiece.PLOT_SIZE));

        return new BlockPos(Math.min(near.getX(), far.getX()), near.getY(), Math.min(near.getZ(), far.getZ()));
    }

    private static BlockState stateAt(GameTestHelper helper, CharredHutPiece hut, int x, int y, int z) {
        return helper.getLevel().getBlockState(hut.localToWorld(x, y, z));
    }

    private static void assertBlock(GameTestHelper helper, CharredHutPiece hut, Block expected, int x, int y, int z) {
        BlockState state = stateAt(helper, hut, x, y, z);
        helper.assertTrue(state.is(expected),
                "expected " + expected + " at hut " + x + "/" + y + "/" + z + ", found " + state);
    }

    private static void assertNotBlock(GameTestHelper helper, CharredHutPiece hut, Block unwanted, int x, int y, int z) {
        BlockState state = stateAt(helper, hut, x, y, z);
        helper.assertFalse(state.is(unwanted),
                "did not expect " + unwanted + " at hut " + x + "/" + y + "/" + z);
    }
}
