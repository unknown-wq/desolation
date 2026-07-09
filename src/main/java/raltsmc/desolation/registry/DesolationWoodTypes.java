package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.WoodType;
import raltsmc.desolation.Desolation;

public class DesolationWoodTypes {
	public static final WoodType CHARRED = WoodTypeBuilder.copyOf(WoodType.OAK)
			.register(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred"), DesolationBlockSetTypes.CHARRED);
}
