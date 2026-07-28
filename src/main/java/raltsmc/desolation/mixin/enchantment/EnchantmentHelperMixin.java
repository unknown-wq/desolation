package raltsmc.desolation.mixin.enchantment;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.registry.DesolationStatusEffects;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "doPostAttackEffectsWithItemSource(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL")
    )
    private static void desolation$cinderSoulAttackIgnite(ServerLevel world, Entity target, DamageSource damageSource, ItemStack weapon, CallbackInfo ci) {
        // This runs for every attack in the world, so use the cached registration holder instead
        // of resolving the effect through the registry map on each hit.
        if (target instanceof LivingEntity && damageSource.getEntity() instanceof Player attacker) {
            if (attacker.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
                target.igniteForSeconds(6.0f);
            }
        }
    }
}
