package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import raltsmc.desolation.Desolation;

public class DesolationBlockSetTypes {
	public static final BlockSetType CHARRED = BlockSetTypeBuilder.copyOf(BlockSetType.OAK)
			.register(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "charred"));
}
