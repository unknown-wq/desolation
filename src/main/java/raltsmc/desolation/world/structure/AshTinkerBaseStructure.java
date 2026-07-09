package raltsmc.desolation.world.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationStructures;

import java.util.Optional;

public class AshTinkerBaseStructure extends Structure {
    public static final MapCodec<AshTinkerBaseStructure> CODEC = AshTinkerBaseStructure.simpleCodec(AshTinkerBaseStructure::new);
    private static final Identifier TINKER_BASE_MAIN = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "ash_tinker_base/ash_tinker_base");

    public AshTinkerBaseStructure(Structure.StructureSettings config) {
        super(config);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        if (Structure.getLowestY(context, 16, 16) > context.chunkGenerator().getSeaLevel()) {
            return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, collector -> this.addPieces(collector, context));
        } else {
            return Optional.empty();
        }
    }

    private void addPieces(StructurePiecesBuilder collector, Structure.GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        int y = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        BlockPos pos = new BlockPos(x, y, z);
        Rotation rotation = Rotation.getRandom(context.random());
        StructureTemplateManager manager = context.structureTemplateManager();

        collector.addPiece(new AshTinkerBaseGenerator(manager, TINKER_BASE_MAIN, pos, rotation));

        collector.getBoundingBox();
    }

    @Override
    public StructureType<?> type() {
        return DesolationStructures.ASH_TINKER_BASE_STRUCTURE_TYPE;
    }
}
