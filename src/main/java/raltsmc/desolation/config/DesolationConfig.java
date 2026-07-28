package raltsmc.desolation.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Mth;

@Config(name = "desolation")
public class DesolationConfig implements ConfigData {
    public boolean showGogglesOverlay = true;
    public boolean biomeSoundAmbience = true;

    // "Ashen Lung": breathing ash-laden air fills a meter instead of applying a flat debuff. The
    // meter gains 1 point per tick at the base rate (doubled on/in ash, doubled again during an ash
    // storm) and drains outside the biome, so the thresholds below are roughly "seconds times 20".
    public boolean ashenLungEnabled = true;
    public int ashenLungMaxSmoke = 600;
    public int ashenLungMiningFatigueThreshold = 240;
    public int ashenLungWeaknessThreshold = 450;
    public int ashenLungRecoveryRate = 3;
    public int ashenLungFilterDamageInterval = 100;

    // Cinder Dash. The dash itself is applied by the server, so these values are authoritative and
    // clients cannot exceed them.
    public boolean dashEnabled = true;
    public int dashMaxCharges = 3;
    public int dashRechargeTicks = 200;
    public int dashDurationTicks = 10;
    public double dashSpeed = 0.75D;
    public double dashDamage = 6.0D;
    public int dashIgniteSeconds = 6;

    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double charredForestChance = 0.08D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double smallCharredForestChance = 0.04D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public double charredForestClearingChance = 0.05D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart
    public boolean generateClearings = true;

    // Charred Hut. Spacing and separation live in the structure set (data, so datapacks can retune
    // them); what is here is the switch, a thinning factor applied on top of the spacing, and how
    // often a hut still has anything left in it worth opening.
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart @ConfigEntry.Gui.Tooltip
    public boolean charredHutEnabled = true;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart @ConfigEntry.Gui.Tooltip
    public double charredHutRarity = 0.8D;
    @ConfigEntry.Category("generation") @ConfigEntry.Gui.RequiresRestart @ConfigEntry.Gui.Tooltip
    public double charredHutChestChance = 0.45D;

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
        charredHutRarity = Mth.clamp(charredHutRarity, 0.0D, 1.0D);
        charredHutChestChance = Mth.clamp(charredHutChestChance, 0.0D, 1.0D);
        ashRainDensity = Mth.clamp(ashRainDensity, 0.0D, 3.0D);
        postFxHeatHazeStrength = Mth.clamp(postFxHeatHazeStrength, 0.0D, 3.0D);

        ashenLungMaxSmoke = Mth.clamp(ashenLungMaxSmoke, 20, 24000);
        ashenLungMiningFatigueThreshold = Mth.clamp(ashenLungMiningFatigueThreshold, 1, ashenLungMaxSmoke);
        ashenLungWeaknessThreshold = Mth.clamp(ashenLungWeaknessThreshold, ashenLungMiningFatigueThreshold, ashenLungMaxSmoke);
        ashenLungRecoveryRate = Mth.clamp(ashenLungRecoveryRate, 1, 100);
        ashenLungFilterDamageInterval = Mth.clamp(ashenLungFilterDamageInterval, 1, 6000);

        dashMaxCharges = Mth.clamp(dashMaxCharges, 1, 16);
        dashRechargeTicks = Mth.clamp(dashRechargeTicks, 10, 6000);
        dashDurationTicks = Mth.clamp(dashDurationTicks, 1, 40);
        dashSpeed = Mth.clamp(dashSpeed, 0.1D, 3.0D);
        dashDamage = Mth.clamp(dashDamage, 0.0D, 100.0D);
        dashIgniteSeconds = Mth.clamp(dashIgniteSeconds, 0, 60);
    }
}
