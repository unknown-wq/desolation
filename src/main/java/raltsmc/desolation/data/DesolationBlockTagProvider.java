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
		builder(BlockTags.MINEABLE_WITH_AXE)
			.add(DesolationBlocks.CHARRED_FENCE_GATE.builtInRegistryHolder().key());

		builder(BlockTags.MINEABLE_WITH_HOE)
			.add(DesolationBlocks.CHARRED_BRANCHES.builtInRegistryHolder().key());

		builder(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.COOLED_EMBER_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.EMBER_BLOCK.builtInRegistryHolder().key());

		builder(BlockTags.MINEABLE_WITH_SHOVEL)
			.add(DesolationBlocks.ASH_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.ASH_LAYER_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_SOIL.builtInRegistryHolder().key());

		builder(BlockTags.SWORD_EFFICIENT)
			.add(DesolationBlocks.ASH_BRAMBLE.builtInRegistryHolder().key());


		builder(BlockTags.FLOWER_POTS)
			.add(DesolationBlocks.POTTED_CHARRED_SAPLING.builtInRegistryHolder().key());

		builder(BlockTags.LEAVES)
			.add(DesolationBlocks.CHARRED_BRANCHES.builtInRegistryHolder().key());

		builder(BlockTags.INFINIBURN_OVERWORLD)
			.add(DesolationBlocks.EMBER_BLOCK.builtInRegistryHolder().key());

		builder(BlockTags.LOGS)
			.addTag(DesolationBlockTags.CHARRED_LOGS);

		builder(BlockTags.PLANKS)
			.add(DesolationBlocks.CHARRED_PLANKS.builtInRegistryHolder().key());

		builder(BlockTags.CEILING_HANGING_SIGNS)
			.add(DesolationBlocks.CHARRED_HANGING_SIGN.builtInRegistryHolder().key());

		builder(BlockTags.WALL_HANGING_SIGNS)
			.add(DesolationBlocks.CHARRED_WALL_HANGING_SIGN.builtInRegistryHolder().key());

		builder(BlockTags.STANDING_SIGNS)
			.add(DesolationBlocks.CHARRED_SIGN.builtInRegistryHolder().key());

		builder(BlockTags.WALL_SIGNS)
			.add(DesolationBlocks.CHARRED_WALL_SIGN.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_BUTTONS)
			.add(DesolationBlocks.CHARRED_BUTTON.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_DOORS)
			.add(DesolationBlocks.CHARRED_DOOR.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_FENCES)
			.add(DesolationBlocks.CHARRED_FENCE.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_PRESSURE_PLATES)
			.add(DesolationBlocks.CHARRED_PRESSURE_PLATE.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_SLABS)
			.add(DesolationBlocks.CHARRED_SLAB.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_STAIRS)
			.add(DesolationBlocks.CHARRED_STAIRS.builtInRegistryHolder().key());

		builder(BlockTags.WOODEN_TRAPDOORS)
			.add(DesolationBlocks.CHARRED_TRAPDOOR.builtInRegistryHolder().key());


		builder(DesolationBlockTags.CHARRED_LOGS)
			.add(DesolationBlocks.CHARRED_LOG.builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_WOOD.builtInRegistryHolder().key())
			.add(DesolationBlocks.STRIPPED_CHARRED_LOG.builtInRegistryHolder().key())
			.add(DesolationBlocks.STRIPPED_CHARRED_WOOD.builtInRegistryHolder().key());

		builder(DesolationBlockTags.SCORCHED_EARTH)
			.add(DesolationBlocks.ASH_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.CHARRED_SOIL.builtInRegistryHolder().key())
			.add(DesolationBlocks.COOLED_EMBER_BLOCK.builtInRegistryHolder().key())
			.add(DesolationBlocks.EMBER_BLOCK.builtInRegistryHolder().key());


		builder(ConventionalBlockTags.STRIPPED_LOGS)
			.add(DesolationBlocks.STRIPPED_CHARRED_LOG.builtInRegistryHolder().key());

		builder(ConventionalBlockTags.STRIPPED_WOODS)
			.add(DesolationBlocks.STRIPPED_CHARRED_WOOD.builtInRegistryHolder().key());

	}
}
