package raltsmc.desolation.registry;

import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import raltsmc.desolation.Desolation;

@SuppressWarnings("SameParameterValue")
public class DesolationJukeboxSongs {
    public static final ResourceKey<JukeboxSong> ASHES = of("ashes");

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationJukeboxSongs() {
        return;
    }

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, ASHES, DesolationSounds.MUSIC_DISC_ASHES_SOUND, 93, 14);
    }

    private static ResourceKey<JukeboxSong> of(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name));
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(key, new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.identifier())), lengthInSeconds, comparatorOutput));
    }
}
