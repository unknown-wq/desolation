package raltsmc.desolation.registry;

import net.minecraft.data.BlockFamily;

public class DesolationBlockFamilies {
    public static final BlockFamily CHARRED = new BlockFamily.Builder(DesolationBlocks.CHARRED_PLANKS)
            .button(DesolationBlocks.CHARRED_BUTTON)
            .fence(DesolationBlocks.CHARRED_FENCE)
            .fenceGate(DesolationBlocks.CHARRED_FENCE_GATE)
            .pressurePlate(DesolationBlocks.CHARRED_PRESSURE_PLATE)
            .sign(DesolationBlocks.CHARRED_SIGN, DesolationBlocks.CHARRED_WALL_SIGN)
            .slab(DesolationBlocks.CHARRED_SLAB)
            .stairs(DesolationBlocks.CHARRED_STAIRS)
            .door(DesolationBlocks.CHARRED_DOOR)
            .trapdoor(DesolationBlocks.CHARRED_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();
}
