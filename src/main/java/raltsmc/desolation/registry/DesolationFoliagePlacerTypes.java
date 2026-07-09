package raltsmc.desolation.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.mixin.worldgen.PlacerTypeInvokers.FoliagePlacerTypeInvoker;
import raltsmc.desolation.world.gen.foliage.CharredFoliagePlacer;

public class DesolationFoliagePlacerTypes {
    public static final FoliagePlacerType<CharredFoliagePlacer> CHARRED_FOLIAGE_PLACER = FoliagePlacerTypeInvoker.desolation$register(
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred_foliage_placer").toString(), CharredFoliagePlacer.CODEC);

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationFoliagePlacerTypes() {
        return;
    }

    public static void init() { }
}
