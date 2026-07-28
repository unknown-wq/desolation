package raltsmc.desolation.world.structure;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.tag.DesolationBiomeTags;

import java.util.Map;

/**
 * Data-generated {@code worldgen/structure} entries. The Ash Tinker Base predates the dynamic
 * registry provider and still ships as a hand-written JSON; anything added since goes through here so
 * the biome tag, the generation step and the terrain adaptation cannot drift apart from the code.
 */
public final class DesolationStructureFeatures {
	public static final ResourceKey<Structure> CHARRED_HUT = of("charred_hut");

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationStructureFeatures() {
		return;
	}

	public static void bootstrap(BootstrapContext<Structure> context) {
		HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

		// BEARD_THIN rather than BEARD_THICK: the hut is a light wooden thing and should look like it
		// was built on the slope it stands on, not like it grew a plinth.
		context.register(CHARRED_HUT, new CharredHutStructure(new Structure.StructureSettings(
				biomes.getOrThrow(DesolationBiomeTags.CHARRED_HUT_HAS_STRUCTURE),
				Map.of(),
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.BEARD_THIN)));
	}

	private static ResourceKey<Structure> of(String path) {
		return ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path));
	}
}
