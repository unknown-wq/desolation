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

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationParticles() {
        return;
    }

    public static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), type);
    }

    public static void init() { }
}
