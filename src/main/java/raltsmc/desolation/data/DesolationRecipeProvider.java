package raltsmc.desolation.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationItems;
import raltsmc.desolation.tag.DesolationItemTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DesolationRecipeProvider extends FabricRecipeProvider {
	public DesolationRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
		return new RecipeProvider(registryLookup, exporter) {
			@Override
			public void buildRecipes() {
				// vanilla recipes
				twoByTwoPacker(RecipeCategory.MISC, Items.CHARCOAL, DesolationItems.CHARCOAL_BIT);

				shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 1)
						.requires(Items.BONE_MEAL)
						.requires(Items.CHARCOAL)
						.requires(Items.CLAY_BALL)
						.unlockedBy("has_charcoal", has(Items.CHARCOAL))
						.save(exporter);


				// misc. recipes
				nineBlockStorageRecipes(RecipeCategory.MISC, DesolationItems.ACTIVATED_CHARCOAL, RecipeCategory.BUILDING_BLOCKS, DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK);

				oreSmelting(List.of(Items.CHARCOAL), RecipeCategory.MISC, CookingBookCategory.MISC, DesolationItems.ACTIVATED_CHARCOAL, 3.0f, 800, "charcoal");

				shaped(RecipeCategory.TOOLS, DesolationItems.AIR_FILTER, 1)
						.pattern("CCC")
						.pattern("CPC")
						.pattern("CCC")
						.define('C', DesolationItems.ACTIVATED_CHARCOAL)
						.define('P', Items.PAPER)
						.unlockedBy("has_activated_charcoal", has(DesolationItems.ACTIVATED_CHARCOAL))
						.save(exporter);

				nineBlockStorageRecipes(RecipeCategory.MISC, DesolationItems.ASH_PILE, RecipeCategory.BUILDING_BLOCKS, DesolationBlocks.ASH_BLOCK);

				shapeless(RecipeCategory.MISC, DesolationItems.CHARCOAL_BIT, 4)
						.requires(Items.CHARCOAL)
						.group("charcoal")
						.unlockedBy("has_charcoal", has(Items.CHARCOAL))
						.save(exporter);

				shaped(RecipeCategory.MISC, DesolationItems.INFUSED_POWDER, 1)
						.pattern("CCC")
						.pattern("CFC")
						.pattern("CCC")
						.define('C', DesolationItems.ACTIVATED_CHARCOAL)
						.define('F', DesolationItems.CINDERFRUIT)
						.unlockedBy("has_cinderfruit", has(DesolationItems.CINDERFRUIT))
						.save(exporter);

				shaped(RecipeCategory.MISC, DesolationItems.PRIMED_ASH, 1)
						.pattern("CCC")
						.pattern("CAC")
						.pattern("CCC")
						.define('A', DesolationItems.ASH_PILE)
						.define('C', DesolationItems.ACTIVATED_CHARCOAL)
						.unlockedBy("has_ash_pile", has(DesolationItems.ASH_PILE))
						.save(exporter);


				// wood recipes
				shapeless(RecipeCategory.REDSTONE, DesolationBlocks.CHARRED_BUTTON, 1)
						.requires(DesolationBlocks.CHARRED_PLANKS)
						.group("wooden_button")
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				doorBuilder(DesolationBlocks.CHARRED_DOOR, Ingredient.of(DesolationBlocks.CHARRED_PLANKS))
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				shaped(RecipeCategory.DECORATIONS, DesolationBlocks.CHARRED_FENCE, 3)
						.pattern("W#W")
						.pattern("W#W")
						.define('#', Items.STICK)
						.define('W', DesolationBlocks.CHARRED_PLANKS)
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				shaped(RecipeCategory.REDSTONE, DesolationBlocks.CHARRED_FENCE_GATE, 1)
						.pattern("#W#")
						.pattern("#W#")
						.define('#', Items.STICK)
						.define('W', DesolationBlocks.CHARRED_PLANKS)
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				hangingSignBuilder(DesolationBlocks.CHARRED_HANGING_SIGN, Ingredient.of(DesolationBlocks.STRIPPED_CHARRED_LOG))
						.group("hanging_sign")
						.unlockedBy("has_stripped_logs", has(DesolationBlocks.STRIPPED_CHARRED_LOG))
						.save(exporter);

				planksFromLogs(DesolationBlocks.CHARRED_PLANKS, DesolationItemTags.CHARRED_LOGS, 4);

				pressurePlate(DesolationBlocks.CHARRED_PRESSURE_PLATE, DesolationBlocks.CHARRED_PLANKS);

				shaped(RecipeCategory.DECORATIONS, DesolationBlocks.CHARRED_SIGN, 3)
						.group("sign")
						.pattern("###")
						.pattern("###")
						.pattern(" X ")
						.define('#', DesolationBlocks.CHARRED_PLANKS)
						.define('X', Items.STICK)
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				slab(RecipeCategory.BUILDING_BLOCKS, DesolationBlocks.CHARRED_SLAB, DesolationBlocks.CHARRED_PLANKS);

				stairBuilder(DesolationBlocks.CHARRED_STAIRS, Ingredient.of(DesolationBlocks.CHARRED_PLANKS))
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				trapdoorBuilder(DesolationBlocks.CHARRED_TRAPDOOR, Ingredient.of(DesolationBlocks.CHARRED_PLANKS))
						.unlockedBy("has_planks", has(DesolationBlocks.CHARRED_PLANKS))
						.save(exporter);

				shaped(RecipeCategory.BUILDING_BLOCKS, DesolationBlocks.CHARRED_WOOD, 3)
						.pattern("LL")
						.pattern("LL")
						.define('L', DesolationBlocks.CHARRED_LOG)
						.unlockedBy("has_logs", has(DesolationBlocks.CHARRED_LOG))
						.save(exporter);

				shaped(RecipeCategory.BUILDING_BLOCKS, DesolationBlocks.STRIPPED_CHARRED_WOOD, 3)
						.pattern("LL")
						.pattern("LL")
						.define('L', DesolationBlocks.STRIPPED_CHARRED_LOG)
						.unlockedBy("has_logs", has(DesolationBlocks.STRIPPED_CHARRED_LOG))
						.save(exporter);
			}
		};
	}

	@Override
	public String getName() {
		return "Desolation Recipes";
	}

	@Override
	protected Identifier getRecipeIdentifier(Identifier identifier) {
		return Identifier.fromNamespaceAndPath(Desolation.MOD_ID, identifier.getPath());
	}
}
