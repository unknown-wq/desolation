package raltsmc.desolation.client.render.entity;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import raltsmc.desolation.client.render.entity.feature.GeoGlowLayerRenderer;
import raltsmc.desolation.client.render.entity.model.BlackenedEntityModel;
import raltsmc.desolation.entity.BlackenedEntity;

public class BlackenedEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<BlackenedEntity, R> {
    public BlackenedEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlackenedEntityModel());

        withRenderLayer(new GeoGlowLayerRenderer<>(this, "textures/entity/blackened_glow.png"));
    }
}
