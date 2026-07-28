package raltsmc.desolation.item;

import net.minecraft.world.item.Item;

// The permanent glint is a DataComponents.ENCHANTMENT_GLINT_OVERRIDE on the item properties in
// DesolationItems; Item has had no hasGlint() hook to override since the component rewrite.
public class CinderHeartItem extends Item {
    public CinderHeartItem(Properties properties) {
        super(properties);
    }
}
