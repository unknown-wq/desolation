package raltsmc.desolation.mixin.client.gui.hud;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

/**
 * The goggles HUD overlay was driven entirely by the Trinkets API (goggles worn in a trinket
 * slot). Trinkets was removed in the 26.1 port, so there is no longer a way to detect an
 * equipped pair of goggles and the overlay injection has been removed. Additionally, the
 * vanilla HUD overlay pipeline was rewritten in 26.1 ({@code Gui} now uses the
 * {@code GuiGraphicsExtractor} "extract" pattern instead of {@code renderMiscOverlays}).
 *
 * <p>The class and its {@code desolation.mixins.json} entry are retained per the port plan.
 * Reintroduce the overlay here if a non-trinket goggles-equip mechanism is added.
 */
@Mixin(Gui.class)
public abstract class InGameHudMixin {
}
