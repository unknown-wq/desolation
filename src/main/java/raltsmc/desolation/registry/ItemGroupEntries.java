package raltsmc.desolation.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Stores the item group's entries as {@link ItemLike} references and materializes
 * {@link ItemStack}s lazily. In 26.1 an ItemStack's components are bound only when the
 * item registry is frozen, so building stacks eagerly during mod init (before freeze)
 * throws "Components not bound yet". getCollection() is only called from the creative-tab
 * callbacks, which run after the registries are frozen.
 */
public record ItemGroupEntries(@Nullable ItemLike relativeItem, ArrayList<ItemLike> items) {
	ItemGroupEntries(ArrayList<ItemLike> items) {
		this(null, items);
	}

	static ItemGroupEntries empty(@Nullable ItemLike relativeItem) {
		return new ItemGroupEntries(relativeItem, new ArrayList<>(64));
	}

	static ItemGroupEntries empty() {
		return new ItemGroupEntries(new ArrayList<>(64));
	}

	void addItem(ItemLike item) {
		items.add(item);
	}

	Collection<ItemStack> getCollection() {
		ArrayList<ItemStack> stacks = new ArrayList<>(items.size());
		for (ItemLike item : items) {
			stacks.add(new ItemStack(item));
		}
		return stacks;
	}
}
