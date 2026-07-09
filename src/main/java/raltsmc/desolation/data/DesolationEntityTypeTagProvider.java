package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import raltsmc.desolation.registry.DesolationEntities;

import java.util.concurrent.CompletableFuture;

public class DesolationEntityTypeTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
	protected DesolationEntityTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void addTags(HolderLookup.Provider registries) {
		/*
		 * Basic entity type tags
		 */

		valueLookupBuilder(EntityTypeTags.ARTHROPOD)
			.add(DesolationEntities.ASH_SCUTTLER);

		valueLookupBuilder(EntityTypeTags.SKELETONS)
			.add(DesolationEntities.BLACKENED);
	}
}
