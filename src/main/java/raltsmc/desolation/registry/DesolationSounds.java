package raltsmc.desolation.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import raltsmc.desolation.Desolation;

@SuppressWarnings("SameParameterValue")
public class DesolationSounds {
    public static final Holder.Reference<SoundEvent> MUSIC_DISC_ASHES_SOUND = registerReference("music_disc.ashes");
    public static final SoundEvent EMBER_BLOCK_POP_1 = register("block.ember_pop1");
    public static final SoundEvent EMBER_BLOCK_POP_2 = register("block.ember_pop2");
    public static final SoundEvent EMBER_BLOCK_POP_3 = register("block.ember_pop3");
    public static final SoundEvent EMBER_BLOCK_POP_4 = register("block.ember_pop4");

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationSounds() {
        return;
    }

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);

        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    private static Holder.Reference<SoundEvent> registerReference(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);

        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    public static void init() { }
}
