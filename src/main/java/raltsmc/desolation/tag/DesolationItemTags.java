package raltsmc.desolation.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import raltsmc.desolation.Desolation;

@SuppressWarnings("SameParameterValue")
public final class DesolationItemTags {
	/**
	 * Head-slot gear that filters ash out of the air (see the "Ashen Lung" meter). Checked as a tag
	 * rather than as a single item so add-ons and datapacks can contribute their own masks.
	 */
	public static final TagKey<Item> AIR_FILTERS = DesolationItemTags.of("air_filters");

	public static final TagKey<Item> CHARRED_LOGS = DesolationItemTags.of("charred_logs");

	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationItemTags() {
		return;
	}

	private static TagKey<Item> of(String path) {
		return DesolationItemTags.of(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path));
	}

	private static TagKey<Item> of(Identifier id) {
		return TagKey.create(Registries.ITEM, id);
	}
}
