package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import raltsmc.desolation.Desolation;

public class DesolationParticles {
    public static final ParticleType<SimpleParticleType> SPARK = register("spark", FabricParticleTypes.simple());

    /**
     * Slow, tumbling ash flake. Replaces the vanilla {@code WHITE_ASH} the biomes and the ash rain
     * used to borrow, so colour, drift speed and lifetime are ours to tune.
     */
    public static final SimpleParticleType ASH_DRIFT = register("ash_drift", FabricParticleTypes.simple());

    /** Ember lifted off hot ground: rises, glows, cools to nothing. */
    public static final SimpleParticleType EMBER_FLOAT = register("ember_float", FabricParticleTypes.simple());

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationParticles() {
        return;
    }

    public static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), type);
    }

    public static void init() { }
}
