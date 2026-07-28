package raltsmc.desolation.init.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.client.particle.SparkParticle;
import raltsmc.desolation.client.weather.AshRainRenderer;
import raltsmc.desolation.client.render.entity.AshScuttlerEntityRenderer;
import raltsmc.desolation.client.render.entity.BlackenedEntityRenderer;
import raltsmc.desolation.init.server.CinderSoulC2SPacket;
import raltsmc.desolation.registry.DesolationEntities;
import raltsmc.desolation.registry.DesolationParticles;
import raltsmc.desolation.registry.DesolationStatusEffects;

@Environment(EnvType.CLIENT)
public class DesolationClient implements ClientModInitializer {
    public static KeyMapping cinderDashBinding;

    @Override
    public void onInitializeClient() {
        // Block render passes are auto-detected from each sprite's alpha in 26.2 (fully
        // transparent -> cutout, partial -> translucent). The only override is a per-texture
        // "force_translucent" flag emitted by datagen; there is no code-side render-layer API.

        EntityRendererRegistry.register(DesolationEntities.ASH_SCUTTLER, AshScuttlerEntityRenderer::new);
        EntityRendererRegistry.register(DesolationEntities.BLACKENED, BlackenedEntityRenderer::new);

        ParticleProviderRegistry.getInstance().register(DesolationParticles.SPARK, SparkParticle.Factory::new);

        // "Ash rain": when a storm is active over a Charred Forest, render falling ash instead of
        // vanilla rain (the biome itself keeps precipitation disabled).
        AshRainRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(DesolationClient::tickCinderDashBinding);
    }

    /**
     * Drains the key binding's click queue and turns a press into one intent packet. Using
     * {@link KeyMapping#consumeClick()} instead of {@code isDown()} means holding the key fires once
     * rather than once per tick, and the client never decides whether the dash actually happens.
     */
    private static void tickCinderDashBinding(net.minecraft.client.Minecraft client) {
        boolean pressed = false;

        while (cinderDashBinding.consumeClick()) {
            pressed = true;
        }

        LocalPlayer player = client.player;

        if (!pressed || player == null || player.isSpectator()) {
            return;
        }

        if (!player.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
            return;
        }

        if (ClientPlayNetworking.canSend(CinderSoulC2SPacket.ID)) {
            ClientPlayNetworking.send(new CinderSoulC2SPacket(CinderSoulC2SPacket.Action.DASH));
        }
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
