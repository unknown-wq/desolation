package raltsmc.desolation.world.feature;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import raltsmc.desolation.Desolation;

public class DesolationFeatures {
    public static Feature<BlockStateConfiguration> GIANT_BOULDER = new GiantBoulderFeature(BlockStateConfiguration.CODEC);
    public static Feature<ScatteredFeatureConfig> SCATTERED = new ScatteredFeature(ScatteredFeatureConfig.CODEC);

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationFeatures() {
        return;
    }

    public static void init() {
        register("giant_boulder", GIANT_BOULDER);
        register("scattered", SCATTERED);
    }

    private static void register(String name, Feature<?> feature) {
        Registry.register(BuiltInRegistries.FEATURE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, name), feature);
    }
}
