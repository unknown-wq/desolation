package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBlockTags;
import raltsmc.desolation.tag.DesolationItemTags;

import java.util.concurrent.CompletableFuture;

public class DesolationItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	protected DesolationItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, BlockTagsProvider blockTagProvider) {
		super(output, registriesFuture, blockTagProvider);
	}

	@Override
	public void addTags(HolderLookup.Provider registries) {
		copy(BlockTags.LEAVES, ItemTags.LEAVES);

		copy(BlockTags.LOGS, ItemTags.LOGS);

		builder(ItemTags.NON_FLAMMABLE_WOOD)
			.addTag(DesolationItemTags.CHARRED_LOGS)
			.add(DesolationBlocks.CHARRED_BUTTON.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_DOOR.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_FENCE.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_FENCE_GATE.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_PLANKS.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_PRESSURE_PLATE.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_SLAB.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_STAIRS.asItem().builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_TRAPDOOR.asItem().builtInRegistryHolder().key());

		copy(BlockTags.PLANKS, ItemTags.PLANKS);

		// BlockTags.SAPLINGS was removed in 26.2; add the sapling to the item tag directly.
		builder(ItemTags.SAPLINGS)
			.add(DesolationBlocks.CHARRED_SAPLING.asItem().builtInRegistryHolder().key());

		copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);

		copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);

		copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);

		copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);

		copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);

		copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);

		copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);

		copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);

		copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);


		copy(DesolationBlockTags.CHARRED_LOGS, DesolationItemTags.CHARRED_LOGS);


		copy(ConventionalBlockTags.STRIPPED_LOGS, ConventionalItemTags.STRIPPED_LOGS);

		copy(ConventionalBlockTags.STRIPPED_WOODS, ConventionalItemTags.STRIPPED_WOODS);
	}
}
