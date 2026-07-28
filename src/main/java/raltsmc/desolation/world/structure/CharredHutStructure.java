package raltsmc.desolation.world.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationStructures;

import java.util.Optional;

/**
 * Places a single {@link CharredHutPiece}. Everything the piece needs in order to build itself — how
 * much of it is left, how its story ended, whether there is a chest — is decided here, once, and then
 * travels with the piece so that reloading a chunk cannot change the answer.
 *
 * <p>The hut has a flat 9x9 footprint and no interest in hanging off a cliff, so a candidate site is
 * rejected outright unless its four corners agree on a height to within a few blocks. That is cheap
 * (four heightmap samples) and it is the reason the structure can get away with a flat foundation.
 */
public class CharredHutStructure extends Structure {
    public static final MapCodec<CharredHutStructure> CODEC = Structure.simpleCodec(CharredHutStructure::new);

    /** How uneven the four corners of the plot may be before the site is thrown away. */
    private static final int MAX_CORNER_SPREAD = 3;

    /** A hut whose people never made it out was never emptied, so its chest is far more likely. */
    private static final double TRAPPED_CHEST_BONUS = 0.35D;

    public CharredHutStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        DesolationConfig config = Desolation.CONFIG;

        if (!config.charredHutEnabled) {
            return Optional.empty();
        }

        WorldgenRandom random = context.random();

        // Drawn first and unconditionally: the structure set has already picked this chunk, and
        // thinning here keeps the spacing honest while stopping huts from feeling like street lamps.
        double rarityRoll = random.nextDouble();

        if (rarityRoll >= config.charredHutRarity) {
            return Optional.empty();
        }

        ChunkPos chunkPos = context.chunkPos();
        int originX = chunkPos.getMinBlockX();
        int originZ = chunkPos.getMinBlockZ();
        int far = CharredHutPiece.PLOT_SIZE - 1;

        int[] corners = new int[] {
                surfaceAt(context, originX, originZ),
                surfaceAt(context, originX + far, originZ),
                surfaceAt(context, originX, originZ + far),
                surfaceAt(context, originX + far, originZ + far)
        };

        int lowest = corners[0];
        int highest = corners[0];

        for (int corner : corners) {
            lowest = Math.min(lowest, corner);
            highest = Math.max(highest, corner);
        }

        if (highest - lowest > MAX_CORNER_SPREAD || lowest <= context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }

        CharredHutPiece.Condition condition = pickCondition(random);
        CharredHutPiece.Story story = random.nextBoolean()
                ? CharredHutPiece.Story.FLED : CharredHutPiece.Story.TRAPPED;
        double chestChance = story == CharredHutPiece.Story.TRAPPED
                ? config.charredHutChestChance + TRAPPED_CHEST_BONUS : config.charredHutChestChance;
        boolean chest = random.nextDouble() < chestChance;
        Direction orientation = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        long pieceSeed = random.nextLong();

        // The ground course sits one block into the terrain, so the floorboards end up flush with it.
        BlockPos ground = new BlockPos(originX, lowest - 1, originZ);

        return Optional.of(new Structure.GenerationStub(ground, (StructurePiecesBuilder collector) ->
                collector.addPiece(new CharredHutPiece(ground, orientation, condition, story, chest, pieceSeed))));
    }

    /**
     * Weighted so that the most legible ruin is also the rarest: most huts are found half down, and a
     * roof that is still up is worth walking towards.
     */
    private static CharredHutPiece.Condition pickCondition(WorldgenRandom random) {
        int roll = random.nextInt(10);

        if (roll < 3) {
            return CharredHutPiece.Condition.STANDING;
        }

        return roll < 7 ? CharredHutPiece.Condition.COLLAPSED : CharredHutPiece.Condition.FOUNDATION;
    }

    private static int surfaceAt(Structure.GenerationContext context, int x, int z) {
        return context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());
    }

    @Override
    public StructureType<?> type() {
        return DesolationStructures.CHARRED_HUT_STRUCTURE_TYPE;
    }
}
