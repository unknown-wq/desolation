package raltsmc.desolation.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import raltsmc.desolation.Desolation;

public class DesolationLootTables {
    public static final ResourceKey<LootTable> ASH_SCUTTLER_DIG = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "misc/ash_scuttler_dig"));
    public static final ResourceKey<LootTable> ASH_TINKER_BASE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "chests/ash_tinker_base"));

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationLootTables() {
        return;
    }
}
