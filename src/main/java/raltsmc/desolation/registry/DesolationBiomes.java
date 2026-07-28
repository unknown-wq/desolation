package raltsmc.desolation.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.world.biome.BiomeCreator;

public class DesolationBiomes {
    public static final ResourceKey<Biome> CHARRED_FOREST = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred_forest"));
    public static final ResourceKey<Biome> CHARRED_FOREST_CLEARING = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred_forest_clearing"));
    public static final ResourceKey<Biome> CHARRED_FOREST_SMALL = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred_forest_small"));

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationBiomes() {
        return;
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(CHARRED_FOREST, BiomeCreator.createCharredForest(context, BiomeCreator.Variant.DENSE));
        context.register(CHARRED_FOREST_CLEARING, BiomeCreator.createCharredForest(context, BiomeCreator.Variant.CLEARING));
        context.register(CHARRED_FOREST_SMALL, BiomeCreator.createCharredForest(context, BiomeCreator.Variant.SMALL));
    }
}
