package raltsmc.desolation.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import raltsmc.desolation.registry.DesolationParticles;

/** Client-side factories for the mod's atmosphere particles. */
@Environment(EnvType.CLIENT)
public final class DesolationParticleFactories {
    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationParticleFactories() {
        return;
    }

    public static void register() {
        ParticleProviderRegistry.getInstance().register(DesolationParticles.ASH_DRIFT, AshDriftParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(DesolationParticles.EMBER_FLOAT, EmberFloatParticle.Factory::new);
    }
}
