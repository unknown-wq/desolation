package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.entity.AshScuttlerEntity;
import raltsmc.desolation.entity.BlackenedEntity;

public final class DesolationEntities {
    public static final EntityType<AshScuttlerEntity> ASH_SCUTTLER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "ash_scuttler"),
            EntityType.Builder.of(AshScuttlerEntity::new, MobCategory.AMBIENT)
                    .sized(0.56f, 0.32f)
                    .fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "ash_scuttler")))
    );

    public static final EntityType<BlackenedEntity> BLACKENED = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "blackened"),
            EntityType.Builder.of(BlackenedEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 2f)
                    .fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "blackened")))
    );

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationEntities() {
        return;
    }

    static void init() {
        FabricDefaultAttributeRegistry.register(ASH_SCUTTLER, AshScuttlerEntity.createAnimalAttributes());
        FabricDefaultAttributeRegistry.register(BLACKENED, BlackenedEntity.createBlackenedAttributes());

        SpawnPlacements.register(ASH_SCUTTLER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(BLACKENED, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }
}
