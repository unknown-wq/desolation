package raltsmc.desolation.mixin.entity;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import raltsmc.desolation.registry.DesolationStatusEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void desolation$negateDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        // This runs for every damage instance in the world, so test the cheap tag first and use
        // the cached registration holder instead of looking the effect up in the registry map.
        if (source.is(DamageTypeTags.IS_FIRE) && this.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
            info.setReturnValue(false);
        }
    }
}
