package raltsmc.desolation.mixin.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import raltsmc.desolation.registry.DesolationStatusEffects;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    @Final
    private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void desolation$negateDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source.is(DamageTypeTags.IS_FIRE) && this.activeEffects.containsKey(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(DesolationStatusEffects.CINDER_SOUL))) {
            info.setReturnValue(false);
        }
    }
}
