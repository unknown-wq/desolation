package raltsmc.desolation.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationPlacementModifiers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A count-style placement modifier that clusters features into "hotspots" (burn scars).
 * <p>
 * It emits {@code count} copies of the incoming position, where {@code count} is driven by a
 * deterministic noise field sampled at the position's XZ. All features using this modifier read the
 * <em>same</em> noise instance for a given world, so their densities correlate — embers, ash, tufts
 * and boulders pile up in the same regions and thin out between them.
 * <p>
 * The noise is seeded from the world seed (not a fixed constant), so burn scars land in different
 * places in every world. Every shape knob — {@code frequency}, {@code threshold}, {@code intensity}
 * and the {@code enabled} toggle — lives in the codec, so datapacks that override a placed feature
 * are honoured. {@link DesolationConfig} only contributes a global on/off switch and a density
 * multiplier, and those are snapshotted once per world (on the first chunk generated for a given
 * seed) rather than read per chunk: a config edit mid-session can therefore never produce seams
 * between neighbouring chunks.
 */
public class HotspotPlacement extends PlacementModifier {
    /** Noise frequency used by the mod's own placed features (blocks<sup>-1</sup>). */
    public static final double DEFAULT_FREQUENCY = 0.02D;
    /** Noise value above which a position counts as inside a burn scar. */
    public static final double DEFAULT_THRESHOLD = 0.30D;
    /** Multiplier applied to {@code hotspot_count} at the centre of a scar. */
    public static final double DEFAULT_INTENSITY = 1.0D;

    public static final MapCodec<HotspotPlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.DOUBLE.fieldOf("salt").orElse(0d).forGetter((p) -> p.salt),
            Codec.INT.fieldOf("base_count").orElse(0).forGetter((p) -> p.baseCount),
            Codec.INT.fieldOf("hotspot_count").orElse(1).forGetter((p) -> p.hotspotCount),
            Codec.DOUBLE.fieldOf("frequency").orElse(DEFAULT_FREQUENCY).forGetter((p) -> p.frequency),
            Codec.DOUBLE.fieldOf("threshold").orElse(DEFAULT_THRESHOLD).forGetter((p) -> p.threshold),
            Codec.DOUBLE.fieldOf("intensity").orElse(DEFAULT_INTENSITY).forGetter((p) -> p.intensity),
            Codec.BOOL.fieldOf("enabled").orElse(true).forGetter((p) -> p.enabled)
    ).apply(instance, HotspotPlacement::new));

    /** Hash key used to fork the world seed into this modifier's own noise stream. */
    private static final Identifier NOISE_KEY = Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "hotspot");

    /**
     * Per-world noise + config snapshot, keyed by world seed. Worldgen runs on many threads and
     * touches several worlds per process at most, so a concurrent map costs nothing and keeps every
     * chunk of a given world on exactly the same field.
     */
    private static final Map<Long, WorldTuning> TUNING_BY_SEED = new ConcurrentHashMap<>();

    private final double salt;
    private final int baseCount;
    private final int hotspotCount;
    private final double frequency;
    private final double threshold;
    private final double intensity;
    private final boolean enabled;

    public HotspotPlacement(double salt, int baseCount, int hotspotCount, double frequency, double threshold,
                            double intensity, boolean enabled) {
        this.salt = salt;
        this.baseCount = baseCount;
        this.hotspotCount = hotspotCount;
        this.frequency = frequency;
        this.threshold = threshold;
        this.intensity = intensity;
        this.enabled = enabled;
    }

    public static HotspotPlacement of(double salt, int baseCount, int hotspotCount) {
        return of(salt, baseCount, hotspotCount, DEFAULT_FREQUENCY, DEFAULT_THRESHOLD, DEFAULT_INTENSITY);
    }

    public static HotspotPlacement of(double salt, int baseCount, int hotspotCount, double frequency, double threshold,
                                      double intensity) {
        return new HotspotPlacement(salt, baseCount, hotspotCount, frequency, threshold, intensity, true);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        if (!this.enabled) {
            return flat(pos);
        }

        WorldTuning tuning = tuningFor(context.getLevel().getSeed());
        if (!tuning.clustered()) {
            // Fallback: behave like the old flat CountPlacement (baseCount + full hotspotCount).
            return flat(pos);
        }

        double n = tuning.noise().getValue(pos.getX() * this.frequency + this.salt, pos.getZ() * this.frequency, false);

        int count = this.baseCount;
        if (n > this.threshold) {
            double span = Math.max(1.0E-4D, 1.0D - this.threshold);
            double t = Mth.clamp((n - this.threshold) / span, 0.0D, 1.0D);
            count += (int) Math.round(this.hotspotCount * this.intensity * tuning.densityScale() * t);
        }

        return IntStream.range(0, count).mapToObj((i) -> pos);
    }

    private Stream<BlockPos> flat(BlockPos pos) {
        return IntStream.range(0, this.baseCount + this.hotspotCount).mapToObj((i) -> pos);
    }

    private static WorldTuning tuningFor(long seed) {
        return TUNING_BY_SEED.computeIfAbsent(seed, HotspotPlacement::createTuning);
    }

    private static WorldTuning createTuning(long seed) {
        // XOROSHIRO + a positional fork keyed on our own identifier: the field is reproducible for a
        // given seed but shares no state with vanilla's noise streams.
        RandomSource source = WorldgenRandom.Algorithm.XOROSHIRO.newInstance(seed).forkPositional().fromHashOf(NOISE_KEY);
        DesolationConfig config = Desolation.CONFIG;

        return new WorldTuning(new PerlinSimplexNoise(source, List.of(0)), config.clusterFeatures,
                config.hotspotDensityScale);
    }

    @Override
    public PlacementModifierType<?> type() {
        return DesolationPlacementModifiers.HOTSPOT;
    }

    /** Noise field plus the config snapshot taken when a world first generated a chunk. */
    private record WorldTuning(PerlinSimplexNoise noise, boolean clustered, double densityScale) {
    }
}
