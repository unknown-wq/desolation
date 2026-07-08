package raltsmc.desolation.init.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.client.particle.SparkParticle;
import raltsmc.desolation.client.render.entity.AshScuttlerEntityRenderer;
import raltsmc.desolation.client.render.entity.BlackenedEntityRenderer;
import raltsmc.desolation.registry.DesolationEntities;
import raltsmc.desolation.registry.DesolationParticles;

@Environment(EnvType.CLIENT)
public class DesolationClient implements ClientModInitializer {
    public static KeyMapping cinderDashBinding;

    @Override
    public void onInitializeClient() {
        // Block render layers (translucent/cutout) are now declared via the "render_type"
        // field in each block model JSON (Fabric's BlockRenderLayerMap was removed in 26.1).

        EntityRendererRegistry.register(DesolationEntities.ASH_SCUTTLER, AshScuttlerEntityRenderer::new);
        EntityRendererRegistry.register(DesolationEntities.BLACKENED, BlackenedEntityRenderer::new);

        ParticleProviderRegistry.getInstance().register(DesolationParticles.SPARK, SparkParticle.Factory::new);
    }

    static {
        cinderDashBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.desolation.cinder_dash",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "cat"))
        ));
    }
}
