package raltsmc.desolation.world.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raltsmc.desolation.registry.DesolationEntities;
import raltsmc.desolation.registry.DesolationParticles;
import raltsmc.desolation.world.feature.DesolationPlacedFeatures;

import java.util.List;
import java.util.Optional;

public class BiomeCreator {
    /**
     * The three faces of a burn. They differ in far more than tree density: colours, weather,
     * ash and ember cover, ambience, music and who lives there.
     *
     * <ul>
     *   <li>{@link #DENSE} — the heart of the burn. Standing dead trees everywhere, choking smoke,
     *       almost no light, and the Blackened that hunt under their cover.</li>
     *   <li>{@link #CLEARING} — where the fire burned hottest and took everything. Open, bright,
     *       hot and dry: knee-deep ash, glowing coals underfoot, wind instead of creaking wood.</li>
     *   <li>{@link #SMALL} — the outer edge, already halfway back to an ordinary forest. Thin ash,
     *       barely any embers, and the first wildlife returning.</li>
     * </ul>
     */
    public enum Variant {
        DENSE,
        CLEARING,
        SMALL
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    private BiomeCreator() {
        return;
    }

    public static Biome createCharredForest(BootstrapContext<Biome> context, Variant variant) {
        Biome.BiomeBuilder builder = new Biome.BiomeBuilder()
                .generationSettings(createGenerationSettings(context, variant))
                .mobSpawnSettings(createSpawnSettings(variant))
                .hasPrecipitation(false);

        switch (variant) {
            case DENSE -> builder
                    .temperature(0.9F)
                    .downfall(0.1F)
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(0x515a63)
                            .grassColorOverride(0x2b2426)
                            .foliageColorOverride(0x3a3335)
                            .build())
                    .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x232930)
                    .setAttribute(EnvironmentAttributes.FOG_COLOR, 0x9d9a96)
                    .setAttribute(EnvironmentAttributes.SKY_COLOR, 0x8e969e)
                    .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES,
                            AmbientParticle.of(DesolationParticles.ASH_DRIFT, 0.13F))
                    .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                            Optional.of(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP),
                            Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)),
                            List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D))))
                    .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC,
                            new BackgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BASALT_DELTAS)));
            case CLEARING -> builder
                    .temperature(1.1F)
                    .downfall(0.0F)
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(0x6a7480)
                            .grassColorOverride(0x453b38)
                            .foliageColorOverride(0x554a47)
                            .build())
                    .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x333b44)
                    .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xcfc7bd)
                    .setAttribute(EnvironmentAttributes.SKY_COLOR, 0xb9c0c7)
                    // Open ground: less falling ash, but coals still spitting sparks into the air.
                    .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, List.of(
                            new AmbientParticle(DesolationParticles.ASH_DRIFT, 0.07F),
                            new AmbientParticle(DesolationParticles.EMBER_FLOAT, 0.05F)))
                    .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                            Optional.of(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP),
                            Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)),
                            List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D))))
                    .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC,
                            new BackgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY)));
            case SMALL -> builder
                    .temperature(0.8F)
                    .downfall(0.2F)
                    .specialEffects((new BiomeSpecialEffects.Builder())
                            .waterColor(0x5f6a75)
                            .grassColorOverride(0x38312f)
                            .foliageColorOverride(0x484042)
                            .build())
                    .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x2d343b)
                    .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xbdbcb8)
                    .setAttribute(EnvironmentAttributes.SKY_COLOR, 0xa8b1b9)
                    .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES,
                            AmbientParticle.of(DesolationParticles.ASH_DRIFT, 0.045F))
                    // No looping bed on the fringe — it should fade back into ordinary forest.
                    .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                            Optional.empty(),
                            Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 9000, 8, 2.0D)),
                            List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.005D))))
                    .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC,
                            new BackgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)));
        }

        return builder.build();
    }

    private static BiomeGenerationSettings createGenerationSettings(BootstrapContext<Biome> context, Variant variant) {
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, configuredCarvers);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);
        BiomeDefaultFeatures.addDefaultMonsterRoom(generationSettings);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(generationSettings);
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);
        BiomeDefaultFeatures.addDefaultSprings(generationSettings);
        generationSettings.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, DesolationPlacedFeatures.GIANT_BOULDER);

        switch (variant) {
            case DENSE -> {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_LARGE);
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_CHARRED_SAPLING);
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_FALLEN_LARGE);
            }
            case CLEARING -> generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_FALLEN_SMALL);
            case SMALL -> {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_SMALL);
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_FALLEN_SMALL);
            }
        }

        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_SCORCHED_TUFT);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_ASH_BRAMBLE);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PLANT_CINDERFRUIT);

        // Ash and embers are what actually sells "how badly did this burn", so each variant gets its
        // own density rather than the one shared pair of features.
        switch (variant) {
            case DENSE -> {
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_ASH_LAYER);
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_EMBER_CHUNK);
            }
            case CLEARING -> {
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_ASH_LAYER_DEEP);
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_EMBER_CHUNK_DEEP);
            }
            case SMALL -> {
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_ASH_LAYER_SPARSE);
                generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_EMBER_CHUNK_SPARSE);
            }
        }

        return generationSettings.build();
    }

    /**
     * Variant-specific spawns only. The roster shared by every charred forest (and by any biome a
     * datapack adds to {@code desolation:charred_forests}) is applied from
     * {@link DesolationBiomeModifications} so other mods can hook the same tag.
     */
    private static MobSpawnSettings createSpawnSettings(Variant variant) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();

        switch (variant) {
            case DENSE -> {
                // Nothing grazes in here. The Blackened own the place and hunt in packs.
                spawnSettings.creatureGenerationProbability(0.02F);
                spawnSettings.addSpawn(MobCategory.AMBIENT, 1, new MobSpawnSettings.SpawnerData(DesolationEntities.ASH_SCUTTLER, 1, 2));
                spawnSettings.addSpawn(MobCategory.MONSTER, 6, new MobSpawnSettings.SpawnerData(DesolationEntities.BLACKENED, 2, 4));
                spawnSettings.addSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityTypes.SPIDER, 1, 2));
            }
            case CLEARING -> {
                // Open, hot and exposed: scuttlers thrive in the deep ash, the Blackened avoid it.
                spawnSettings.creatureGenerationProbability(0.01F);
                spawnSettings.addSpawn(MobCategory.AMBIENT, 8, new MobSpawnSettings.SpawnerData(DesolationEntities.ASH_SCUTTLER, 2, 4));
                spawnSettings.addSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(DesolationEntities.BLACKENED, 1, 2));
                spawnSettings.addSpawn(MobCategory.MONSTER, 6, new MobSpawnSettings.SpawnerData(EntityTypes.MAGMA_CUBE, 1, 2));
            }
            case SMALL -> {
                // The fringe: life is creeping back in from the surrounding forest.
                spawnSettings.creatureGenerationProbability(0.07F);
                spawnSettings.addSpawn(MobCategory.AMBIENT, 4, new MobSpawnSettings.SpawnerData(DesolationEntities.ASH_SCUTTLER, 1, 2));
                spawnSettings.addSpawn(MobCategory.MONSTER, 3, new MobSpawnSettings.SpawnerData(DesolationEntities.BLACKENED, 1, 2));
                spawnSettings.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityTypes.RABBIT, 2, 3));
                spawnSettings.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityTypes.FOX, 1, 2));
            }
        }

        return spawnSettings.build();
    }
}
