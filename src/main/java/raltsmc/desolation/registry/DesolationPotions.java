package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import raltsmc.desolation.Desolation;

public class DesolationPotions {
    public static final Potion CINDER_SOUL = register("cinder_soul", new Potion("cinder_soul", new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(DesolationStatusEffects.CINDER_SOUL), 1200)));
    public static final Potion LONG_CINDER_SOUL = register("long_cinder_soul", new Potion("cinder_soul", new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(DesolationStatusEffects.CINDER_SOUL), 3600)));
    public static final Potion BLINDNESS = register("blindness", new Potion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 1200)));
    public static final Potion LONG_BLINDNESS = register("long_blindness", new Potion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 3600)));

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationPotions() {
        return;
    }

    private static Potion register(String id, Potion potion) {
        return Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, id), potion);
    }

    public static void init() {
        FabricPotionBrewingBuilder.BUILD.register((builder) -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(DesolationItems.INFUSED_POWDER), Potions.FIRE_RESISTANCE);
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(DesolationItems.HEART_OF_CINDER), BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.CINDER_SOUL));
            builder.registerPotionRecipe(BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.CINDER_SOUL), Ingredient.of(Items.REDSTONE), BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.LONG_CINDER_SOUL));
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(DesolationItems.PRIMED_ASH), BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.BLINDNESS));
            builder.registerPotionRecipe(BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.BLINDNESS), Ingredient.of(Items.REDSTONE), BuiltInRegistries.POTION.wrapAsHolder(DesolationPotions.LONG_BLINDNESS));
        });
    }
}
