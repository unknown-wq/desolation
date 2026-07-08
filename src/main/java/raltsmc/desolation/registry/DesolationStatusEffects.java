package raltsmc.desolation.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.entity.effect.CinderSoulStatusEffect;

@SuppressWarnings("SameParameterValue")
public class DesolationStatusEffects {
    public static final MobEffect CINDER_SOUL = register("cinder_soul", new CinderSoulStatusEffect());

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationStatusEffects() {
        return;
    }

    private static MobEffect register(String name, MobEffect entry) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), entry);
    }

    public static void init() { }
}
