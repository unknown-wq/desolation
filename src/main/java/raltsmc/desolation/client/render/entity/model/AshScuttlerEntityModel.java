package raltsmc.desolation.client.render.entity.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import net.minecraft.resources.Identifier;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.entity.AshScuttlerEntity;

public class AshScuttlerEntityModel extends DefaultedEntityGeoModel<AshScuttlerEntity> {
	public AshScuttlerEntityModel() {
		super(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "ash_scuttler"));
	}
}
