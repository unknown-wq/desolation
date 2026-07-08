package raltsmc.desolation.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raltsmc.desolation.registry.DesolationLootTables;
import raltsmc.desolation.registry.DesolationStructures;

public class AshTinkerBaseGenerator extends TemplateStructurePiece {
    public AshTinkerBaseGenerator(StructureTemplateManager manager, Identifier template, BlockPos pos, Rotation rotation) {
        super(DesolationStructures.ASH_TINKER_BASE_PIECE, 0, manager, template, template.toString(),
                createPlacementData(rotation), pos);
    }

    public AshTinkerBaseGenerator(StructurePieceSerializationContext context, CompoundTag nbt) {
        super(DesolationStructures.ASH_TINKER_BASE_PIECE, nbt, context.structureTemplateManager(),
                (identifier1 -> createPlacementData(Rotation.valueOf(nbt.getStringOr("Rot", "none")))));
    }

    private static StructurePlaceSettings createPlacementData(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor serverWorldAccess,
                                  RandomSource random, BoundingBox boundingBox) {
        if ("chest".equals(metadata)) {
            serverWorldAccess.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity blockEntity = serverWorldAccess.getBlockEntity(pos.below());
            if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                chestBlockEntity.setLootTable(DesolationLootTables.ASH_TINKER_BASE);
            }
        }
    }
}
