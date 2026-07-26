package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import raltsmc.desolation.item.AshItem;
import raltsmc.desolation.item.CinderHeartItem;

public final class DesolationItems {
    public static BlockItem CHARRED_SOIL;
    public static BlockItem COOLED_EMBER_BLOCK;
    public static BlockItem EMBER_BLOCK;
    public static BlockItem ASH_BLOCK;
    public static BlockItem ASH_LAYER_BLOCK;
    public static BlockItem ACTIVATED_CHARCOAL_BLOCK;
    public static BlockItem SCORCHED_TUFT;
    public static BlockItem ASH_BRAMBLE;
    public static BlockItem CHARRED_BRANCHES;
    public static BlockItem CHARRED_LOG;
    public static BlockItem CHARRED_WOOD;
    public static BlockItem STRIPPED_CHARRED_LOG;
    public static BlockItem STRIPPED_CHARRED_WOOD;
    public static BlockItem CHARRED_PLANKS;
    public static BlockItem CHARRED_SAPLING;

    public static BlockItem CHARRED_SLAB;
    public static BlockItem CHARRED_STAIRS;
    public static BlockItem CHARRED_PRESSURE_PLATE;
    public static BlockItem CHARRED_TRAPDOOR;
    public static BlockItem CHARRED_FENCE;
    public static BlockItem CHARRED_FENCE_GATE;
    public static BlockItem CHARRED_BUTTON;
    public static BlockItem CHARRED_DOOR;

    public static SignItem CHARRED_SIGN;
    public static HangingSignItem CHARRED_HANGING_SIGN;

    public static Item CHARCOAL_BIT;
    public static Item ASH_PILE;
    //public static final Item GLASS_SHARD;
    public static Item PRIMED_ASH;
    public static Item ACTIVATED_CHARCOAL;
    public static Item AIR_FILTER;
    public static Item CINDERFRUIT;
    public static Item CINDERFRUIT_SEEDS;
    public static Item INFUSED_POWDER;
    public static Item HEART_OF_CINDER;

    public static Item MUSIC_DISC_ASHES;

    public static Item SPAWN_EGG_ASH_SCUTTLER;
    public static Item SPAWN_EGG_BLACKENED;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationItems() {
        return;
    }

    static void init() {
        CHARRED_SOIL = DesolationRegistries.registerBlockItem("charred_soil", DesolationBlocks.CHARRED_SOIL);
        COOLED_EMBER_BLOCK = DesolationRegistries.registerBlockItem("cooled_ember_block", DesolationBlocks.COOLED_EMBER_BLOCK);
        EMBER_BLOCK = DesolationRegistries.registerBlockItem("ember_block", DesolationBlocks.EMBER_BLOCK);
        ASH_BLOCK = DesolationRegistries.registerBlockItem("ash_block", DesolationBlocks.ASH_BLOCK);
        ASH_LAYER_BLOCK = DesolationRegistries.registerBlockItem("ash", DesolationBlocks.ASH_LAYER_BLOCK);
        ACTIVATED_CHARCOAL_BLOCK = DesolationRegistries.registerBlockItem("activated_charcoal_block", DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK);
        SCORCHED_TUFT = DesolationRegistries.registerBlockItem("scorched_tuft", DesolationBlocks.SCORCHED_TUFT);
        ASH_BRAMBLE = DesolationRegistries.registerBlockItem("ash_bramble", DesolationBlocks.ASH_BRAMBLE);
        CHARRED_BRANCHES = DesolationRegistries.registerBlockItem("charred_branches", DesolationBlocks.CHARRED_BRANCHES);
        CHARRED_LOG = DesolationRegistries.registerBlockItem("charred_log", DesolationBlocks.CHARRED_LOG);
        CHARRED_WOOD = DesolationRegistries.registerBlockItem("charred_wood", DesolationBlocks.CHARRED_WOOD);
        STRIPPED_CHARRED_LOG = DesolationRegistries.registerBlockItem("stripped_charred_log", DesolationBlocks.STRIPPED_CHARRED_LOG);
        STRIPPED_CHARRED_WOOD = DesolationRegistries.registerBlockItem("stripped_charred_wood", DesolationBlocks.STRIPPED_CHARRED_WOOD);
        CHARRED_PLANKS = DesolationRegistries.registerBlockItem("charred_planks", DesolationBlocks.CHARRED_PLANKS);
        CHARRED_SAPLING = DesolationRegistries.registerBlockItem("charred_sapling", DesolationBlocks.CHARRED_SAPLING);

        CHARRED_SLAB = DesolationRegistries.registerBlockItem("charred_slab", DesolationBlocks.CHARRED_SLAB);
        CHARRED_STAIRS = DesolationRegistries.registerBlockItem("charred_stairs", DesolationBlocks.CHARRED_STAIRS);
        CHARRED_PRESSURE_PLATE = DesolationRegistries.registerBlockItem("charred_pressure_plate", DesolationBlocks.CHARRED_PRESSURE_PLATE);
        CHARRED_TRAPDOOR = DesolationRegistries.registerBlockItem("charred_trapdoor", DesolationBlocks.CHARRED_TRAPDOOR);
        CHARRED_FENCE = DesolationRegistries.registerBlockItem("charred_fence", DesolationBlocks.CHARRED_FENCE);
        CHARRED_FENCE_GATE = DesolationRegistries.registerBlockItem("charred_fence_gate", DesolationBlocks.CHARRED_FENCE_GATE);
        CHARRED_BUTTON = DesolationRegistries.registerBlockItem("charred_button", DesolationBlocks.CHARRED_BUTTON);
        CHARRED_DOOR = DesolationRegistries.registerBlockItem("charred_door", DesolationBlocks.CHARRED_DOOR);

        CHARRED_SIGN = DesolationRegistries.register("charred_sign", properties -> new SignItem(DesolationBlocks.CHARRED_SIGN, DesolationBlocks.CHARRED_WALL_SIGN, properties), new Item.Properties().stacksTo(16).useBlockDescriptionPrefix());
        CHARRED_HANGING_SIGN = DesolationRegistries.register("charred_hanging_sign", properties -> new HangingSignItem(DesolationBlocks.CHARRED_HANGING_SIGN, DesolationBlocks.CHARRED_WALL_HANGING_SIGN, properties), new Item.Properties().stacksTo(16).useBlockDescriptionPrefix());


        CHARCOAL_BIT = DesolationRegistries.register("charcoal_bit", Item::new, new Item.Properties());
        ASH_PILE = DesolationRegistries.register("ash_pile", AshItem::new, new Item.Properties());
        //GLASS_SHARD = DesolationRegistries.register("glass_shard", Item::new, new Item.Properties());
        PRIMED_ASH = DesolationRegistries.register("primed_ash", Item::new, new Item.Properties());
        ACTIVATED_CHARCOAL = DesolationRegistries.register("activated_charcoal", Item::new, new Item.Properties());
        AIR_FILTER = DesolationRegistries.register("air_filter", Item::new, new Item.Properties());
        CINDERFRUIT = DesolationRegistries.register("cinderfruit", Item::new, new Item.Properties().food(
                new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(0.3F)
                        .build(),
                Consumables.defaultFood()
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200), 1.0F))
                        .build()
        ));
        CINDERFRUIT_SEEDS = DesolationRegistries.register("cinderfruit_seeds", properties -> new BlockItem(DesolationBlocks.CINDERFRUIT_PLANT, properties.useItemDescriptionPrefix()), new Item.Properties());
        INFUSED_POWDER = DesolationRegistries.register("infused_powder", Item::new, new Item.Properties());
        HEART_OF_CINDER = DesolationRegistries.register("heart_of_cinder", CinderHeartItem::new, new Item.Properties().rarity(Rarity.RARE)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

        MUSIC_DISC_ASHES = DesolationRegistries.register("music_disc_ashes", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(DesolationJukeboxSongs.ASHES));
        SPAWN_EGG_ASH_SCUTTLER = DesolationRegistries.register("ash_scuttler_spawn_egg", SpawnEggItem::new,
                new Item.Properties().component(DataComponents.ENTITY_DATA, TypedEntityData.of(DesolationEntities.ASH_SCUTTLER, new CompoundTag())));
        SPAWN_EGG_BLACKENED = DesolationRegistries.register("blackened_spawn_egg", SpawnEggItem::new,
                new Item.Properties().component(DataComponents.ENTITY_DATA, TypedEntityData.of(DesolationEntities.BLACKENED, new CompoundTag())));


        addCompostables();
        addFuels();
    }

    private static void addCompostables() {
        CompostableRegistry compostingRegistry = CompostableRegistry.INSTANCE;
        float LEAVES_CHANCE = compostingRegistry.get(Items.OAK_LEAVES);
        float SAPLING_CHANCE = compostingRegistry.get(Items.OAK_SAPLING);

        compostingRegistry.add(CHARRED_BRANCHES, LEAVES_CHANCE);
        compostingRegistry.add(CHARRED_SAPLING, SAPLING_CHANCE);
    }

    private static void addFuels() {
        FuelValueEvents.BUILD.register((builder, context) -> {
            builder.add(DesolationItems.CHARCOAL_BIT, 400);
        });
    }
}
