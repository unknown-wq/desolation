package raltsmc.desolation.client.render.entity.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import net.minecraft.resources.Identifier;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.entity.BlackenedEntity;

public class BlackenedEntityModel extends DefaultedEntityGeoModel<BlackenedEntity> {
	public BlackenedEntityModel() {
		super(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "blackened"));
	}
}
