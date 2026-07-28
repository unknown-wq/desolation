package raltsmc.desolation.mixin.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.Desolation;

import java.util.Optional;

/**
 * Silences biome ambient loop sounds inside Desolation biomes when the {@code biomeSoundAmbience}
 * config option is disabled.
 *
 * <p>The 26.1 biome ambient sound system was rewritten: {@code BiomeAmbientSoundsHandler#tick}
 * now resolves loop sounds through the environment-attribute system rather than reading the biome
 * directly, and the old per-loop {@code method_25459} hook no longer exists. We therefore intercept
 * at {@code tick} HEAD, resolve the biome at the player's position, and if it belongs to Desolation
 * (and ambience is disabled) we stop any active loops and cancel the tick so no new loop is started.
 */
@Environment(EnvType.CLIENT)
@Mixin(BiomeAmbientSoundsHandler.class)
public class BiomeEffectSoundPlayerMixin {
    @Shadow
    @Final
    private LocalPlayer player;
    @Shadow
    @Final
    private SoundManager soundManager;
    @Shadow
    @Final
    private Object2ObjectArrayMap<Holder<SoundEvent>, BiomeAmbientSoundsHandler.LoopSoundInstance> loopSounds;
    @Shadow
    private Holder<SoundEvent> previousLoopSound;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void desolation$stopSound(CallbackInfo ci) {
        // This runs every client tick, so bail out on the config flag before paying for a biome
        // lookup: with ambience left enabled (the default) the injection costs one field read.
        if (Desolation.CONFIG.biomeSoundAmbience || !desolation$inDesolationBiome()) {
            return;
        }

        if (!loopSounds.isEmpty()) {
            loopSounds.values().forEach(soundManager::stop);
            loopSounds.clear();
        }
        previousLoopSound = null;
        ci.cancel();
    }

    @Unique
    private boolean desolation$inDesolationBiome() {
        Optional<ResourceKey<Biome>> biomeKey = player.level().getBiome(player.blockPosition()).unwrapKey();

        return biomeKey.isPresent() && Desolation.MOD_ID.equals(biomeKey.get().identifier().getNamespace());
    }
}
