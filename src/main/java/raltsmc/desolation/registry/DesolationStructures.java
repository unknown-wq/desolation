package raltsmc.desolation.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.world.structure.AshTinkerBaseGenerator;
import raltsmc.desolation.world.structure.AshTinkerBaseStructure;
import raltsmc.desolation.world.structure.CharredHutPiece;
import raltsmc.desolation.world.structure.CharredHutStructure;

@SuppressWarnings("SameParameterValue")
public class DesolationStructures {
	public static final StructureType<AshTinkerBaseStructure> ASH_TINKER_BASE_STRUCTURE_TYPE = registerStructureType("ash_tinker_base", AshTinkerBaseStructure.CODEC);
	public static final StructureType<CharredHutStructure> CHARRED_HUT_STRUCTURE_TYPE = registerStructureType("charred_hut", CharredHutStructure.CODEC);

	public static final StructurePieceType ASH_TINKER_BASE_PIECE = registerStructurePiece("ash_tinker_base", AshTinkerBaseGenerator::new);
	public static final StructurePieceType CHARRED_HUT_PIECE = registerStructurePiece("charred_hut", CharredHutPiece::new);

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationStructures() {
		return;
	}

	private static <S extends Structure> StructureType<S> registerStructureType(String name, MapCodec<S> codec) {
		return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), () -> codec);
	}

	private static StructurePieceType registerStructurePiece(String name, StructurePieceType piece) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), piece);
	}

	public static void init() { }
}
