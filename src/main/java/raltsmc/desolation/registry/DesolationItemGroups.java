package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import raltsmc.desolation.Desolation;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DesolationItemGroups {
    private static final HashMap<ResourceKey<CreativeModeTab>, HashMap<ItemLike, ItemGroupEntries>> ITEM_GROUP_ENTRY_MAPS;
    private static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "items"));

    /*
     * The vanilla creative tab keys are not publicly exposed in 26.1, so we reconstruct them here.
     */
    private static final ResourceKey<CreativeModeTab> BUILDING_BLOCKS = vanillaTab("building_blocks");
    private static final ResourceKey<CreativeModeTab> NATURAL = vanillaTab("natural_blocks");
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL = vanillaTab("functional_blocks");
    private static final ResourceKey<CreativeModeTab> TOOLS = vanillaTab("tools_and_utilities");
    private static final ResourceKey<CreativeModeTab> FOOD_AND_DRINK = vanillaTab("food_and_drinks");
    private static final ResourceKey<CreativeModeTab> INGREDIENTS = vanillaTab("ingredients");
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = vanillaTab("spawn_eggs");

    private static ResourceKey<CreativeModeTab> vanillaTab(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace(path));
    }

    /*
     * These items are the last Vanilla item of a "similar" type to items we add to Vanilla groups.
     * Each is used to build a collection of items which will be inserted below the Vanilla item.
     */
    private static final ItemLike BUILDING_WOOD_ITEMS = Items.MANGROVE_BUTTON;
    private static final ItemLike FOOD_BERRIES = Items.GLOW_BERRIES;
    private static final ItemLike FUNCTIONAL_SIGN = Items.CHERRY_HANGING_SIGN;
    private static final ItemLike INGREDIENTS_COAL = Items.CHARCOAL;
    private static final ItemLike NATURAL_DIRT_ITEMS = Items.FARMLAND;
    private static final ItemLike NATURAL_GRASS = Items.SHORT_GRASS;
    private static final ItemLike NATURAL_HOT_BLOCKS = Items.MAGMA_BLOCK;
    private static final ItemLike NATURAL_LEAVES = Items.MANGROVE_LEAVES;
    private static final ItemLike NATURAL_LOG = Items.MANGROVE_LOG;
    private static final ItemLike NATURAL_SAPLING = Items.MANGROVE_PROPAGULE;
    private static final ItemLike NATURAL_SEEDS = Items.BEETROOT_SEEDS;
    private static final ItemLike NATURAL_SHRUBS = Items.DEAD_BUSH;
    private static final ItemLike NATURAL_SNOWLIKE = Items.MOSS_CARPET;
    private static final ItemLike TOOLS_DISC = Items.MUSIC_DISC_PIGSTEP;
    private static final ItemLike TOOLS_WEARABLE = Items.ELYTRA;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationItemGroups() {
        return;
    }

    static {
        ITEM_GROUP_ENTRY_MAPS = new HashMap<>(8);

        /*
         * For each Vanilla item group, add the same kinds of items Vanilla adds.
         * Since Minecraft 1.19.3, items are often in multiple item groups...
         */

        // BUILDING BLOCKS

        // Wood items
        addGroupEntry(DesolationBlocks.CHARRED_LOG, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_WOOD, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.STRIPPED_CHARRED_LOG, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.STRIPPED_CHARRED_WOOD, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_PLANKS, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_STAIRS, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_SLAB, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_FENCE, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_FENCE_GATE, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationItems.CHARRED_DOOR, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_TRAPDOOR, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_PRESSURE_PLATE, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);
        addGroupEntry(DesolationBlocks.CHARRED_BUTTON, BUILDING_BLOCKS, BUILDING_WOOD_ITEMS);

        // Misc.
        addGroupEntry(DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK, BUILDING_BLOCKS, Items.COAL_BLOCK);


        // NATURAL

        // Dirt Items
        addGroupEntry(DesolationBlocks.CHARRED_SOIL, NATURAL, NATURAL_DIRT_ITEMS);

        // Hot blocks
        addGroupEntry(DesolationBlocks.EMBER_BLOCK, NATURAL, NATURAL_HOT_BLOCKS);
        addGroupEntry(DesolationBlocks.COOLED_EMBER_BLOCK, NATURAL, NATURAL_HOT_BLOCKS);

        // Snow-like
        addGroupEntry(DesolationBlocks.ASH_BLOCK, NATURAL, NATURAL_SNOWLIKE);
        addGroupEntry(DesolationBlocks.ASH_LAYER_BLOCK, NATURAL, NATURAL_SNOWLIKE);

        // Wood
        addGroupEntry(DesolationBlocks.CHARRED_LOG, NATURAL, NATURAL_LOG);

        // Saplings
        addGroupEntry(DesolationBlocks.CHARRED_SAPLING, NATURAL, NATURAL_SAPLING);

        // Leaves
        addGroupEntry(DesolationBlocks.CHARRED_BRANCHES, NATURAL, NATURAL_LEAVES);

        // Seeds
        addGroupEntry(DesolationItems.CINDERFRUIT_SEEDS, NATURAL, NATURAL_SEEDS);

        // Grass
        addGroupEntry(DesolationBlocks.SCORCHED_TUFT, NATURAL, NATURAL_GRASS);

        // Berries
        addGroupEntry(DesolationBlocks.ASH_BRAMBLE, NATURAL, NATURAL_SHRUBS);


        // FUNCTIONAL

        // Wood Items
        addGroupEntry(DesolationItems.CHARRED_SIGN, FUNCTIONAL, FUNCTIONAL_SIGN);
        addGroupEntry(DesolationItems.CHARRED_HANGING_SIGN, FUNCTIONAL, FUNCTIONAL_SIGN);


        // TOOLS

        // Wearable
        addGroupEntry(DesolationItems.AIR_FILTER, TOOLS, TOOLS_WEARABLE);

        // Discs
        addGroupEntry(DesolationItems.MUSIC_DISC_ASHES, TOOLS, TOOLS_DISC);


        // FOOD AND DRINK

        // Berries
        addGroupEntry(DesolationItems.CINDERFRUIT, FOOD_AND_DRINK, FOOD_BERRIES);


        // INGREDIENTS

        // Charcoal and Ash
        addGroupEntry(DesolationItems.ACTIVATED_CHARCOAL, INGREDIENTS, INGREDIENTS_COAL);
        addGroupEntry(DesolationItems.CHARCOAL_BIT, INGREDIENTS, INGREDIENTS_COAL);
        addGroupEntry(DesolationItems.ASH_PILE, INGREDIENTS, INGREDIENTS_COAL);
        addGroupEntry(DesolationItems.PRIMED_ASH, INGREDIENTS, INGREDIENTS_COAL);

        // Misc.
        addGroupEntry(DesolationItems.INFUSED_POWDER, INGREDIENTS, Items.GUNPOWDER);
        addGroupEntry(DesolationItems.HEART_OF_CINDER, INGREDIENTS, Items.HEART_OF_THE_SEA);


        // SPAWN EGGS
        addGroupEntry(DesolationItems.SPAWN_EGG_ASH_SCUTTLER, SPAWN_EGGS, Items.ALLAY_SPAWN_EGG);
        addGroupEntry(DesolationItems.SPAWN_EGG_BLACKENED, SPAWN_EGGS, Items.BEE_SPAWN_EGG);


        /*
         * Add the items configured above to the Vanilla item groups.
         */
        for (ResourceKey<CreativeModeTab> group : ITEM_GROUP_ENTRY_MAPS.keySet()) {
            CreativeModeTabEvents.modifyOutputEvent(group).register((output) -> {
                FeatureFlagSet featureSet = output.getEnabledFeatures();
                HashMap<ItemLike, ItemGroupEntries> entryMap = ITEM_GROUP_ENTRY_MAPS.get(group);

                for (ItemLike relative : entryMap.keySet()) {
                    ItemGroupEntries entries = entryMap.get(relative);

                    // FAPI does not give us a way to add at a feature-flag-disabled location.
                    // So, below we have to adjust for any items which may be disabled.
                    if (relative == null) {
                        // Target the end of the Item Group
                        output.acceptAll(entries.getCollection());
                    } else if (relative.asItem().equals(Items.MANGROVE_HANGING_SIGN) && !Items.MANGROVE_HANGING_SIGN.isEnabled(featureSet)) {
                        output.insertAfter(Items.MANGROVE_SIGN, entries.getCollection());
                    } else {
                        output.insertAfter(relative, entries.getCollection());
                    }
                }
            });
        }


        /*
         * Also add all the items to Desolation's own item group.
         */
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricCreativeModeTab.builder()
                .title(Component.literal("Desolation"))
                .icon(() -> DesolationBlocks.EMBER_BLOCK.asItem().getDefaultInstance())
                .displayItems((params, output) -> {
                    ITEM_GROUP_ENTRY_MAPS.values().stream()
                            .map(HashMap::values).flatMap(Collection::stream)
                            .map(ItemGroupEntries::getCollection).flatMap(Collection::stream)
                            .collect(Collectors.groupingByConcurrent(ItemStack::getItem)).keySet().stream()
                            .sorted(Comparator.comparing((item) -> item.getDefaultInstance().getHoverName().getString())).forEach(output::accept);
                }).build()
        );
    }

    public static void addGroupEntry(ItemLike item, ResourceKey<CreativeModeTab> group) {
        // Appends the item to the bottom of the group.
        addGroupEntry(item, group, null);
    }

    public static void addGroupEntry(ItemLike item, ResourceKey<CreativeModeTab> group, @Nullable ItemLike relative) {
        HashMap<ItemLike, ItemGroupEntries> entryMap = ITEM_GROUP_ENTRY_MAPS.computeIfAbsent(group, (key) -> new HashMap<>(32));
        ItemGroupEntries entries = entryMap.computeIfAbsent(relative, ItemGroupEntries::empty);
        entries.addItem(item);
    }

    public static void init() { }
}
