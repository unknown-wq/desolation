package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.registry.DesolationJukeboxSongs;
import raltsmc.desolation.world.feature.DesolationConfiguredFeatures;
import raltsmc.desolation.world.feature.DesolationPlacedFeatures;
import raltsmc.desolation.world.structure.DesolationStructureFeatures;
import raltsmc.desolation.world.structure.DesolationStructureSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DesolationDynamicRegistryProvider extends FabricDynamicRegistryProvider {
	protected DesolationDynamicRegistryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	public static void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, DesolationConfiguredFeatures::bootstrap);
		registryBuilder.add(Registries.PLACED_FEATURE, DesolationPlacedFeatures::bootstrap);
		registryBuilder.add(Registries.BIOME, DesolationBiomes::bootstrap);
		registryBuilder.add(Registries.JUKEBOX_SONG, DesolationJukeboxSongs::bootstrap);
		registryBuilder.add(Registries.STRUCTURE, DesolationStructureFeatures::bootstrap);
		registryBuilder.add(Registries.STRUCTURE_SET, DesolationStructureSets::bootstrap);
	}

	@Override
	public void configure(HolderLookup.Provider registries, Entries entries) {
		addAll(entries, registries.lookupOrThrow(Registries.CONFIGURED_FEATURE), Desolation.MOD_ID);
		addAll(entries, registries.lookupOrThrow(Registries.PLACED_FEATURE), Desolation.MOD_ID);
		addAll(entries, registries.lookupOrThrow(Registries.BIOME), Desolation.MOD_ID);
		addAll(entries, registries.lookupOrThrow(Registries.JUKEBOX_SONG), Desolation.MOD_ID);
		addAll(entries, registries.lookupOrThrow(Registries.STRUCTURE), Desolation.MOD_ID);
		addAll(entries, registries.lookupOrThrow(Registries.STRUCTURE_SET), Desolation.MOD_ID);
	}

	@Override
	public String getName() {
		return "Desolation Dynamic Registries";
	}

	/**
	 * Version of FabricDynamicRegistryProvider.Entries.addAll() using specified mod ID.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public <T> List<Holder<T>> addAll(Entries entries, HolderLookup.RegistryLookup<T> registry, String modId) {
		return registry.listElementIds()
				.filter(registryKey -> registryKey.identifier().getNamespace().equals(modId))
				.map(key -> entries.add(registry, key))
				.toList();
	}
}
