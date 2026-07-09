package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import raltsmc.desolation.block.CinderfruitPlantBlock;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationItems;

import java.util.concurrent.CompletableFuture;

public class DesolationBlockLootTableProvider extends FabricBlockLootSubProvider {
	protected DesolationBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generate() {
		dropSelf(DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK);
		dropWhenSilkTouch(DesolationBlocks.ASH_LAYER_BLOCK);
		dropOther(DesolationBlocks.ASH_LAYER_BLOCK, DesolationItems.ASH_PILE);
		dropSelf(DesolationBlocks.ASH_BLOCK);
		add(DesolationBlocks.CHARRED_BRANCHES, LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(2))
				.add(LootItem.lootTableItem(Items.STICK).when(LootItemRandomChanceCondition.randomChance(0.18f)))));
		dropSelf(DesolationBlocks.CHARRED_BUTTON);
		add(DesolationBlocks.CHARRED_DOOR, this::createDoorTable);
		dropSelf(DesolationBlocks.CHARRED_FENCE);
		dropSelf(DesolationBlocks.CHARRED_FENCE_GATE);
		dropSelf(DesolationBlocks.CHARRED_LOG);
		dropSelf(DesolationBlocks.CHARRED_PLANKS);
		dropSelf(DesolationBlocks.CHARRED_PRESSURE_PLATE);
		dropSelf(DesolationBlocks.CHARRED_SAPLING);
		dropSelf(DesolationBlocks.CHARRED_HANGING_SIGN);
		dropSelf(DesolationBlocks.CHARRED_SIGN);
		add(DesolationBlocks.CHARRED_SLAB, this::createSlabItemTable);
		dropSelf(DesolationBlocks.CHARRED_SOIL);
		dropSelf(DesolationBlocks.CHARRED_STAIRS);
		dropSelf(DesolationBlocks.CHARRED_TRAPDOOR);
		dropSelf(DesolationBlocks.CHARRED_WOOD);
		add(DesolationBlocks.CINDERFRUIT_PLANT, block -> this.createCropDrops(block,
				DesolationItems.CINDERFRUIT, DesolationItems.CINDERFRUIT_SEEDS,
				LootItemBlockStatePropertyCondition.hasBlockStateProperties(DesolationBlocks.CINDERFRUIT_PLANT)
						.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CinderfruitPlantBlock.AGE, 1))));
		dropSelf(DesolationBlocks.COOLED_EMBER_BLOCK);
		dropSelf(DesolationBlocks.EMBER_BLOCK);
		dropPottedContents(DesolationBlocks.POTTED_CHARRED_SAPLING);
		dropSelf(DesolationBlocks.STRIPPED_CHARRED_LOG);
		dropSelf(DesolationBlocks.STRIPPED_CHARRED_WOOD);
	}

	@Override
	public String getName() {
		return "Desolation Block Loot Tables";
	}
}
