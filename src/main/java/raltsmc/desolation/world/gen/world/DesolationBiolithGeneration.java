package raltsmc.desolation.world.gen.world;

import com.terraformersmc.biolith.api.biome.BiomePlacement;
import com.terraformersmc.biolith.api.biome.sub.BiomeParameterTargets;
import com.terraformersmc.biolith.api.biome.sub.Criterion;
import com.terraformersmc.biolith.api.biome.sub.CriterionBuilder;
import com.terraformersmc.biolith.api.surface.SurfaceGeneration;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.world.gen.surfacerules.DesolationSurfaceRules;

public class DesolationBiolithGeneration {
	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationBiolithGeneration() {
		return;
	}

	public static void init() {
		// Register the surface rules. The rule resolves the charred_forests biome tag, which is
		// only bound once datapacks have loaded. DynamicRegistrySetupCallback is too early (biomes
		// absent / "Tags not bound"), so register at SERVER_STARTING where the frozen registry
		// access has both the mod's data-driven biomes and their bound tags. Biolith keeps mod
		// rules in a static collector and materializes them later during chunk generation, so this
		// still runs before the surface system is built.
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			HolderGetter<Biome> biomes = server.registryAccess().lookupOrThrow(Registries.BIOME);
			SurfaceGeneration.addOverworldSurfaceRules(
					Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "surface_rules"),
					DesolationSurfaceRules.createRules(biomes));
		});

		// Register the surface builders.
		//DesolationSurfaceBuilders.getBuilders().forEach(SurfaceGeneration::addSurfaceBuilder);

		// Add the biomes to Overworld generation via Biolith.
		double cfLargeChance = Desolation.CONFIG.charredForestChance;
		double cfSmallChance = Desolation.CONFIG.smallCharredForestChance;
		double cfClearingChance = Desolation.CONFIG.charredForestClearingChance;
		boolean generateClearings = Desolation.CONFIG.generateClearings;

		BiomePlacement.replaceOverworld(Biomes.FOREST, DesolationBiomes.CHARRED_FOREST_SMALL, cfSmallChance);
		BiomePlacement.replaceOverworld(Biomes.BIRCH_FOREST, DesolationBiomes.CHARRED_FOREST_SMALL, cfSmallChance);
		BiomePlacement.replaceOverworld(Biomes.OLD_GROWTH_BIRCH_FOREST, DesolationBiomes.CHARRED_FOREST, cfLargeChance);
		BiomePlacement.replaceOverworld(Biomes.FOREST, DesolationBiomes.CHARRED_FOREST, cfLargeChance);
		BiomePlacement.replaceOverworld(Biomes.TAIGA, DesolationBiomes.CHARRED_FOREST, cfLargeChance);
		if (generateClearings) {
			Criterion criterion = CriterionBuilder.deviationMin(
					BiomeParameterTargets.PEAKS_VALLEYS,
					(float) cfClearingChance);
			BiomePlacement.addSubOverworld(DesolationBiomes.CHARRED_FOREST, DesolationBiomes.CHARRED_FOREST_CLEARING, criterion);
			BiomePlacement.addSubOverworld(DesolationBiomes.CHARRED_FOREST_SMALL, DesolationBiomes.CHARRED_FOREST_CLEARING, criterion);
		}
	}
}
