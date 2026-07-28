package raltsmc.desolation.world.structure;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import raltsmc.desolation.Desolation;

/**
 * Data-generated {@code worldgen/structure_set} entries: how often the structures above are allowed
 * to try.
 */
public final class DesolationStructureSets {
	public static final ResourceKey<StructureSet> CHARRED_HUTS = of("charred_huts");

	/**
	 * Denser than the Ash Tinker Base (32/7) because a burnt-out homestead is meant to be a thing you
	 * come across rather than a thing you go looking for — but the charred forest is itself an uncommon
	 * biome, and {@code charredHutRarity} thins the survivors further, so this is not a hut per ridge.
	 */
	public static final int SPACING = 20;
	public static final int SEPARATION = 6;
	private static final int SALT = 792814;

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationStructureSets() {
		return;
	}

	public static void bootstrap(BootstrapContext<StructureSet> context) {
		HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

		context.register(CHARRED_HUTS, new StructureSet(
				structures.getOrThrow(DesolationStructureFeatures.CHARRED_HUT),
				new RandomSpreadStructurePlacement(SPACING, SEPARATION, RandomSpreadType.LINEAR, SALT)));
	}

	private static ResourceKey<StructureSet> of(String path) {
		return ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path));
	}
}
