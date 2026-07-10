package raltsmc.desolation.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.world.gen.placement.HotspotPlacement;

public class DesolationPlacementModifiers {
    public static final PlacementModifierType<HotspotPlacement> HOTSPOT = Registry.register(
            BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "hotspot"),
            () -> HotspotPlacement.CODEC);

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationPlacementModifiers() {
        return;
    }

    // Referencing HOTSPOT forces class-init so the type is registered before worldgen bootstrap.
    public static void init() { }
}
