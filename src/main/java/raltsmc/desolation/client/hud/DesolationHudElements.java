package raltsmc.desolation.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.attachment.DesolationAttachments;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationStatusEffects;

/**
 * HUD for the two player-state features. Registered through the Fabric HUD element API rather than a
 * mixin into the vanilla HUD, so other mods can order themselves around it.
 */
@Environment(EnvType.CLIENT)
public final class DesolationHudElements {
    private static final Identifier ASHEN_LUNG = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "ashen_lung");
    private static final Identifier CINDER_DASH = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "cinder_dash");

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 5;
    /** Distance from the bottom of the screen to the ash meter, clear of the hotbar and its bars. */
    private static final int BAR_BOTTOM_OFFSET = 62;
    private static final int PIP_SIZE = 5;
    private static final int PIP_GAP = 3;
    private static final int PIP_BOTTOM_OFFSET = 74;

    private static final int SMOKE_TINT = 0x9C6B52;
    private static final int VIGNETTE_TINT = 0x1A1512;
    private static final int CHARGE_TINT = 0xFF5900;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationHudElements() {
        return;
    }

    public static void register() {
        HudElement ashenLung = DesolationHudElements::renderAshenLung;
        HudElement cinderDash = DesolationHudElements::renderCinderDash;

        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, ASHEN_LUNG, ashenLung);
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, CINDER_DASH, cinderDash);
    }

    private static void renderAshenLung(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        DesolationConfig config = Desolation.CONFIG;
        LocalPlayer player = player();

        if (player == null || !config.ashenLungEnabled) {
            return;
        }

        int smoke = player.getAttachedOrElse(DesolationAttachments.SMOKE_INHALATION, 0);

        if (smoke <= 0) {
            return;
        }

        float ratio = Mth.clamp(smoke / (float) config.ashenLungMaxSmoke, 0F, 1F);
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        // The old "goggles overlay" option finally does something: it gates the haze, not the meter.
        if (config.showGogglesOverlay) {
            int alpha = (int) (ratio * 150F) << 24;
            int opaque = alpha | VIGNETTE_TINT;
            int clear = VIGNETTE_TINT;
            int band = Math.max(1, height / 3);

            graphics.fillGradient(0, 0, width, band, opaque, clear);
            graphics.fillGradient(0, height - band, width, height, clear, opaque);
        }

        int x = (width - BAR_WIDTH) / 2;
        int y = height - BAR_BOTTOM_OFFSET;
        int filled = (int) (BAR_WIDTH * ratio);

        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xAA000000);
        graphics.fill(x, y, x + filled, y + BAR_HEIGHT, 0xFF000000 | SMOKE_TINT);

        Component label = Component.translatable("hud.desolation.ashen_lung");
        graphics.centeredText(Minecraft.getInstance().font, label, width / 2, y - 11, 0xFFD8CCC4);
    }

    private static void renderCinderDash(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        DesolationConfig config = Desolation.CONFIG;
        LocalPlayer player = player();

        if (player == null || !config.dashEnabled || !player.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
            return;
        }

        int charges = Mth.clamp(player.getAttachedOrElse(DesolationAttachments.DASH_CHARGES, 0), 0, config.dashMaxCharges);
        int total = config.dashMaxCharges;
        int stride = PIP_SIZE + PIP_GAP;
        int x = (graphics.guiWidth() - (total * stride - PIP_GAP)) / 2;
        int y = graphics.guiHeight() - PIP_BOTTOM_OFFSET;

        for (int i = 0; i < total; ++i) {
            int left = x + i * stride;

            graphics.fill(left - 1, y - 1, left + PIP_SIZE + 1, y + PIP_SIZE + 1, 0xAA000000);
            graphics.fill(left, y, left + PIP_SIZE, y + PIP_SIZE, i < charges ? (0xFF000000 | CHARGE_TINT) : 0x66FFFFFF);
        }
    }

    /** The HUD root layer is skipped entirely while the GUI is hidden, so only spectators are filtered here. */
    private static LocalPlayer player() {
        LocalPlayer player = Minecraft.getInstance().player;

        return player == null || player.isSpectator() ? null : player;
    }
}
