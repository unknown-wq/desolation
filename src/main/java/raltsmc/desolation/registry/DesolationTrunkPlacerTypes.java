package raltsmc.desolation.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.mixin.worldgen.PlacerTypeInvokers.TrunkPlacerTypeInvoker;
import raltsmc.desolation.world.gen.trunk.BasedTrunkPlacer;
import raltsmc.desolation.world.gen.trunk.FallenTrunkPlacer;

public class DesolationTrunkPlacerTypes {
    public static final TrunkPlacerType<FallenTrunkPlacer> FALLEN = TrunkPlacerTypeInvoker.desolation$register(
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "fallen_trunk_placer").toString(), FallenTrunkPlacer.CODEC);
    public static final TrunkPlacerType<BasedTrunkPlacer> BASED = TrunkPlacerTypeInvoker.desolation$register(
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "based_trunk_placer").toString(), BasedTrunkPlacer.CODEC);

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationTrunkPlacerTypes() {
        return;
    }

    public static void init() { }
}
