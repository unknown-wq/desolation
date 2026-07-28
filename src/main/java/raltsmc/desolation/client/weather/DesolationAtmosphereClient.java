package raltsmc.desolation.client.weather;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import raltsmc.desolation.client.particle.DesolationParticleFactories;

/**
 * Client entrypoint for the atmosphere layer: the mod's own ash and ember particles, and the heat
 * shimmer over ember beds. Kept separate from the main client initializer so the atmosphere can be
 * added to or removed as one piece.
 */
@Environment(EnvType.CLIENT)
public class DesolationAtmosphereClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DesolationParticleFactories.register();
        HeatHazeEffect.register();
    }
}
