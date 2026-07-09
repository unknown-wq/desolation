package raltsmc.desolation.client.render.entity;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import raltsmc.desolation.client.render.entity.feature.GeoGlowLayerRenderer;
import raltsmc.desolation.client.render.entity.model.AshScuttlerEntityModel;
import raltsmc.desolation.entity.AshScuttlerEntity;

public class AshScuttlerEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<AshScuttlerEntity, R> {
    public AshScuttlerEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AshScuttlerEntityModel());

        withRenderLayer(new GeoGlowLayerRenderer<>(this, "textures/entity/ash_scuttler_glow.png"));
    }
}
