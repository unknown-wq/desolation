package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.tag.DesolationBiomeTags;

import java.util.concurrent.CompletableFuture;

public class DesolationBiomeTagProvider extends FabricTagsProvider<Biome> {
	protected DesolationBiomeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.BIOME, registriesFuture);
	}

	@Override
	public void addTags(HolderLookup.Provider registries) {
		/*
		 * Vanilla biome categories
		 */
		builder(BiomeTags.IS_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);


		/*
		 * Conventional biome categories
		 */
		builder(ConventionalBiomeTags.IS_TEMPERATE_OVERWORLD)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(ConventionalBiomeTags.IS_OVERWORLD)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);


		/*
		 * Biome structure generation tags
		 */
		builder(BiomeTags.HAS_MINESHAFT)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(BiomeTags.HAS_RUINED_PORTAL_STANDARD)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(BiomeTags.HAS_STRONGHOLD)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(BiomeTags.HAS_TRIAL_CHAMBERS)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(DesolationBiomeTags.ASH_TINKER_BASE_HAS_STRUCTURE)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);

		builder(DesolationBiomeTags.CHARRED_FORESTS)
			.addOptional(DesolationBiomes.CHARRED_FOREST)
			.addOptional(DesolationBiomes.CHARRED_FOREST_CLEARING)
			.addOptional(DesolationBiomes.CHARRED_FOREST_SMALL);
	}
}
