package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBlockTags;

import java.util.concurrent.CompletableFuture;

public class DesolationBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	protected DesolationBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void addTags(HolderLookup.Provider registries) {
		valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
			.add(DesolationBlocks.CHARRED_FENCE_GATE);

		valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE)
			.add(DesolationBlocks.CHARRED_BRANCHES);

		valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK)
			.add(DesolationBlocks.COOLED_EMBER_BLOCK)
			.add(DesolationBlocks.EMBER_BLOCK);

		valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
			.add(DesolationBlocks.ASH_BLOCK)
			.add(DesolationBlocks.ASH_LAYER_BLOCK)
			.add(DesolationBlocks.CHARRED_SOIL);

		valueLookupBuilder(BlockTags.SWORD_EFFICIENT)
			.add(DesolationBlocks.ASH_BRAMBLE);


		valueLookupBuilder(BlockTags.FLOWER_POTS)
			.add(DesolationBlocks.POTTED_CHARRED_SAPLING);

		valueLookupBuilder(BlockTags.LEAVES)
			.add(DesolationBlocks.CHARRED_BRANCHES);

		valueLookupBuilder(BlockTags.INFINIBURN_OVERWORLD)
			.add(DesolationBlocks.EMBER_BLOCK);

		valueLookupBuilder(BlockTags.LOGS)
			.addTag(DesolationBlockTags.CHARRED_LOGS);

		valueLookupBuilder(BlockTags.PLANKS)
			.add(DesolationBlocks.CHARRED_PLANKS);

		valueLookupBuilder(BlockTags.SAPLINGS)
			.add(DesolationBlocks.CHARRED_SAPLING);

		valueLookupBuilder(BlockTags.CEILING_HANGING_SIGNS)
			.add(DesolationBlocks.CHARRED_HANGING_SIGN);

		valueLookupBuilder(BlockTags.WALL_HANGING_SIGNS)
			.add(DesolationBlocks.CHARRED_WALL_HANGING_SIGN);

		valueLookupBuilder(BlockTags.STANDING_SIGNS)
			.add(DesolationBlocks.CHARRED_SIGN);

		valueLookupBuilder(BlockTags.WALL_SIGNS)
			.add(DesolationBlocks.CHARRED_WALL_SIGN);

		valueLookupBuilder(BlockTags.WOODEN_BUTTONS)
			.add(DesolationBlocks.CHARRED_BUTTON);

		valueLookupBuilder(BlockTags.WOODEN_DOORS)
			.add(DesolationBlocks.CHARRED_DOOR);

		valueLookupBuilder(BlockTags.WOODEN_FENCES)
			.add(DesolationBlocks.CHARRED_FENCE);

		valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
			.add(DesolationBlocks.CHARRED_PRESSURE_PLATE);

		valueLookupBuilder(BlockTags.WOODEN_SLABS)
			.add(DesolationBlocks.CHARRED_SLAB);

		valueLookupBuilder(BlockTags.WOODEN_STAIRS)
			.add(DesolationBlocks.CHARRED_STAIRS);

		valueLookupBuilder(BlockTags.WOODEN_TRAPDOORS)
			.add(DesolationBlocks.CHARRED_TRAPDOOR);


		valueLookupBuilder(DesolationBlockTags.CHARRED_LOGS)
			.add(DesolationBlocks.CHARRED_LOG)
			.add(DesolationBlocks.CHARRED_WOOD)
			.add(DesolationBlocks.STRIPPED_CHARRED_LOG)
			.add(DesolationBlocks.STRIPPED_CHARRED_WOOD);

		valueLookupBuilder(DesolationBlockTags.SCORCHED_EARTH)
			.add(DesolationBlocks.ASH_BLOCK)
			.add(DesolationBlocks.CHARRED_SOIL)
			.add(DesolationBlocks.COOLED_EMBER_BLOCK)
			.add(DesolationBlocks.EMBER_BLOCK);


		valueLookupBuilder(ConventionalBlockTags.STRIPPED_LOGS)
			.add(DesolationBlocks.STRIPPED_CHARRED_LOG);

		valueLookupBuilder(ConventionalBlockTags.STRIPPED_WOODS)
			.add(DesolationBlocks.STRIPPED_CHARRED_WOOD);

	}
}
