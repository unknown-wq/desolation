package raltsmc.desolation.client.weather;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationParticles;
import raltsmc.desolation.tag.DesolationBiomeTags;

/**
 * Heat shimmer over concentrations of {@code ember_block}.
 *
 * <p>The surrounding volume is swept for ember blocks once every {@link #SCAN_INTERVAL_TICKS} ticks —
 * never per frame — and up to {@link #SAMPLE_CAPACITY} of the positions found are kept. Each tick a
 * few of those are picked at random and a rising {@link DesolationParticles#EMBER_FLOAT} is emitted
 * just above them, so the air over a big bed of coals visibly boils while a single stray ember barely
 * registers. Intensity is a function of how many embers were counted, and the whole thing only runs
 * inside a {@link DesolationBiomeTags#CHARRED_FORESTS charred forest}.
 *
 * <p>This was meant to be a real screen-space post-effect ({@code PostChain}). In 26.2 the only way
 * to install one is {@code GameRenderer#setPostEffect}, which is private with no Fabric API in front
 * of it, so driving it would mean an accessor mixin and a hand-written 26.2 core shader — far too
 * fragile to be worth it. The shimmer is therefore particle-based; it costs nothing on the render
 * thread and degrades cleanly with the player's particle setting.
 */
@Environment(EnvType.CLIENT)
public final class HeatHazeEffect {
    /** Horizontal reach of the ember sweep, in blocks. */
    private static final int SCAN_RADIUS_XZ = 12;
    /** Vertical reach of the ember sweep, in blocks. */
    private static final int SCAN_RADIUS_Y = 6;
    /** How often the (comparatively expensive) block sweep runs. */
    private static final int SCAN_INTERVAL_TICKS = 20;
    /** Ember positions remembered per sweep. */
    private static final int SAMPLE_CAPACITY = 96;
    /** Ember count at which the shimmer reaches full strength. */
    private static final float SATURATION_COUNT = 48.0F;
    /** Shimmer particles emitted per tick at full strength. */
    private static final int MAX_SPAWN_PER_TICK = 6;

    private static final long[] SAMPLES = new long[SAMPLE_CAPACITY];
    private static final BlockPos.MutableBlockPos SCAN_CURSOR = new BlockPos.MutableBlockPos();

    private static int sampleCount;
    private static float intensity;
    private static long lastScanTick = Long.MIN_VALUE;

    private HeatHazeEffect() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(HeatHazeEffect::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
        if (client.isPaused()) {
            return;
        }

        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) {
            reset();
            return;
        }

        double strength = Desolation.CONFIG.postFxHeatHaze
                ? Desolation.CONFIG.postFxHeatHazeStrength * particleScale(client)
                : 0.0D;
        if (strength <= 0.0D) {
            reset();
            return;
        }

        if (!level.getBiome(player.blockPosition()).is(DesolationBiomeTags.CHARRED_FORESTS)) {
            reset();
            return;
        }

        long now = level.getGameTime();
        if (now - lastScanTick >= SCAN_INTERVAL_TICKS || now < lastScanTick) {
            scan(level, player.blockPosition());
            lastScanTick = now;
        }

        if (sampleCount == 0 || intensity <= 0.0F) {
            return;
        }

        RandomSource random = level.getRandom();
        int count = (int) Math.round(MAX_SPAWN_PER_TICK * intensity * strength);
        for (int i = 0; i < count; ++i) {
            BlockPos pos = BlockPos.of(SAMPLES[random.nextInt(sampleCount)]);
            double x = pos.getX() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double y = pos.getY() + 1.0D + random.nextDouble() * 0.4D;

            level.addParticle(DesolationParticles.EMBER_FLOAT, x, y, z, 0.0D, 0.02D * intensity, 0.0D);
        }
    }

    /** Sweeps the volume around the player, counting ember blocks and remembering where they are. */
    private static void scan(ClientLevel level, BlockPos center) {
        int found = 0;
        int stored = 0;

        for (int dy = -SCAN_RADIUS_Y; dy <= SCAN_RADIUS_Y; ++dy) {
            int y = center.getY() + dy;
            if (level.isOutsideBuildHeight(y)) {
                continue;
            }

            for (int dz = -SCAN_RADIUS_XZ; dz <= SCAN_RADIUS_XZ; ++dz) {
                for (int dx = -SCAN_RADIUS_XZ; dx <= SCAN_RADIUS_XZ; ++dx) {
                    SCAN_CURSOR.set(center.getX() + dx, y, center.getZ() + dz);
                    if (!level.getBlockState(SCAN_CURSOR).is(DesolationBlocks.EMBER_BLOCK)) {
                        continue;
                    }

                    ++found;
                    // Only the ones with open air above can push heat into the player's view.
                    if (stored < SAMPLE_CAPACITY && level.getBlockState(SCAN_CURSOR.above()).isAir()) {
                        SAMPLES[stored++] = SCAN_CURSOR.asLong();
                    }
                }
            }
        }

        sampleCount = stored;
        intensity = Mth.clamp(found / SATURATION_COUNT, 0.0F, 1.0F);
    }

    private static void reset() {
        sampleCount = 0;
        intensity = 0.0F;
        lastScanTick = Long.MIN_VALUE;
    }

    private static double particleScale(Minecraft client) {
        ParticleStatus status = client.options.particles().get();

        return switch (status) {
            case ALL -> 1.0D;
            case DECREASED -> 0.5D;
            case MINIMAL -> 0.0D;
        };
    }
}
