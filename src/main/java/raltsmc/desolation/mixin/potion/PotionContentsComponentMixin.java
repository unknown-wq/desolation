package raltsmc.desolation.mixin.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.registry.DesolationStatusEffects;

import java.util.function.Consumer;

@Mixin(PotionContents.class)
public abstract class PotionContentsComponentMixin {
    @Inject(method = "addPotionTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V",
            at = @At("TAIL")
    )
    private static void desolation$addTooltip(Iterable<MobEffectInstance> effects, Consumer<Component> textConsumer, float durationMultiplier, float tickRate, CallbackInfo ci) {
        for (MobEffectInstance effect : effects) {
            if (DesolationStatusEffects.CINDER_SOUL.equals(effect.getEffect().value())) {
                textConsumer.accept(Component.translatable("potion.cinder_soul.tooltip_a").withStyle(ChatFormatting.GOLD));
                textConsumer.accept(Component.translatable("potion.cinder_soul.tooltip_b").withStyle(ChatFormatting.GOLD));

                break;
            }
        }
    }
}
