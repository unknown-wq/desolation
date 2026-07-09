package raltsmc.desolation.mixin.entity.player;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.Desolation;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void desolation$tickPlayerEntity(CallbackInfo info) {
        Level world = this.level();

        if (!world.isClientSide()) {
            Optional<ResourceKey<Biome>> biomeKey = world.getBiome(this.blockPosition()).unwrapKey();

            if (this.getY() >= world.getSeaLevel() - 10
                    && biomeKey.isPresent()
                    && Desolation.MOD_ID.equals(biomeKey.get().identifier().getNamespace())) {
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 308));
                this.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 308));
            }
        }
    }
}
