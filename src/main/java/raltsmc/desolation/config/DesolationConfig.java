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

    // Feature clustering ("hotspots" / burn scars). The shape of the scars (frequency, threshold,
    // intensity) lives in the placed features themselves so datapacks can retune it; what is left
    // here is a global switch and a density multiplier, snapshotted once per world rather than read
    // per chunk — otherwise neighbouring chunks would generate against different values and seam.
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public boolean clusterFeatures = true;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double hotspotDensityScale = 1.0D;

    // Client-side "ash rain" (see AshRainRenderer). 0 disables the effect entirely.
    public double ashRainDensity = 1.0D;

    // Client-side heat shimmer over ember clusters (see HeatHazeEffect).
    public boolean postFxHeatHaze = true;
    public double postFxHeatHazeStrength = 1.0D;

    @Override
    public void validatePostLoad() {
        charredForestChance = Mth.clamp(charredForestChance, 0.01D, 1D);
        smallCharredForestChance = Mth.clamp(smallCharredForestChance, 0.01D, 1D);
        charredForestClearingChance = Mth.clamp(charredForestClearingChance, 0.01D, 1D);
        hotspotDensityScale = Mth.clamp(hotspotDensityScale, 0.0D, 5.0D);
        ashRainDensity = Mth.clamp(ashRainDensity, 0.0D, 3.0D);
        postFxHeatHazeStrength = Mth.clamp(postFxHeatHazeStrength, 0.0D, 3.0D);
    }
}
