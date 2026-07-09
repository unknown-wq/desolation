package raltsmc.desolation.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CinderHeartItem extends Item {
    public CinderHeartItem(Properties properties) {
        super(properties);
    }

    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
