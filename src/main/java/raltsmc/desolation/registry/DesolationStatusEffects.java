package raltsmc.desolation.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.entity.effect.CinderSoulStatusEffect;

@SuppressWarnings("SameParameterValue")
public class DesolationStatusEffects {
    // Effect lookups on hot paths (every hurt, every attack) want a holder rather than a
    // BuiltInRegistries.MOB_EFFECT.wrapAsHolder() map lookup, so keep the registration holder.
    public static final Holder.Reference<MobEffect> CINDER_SOUL_HOLDER = registerReference("cinder_soul", new CinderSoulStatusEffect());
    public static final MobEffect CINDER_SOUL = CINDER_SOUL_HOLDER.value();

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationStatusEffects() {
        return;
    }

    private static Holder.Reference<MobEffect> registerReference(String name, MobEffect entry) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), entry);
    }

    public static void init() { }
}
