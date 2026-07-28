package raltsmc.desolation.world.biome;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import raltsmc.desolation.tag.DesolationBiomeTags;

import java.util.function.Predicate;

/**
 * Spawns shared by every charred forest, applied through Fabric's biome modification API against the
 * {@link DesolationBiomeTags#CHARRED_FORESTS} tag rather than baked into each biome definition.
 * <p>
 * Going through the tag means a datapack (or another mod's biome) that opts into the tag inherits the
 * whole roster for free, and other mods can add their own mobs to the Charred Forest by selecting the
 * same tag. The variant-specific spawns — who actually characterises each of the three biomes — stay
 * in {@link BiomeCreator}.
 */
public final class DesolationBiomeModifications {
    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationBiomeModifications() {
        return;
    }

    public static void init() {
        Predicate<BiomeSelectionContext> charredForests = BiomeSelectors.tag(DesolationBiomeTags.CHARRED_FORESTS);

        // A burn is still the overworld at night: the usual undead wander in, just thinner on the
        // ground than in a living forest, since the Blackened have already claimed the territory.
        BiomeModifications.addSpawn(charredForests, MobCategory.MONSTER, EntityTypes.ZOMBIE, 20, 1, 3);
        BiomeModifications.addSpawn(charredForests, MobCategory.MONSTER, EntityTypes.SKELETON, 20, 1, 3);
        BiomeModifications.addSpawn(charredForests, MobCategory.MONSTER, EntityTypes.CREEPER, 20, 1, 2);
        BiomeModifications.addSpawn(charredForests, MobCategory.MONSTER, EntityTypes.ENDERMAN, 2, 1, 1);
        BiomeModifications.addSpawn(charredForests, MobCategory.MONSTER, EntityTypes.WITCH, 1, 1, 1);

        // Bats roost in the hollow trunks.
        BiomeModifications.addSpawn(charredForests, MobCategory.AMBIENT, EntityTypes.BAT, 6, 4, 6);
    }
}
