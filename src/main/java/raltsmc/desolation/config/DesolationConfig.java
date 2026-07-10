package raltsmc.desolation.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Mth;

@Config(name = "desolation")
public class DesolationConfig implements ConfigData {
    public boolean showGogglesOverlay = true;
    public boolean biomeSoundAmbience = true;

    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double charredForestChance = 0.08D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double smallCharredForestChance = 0.04D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double charredForestClearingChance = 0.05D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public boolean generateClearings = true;

    // Feature clustering ("hotspots" / burn scars). Read live during chunk generation, so no restart
    // is required — changes take effect on newly generated chunks.
    @ConfigEntry.Category("generation")
    public boolean clusterFeatures = true;
    @ConfigEntry.Category("generation")
    public double hotspotFrequency = 0.02D;
    @ConfigEntry.Category("generation")
    public double hotspotThreshold = 0.30D;
    @ConfigEntry.Category("generation")
    public double hotspotIntensity = 1.0D;

    @Override
    public void validatePostLoad() {
        charredForestChance = Mth.clamp(charredForestChance, 0.01D, 1D);
        smallCharredForestChance = Mth.clamp(smallCharredForestChance, 0.01D, 1D);
        charredForestClearingChance = Mth.clamp(charredForestClearingChance, 0.01D, 1D);
        hotspotFrequency = Mth.clamp(hotspotFrequency, 0.002D, 0.2D);
        hotspotThreshold = Mth.clamp(hotspotThreshold, -1.0D, 0.95D);
        hotspotIntensity = Mth.clamp(hotspotIntensity, 0.0D, 5.0D);
    }
}
