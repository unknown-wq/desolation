package raltsmc.desolation.world.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raltsmc.desolation.registry.DesolationEntities;
import raltsmc.desolation.world.feature.DesolationPlacedFeatures;

import java.util.List;
import java.util.Optional;

public class BiomeCreator {
    @SuppressWarnings("UnnecessaryReturnStatement")
    private BiomeCreator() {
        return;
    }

    public static Biome createCharredForest(BootstrapContext<Biome> context, boolean isClearing, boolean isSmall) {
        return new Biome.BiomeBuilder()
                .generationSettings(createGenerationSettings(context, isClearing, isSmall))
                .mobSpawnSettings(createSpawnSettings())
                .hasPrecipitation(false)
                .temperature(0.9F)
                .downfall(0.1F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x5b646e)
                        .grassColorOverride(0x342d2f)
                        .foliageColorOverride(0x443d3f)
                        .build())
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x2a3036)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xb5b5b5)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0xa1aab3)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.WHITE_ASH, 0.118093334F))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                        Optional.of(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP),
                        Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)),
                        List.of()))
                .build();
    }

    private static BiomeGenerationSettings createGenerationSettings(BootstrapContext<Biome> context, boolean isClearing, boolean isSmall) {
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
        if (isSmall) {
            if (!isClearing) {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_SMALL);
            }
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_FALLEN_SMALL);
        } else {
            if (!isClearing) {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_LARGE);
            }
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_CHARRED_SAPLING);
            generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.TREES_CHARRED_FALLEN_LARGE);
        }
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_SCORCHED_TUFT);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PATCH_ASH_BRAMBLE);
        generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, DesolationPlacedFeatures.PLANT_CINDERFRUIT);
        generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_ASH_LAYER);
        generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, DesolationPlacedFeatures.PATCH_EMBER_CHUNK);

        return generationSettings.build();
    }

    private static MobSpawnSettings createSpawnSettings() {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();

        spawnSettings.addSpawn(MobCategory.AMBIENT, 1, new MobSpawnSettings.SpawnerData(DesolationEntities.ASH_SCUTTLER, 1, 2));
        // Feral Blackened: higher spawn weight and larger max group make them noticeably more common in the Charred Forest.
        spawnSettings.addSpawn(MobCategory.MONSTER, 4, new MobSpawnSettings.SpawnerData(DesolationEntities.BLACKENED, 1, 4));

        return spawnSettings.build();
    }
}
