package raltsmc.desolation.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import raltsmc.desolation.Desolation;

@SuppressWarnings("SameParameterValue")
public final class DesolationBlockTags {
	public static final TagKey<Block> CHARRED_LOGS = DesolationBlockTags.of("charred_logs");
	public static final TagKey<Block> SCORCHED_EARTH = DesolationBlockTags.of("scorched_earth");

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationBlockTags() {
		return;
	}

	private static TagKey<Block> of(String path) {
		return DesolationBlockTags.of(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path));
	}

	private static TagKey<Block> of(Identifier id) {
		return TagKey.create(Registries.BLOCK, id);
	}
}
