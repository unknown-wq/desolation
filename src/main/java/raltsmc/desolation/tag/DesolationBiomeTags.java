package raltsmc.desolation.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import raltsmc.desolation.Desolation;

@SuppressWarnings("SameParameterValue")
public final class DesolationBiomeTags {
	public static final TagKey<Biome> ASH_TINKER_BASE_HAS_STRUCTURE = DesolationBiomeTags.of("ash_tinker_base_has_structure");
	public static final TagKey<Biome> CHARRED_HUT_HAS_STRUCTURE = DesolationBiomeTags.of("charred_hut_has_structure");

	/** Charred Forest biomes (all variants); used by the surface rules to place charred soil. */
	public static final TagKey<Biome> CHARRED_FORESTS = DesolationBiomeTags.of("charred_forests");

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationBiomeTags() {
		return;
	}

	private static TagKey<Biome> of(String path) {
		return DesolationBiomeTags.of(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path));
	}

	private static TagKey<Biome> of(Identifier id) {
		return TagKey.create(Registries.BIOME, id);
	}
}
