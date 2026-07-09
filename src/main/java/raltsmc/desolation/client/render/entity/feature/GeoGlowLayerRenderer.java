package raltsmc.desolation.client.render.entity.feature;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import raltsmc.desolation.Desolation;

/**
 * Renders an emissive (full-bright) glow texture over the model. In GeckoLib 5.5 the render
 * pipeline moved to render-state extraction + {@code SubmitNodeCollector}; the old
 * {@code RenderLayer.getEyes} approach is replaced by GeckoLib's built-in
 * {@link AutoGlowingGeoLayer}, whose glow texture we override to point at an explicit asset.
 */
public class GeoGlowLayerRenderer<T extends Entity & GeoAnimatable, R extends GeoRenderState> extends AutoGlowingGeoLayer<T, Void, R> {
    private final Identifier texture;

    public GeoGlowLayerRenderer(GeoRenderer<T, Void, R> entityRendererIn, String texture) {
        super(entityRendererIn);

        this.texture = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, texture);
    }

    @Override
    protected Identifier getTextureResource(R renderState) {
        return this.texture;
    }
}
