package raltsmc.desolation.world.gen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationPlacementModifiers;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A count-style placement modifier that clusters features into "hotspots" (burn scars).
 * <p>
 * It emits {@code count} copies of the incoming position, where {@code count} is driven by a shared,
 * deterministic noise field sampled at the position's XZ. All features using this modifier read the
 * <em>same</em> static noise instance, so their densities correlate — embers, ash, tufts and boulders
 * pile up in the same regions and thin out between them. The tunable knobs (enable toggle, frequency,
 * threshold, intensity) are read live from {@link DesolationConfig} inside {@link #getPositions}, which
 * runs at chunk-generation time; only the per-feature identity (salt + base/hotspot counts) is baked
 * into the codec/JSON.
 */
public class HotspotPlacement extends PlacementModifier {
    public static final MapCodec<HotspotPlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            com.mojang.serialization.Codec.DOUBLE.fieldOf("salt").orElse(0d).forGetter((p) -> p.salt),
            com.mojang.serialization.Codec.INT.fieldOf("base_count").orElse(0).forGetter((p) -> p.baseCount),
            com.mojang.serialization.Codec.INT.fieldOf("hotspot_count").orElse(1).forGetter((p) -> p.hotspotCount)
    ).apply(instance, HotspotPlacement::new));

    // Shared, world-independent noise (mirrors the approach of vanilla's Biome.BIOME_INFO_NOISE):
    // a single static instance so every feature that reads it co-locates its hotspots, with no
    // per-call allocation.
    private static final PerlinSimplexNoise NOISE =
            new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(0x0DE5015AL)), List.of(0));

    private final double salt;
    private final int baseCount;
    private final int hotspotCount;

    public HotspotPlacement(double salt, int baseCount, int hotspotCount) {
        this.salt = salt;
        this.baseCount = baseCount;
        this.hotspotCount = hotspotCount;
    }

    public static HotspotPlacement of(double salt, int baseCount, int hotspotCount) {
        return new HotspotPlacement(salt, baseCount, hotspotCount);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        DesolationConfig cfg = Desolation.CONFIG;

        if (!cfg.clusterFeatures) {
            // Fallback: behave like the old flat CountPlacement (baseCount + full hotspotCount).
            return IntStream.range(0, baseCount + hotspotCount).mapToObj((i) -> pos);
        }

        double freq = cfg.hotspotFrequency;
        double n = NOISE.getValue(pos.getX() * freq + salt, pos.getZ() * freq, false);

        int count = baseCount;
        if (n > cfg.hotspotThreshold) {
            double span = Math.max(1.0E-4D, 1.0D - cfg.hotspotThreshold);
            double t = Mth.clamp((n - cfg.hotspotThreshold) / span, 0.0D, 1.0D);
            count += (int) Math.round(hotspotCount * cfg.hotspotIntensity * t);
        }

        return IntStream.range(0, count).mapToObj((i) -> pos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return DesolationPlacementModifiers.HOTSPOT;
    }
}
