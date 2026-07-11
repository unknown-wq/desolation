package raltsmc.desolation.client.weather;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import raltsmc.desolation.tag.DesolationBiomeTags;

/**
 * Client-side "ash rain" effect for the Charred Forest.
 *
 * <p>The Charred Forest biome keeps {@code hasPrecipitation(false)}, so vanilla never renders its
 * blue rain columns there. Instead, whenever a global storm is active ({@code getRainLevel > 0})
 * and the local player stands in a {@link DesolationBiomeTags#CHARRED_FORESTS charred forest}, we
 * spawn a dense field of drifting {@link ParticleTypes#WHITE_ASH} particles above the surrounding
 * terrain so the downpour reads as falling ash rather than water.
 *
 * <p>Registered from {@code DesolationClient#onInitializeClient} via
 * {@link ClientTickEvents#END_CLIENT_TICK}.
 */
@Environment(EnvType.CLIENT)
public final class AshRainRenderer {
    /** Column radius (blocks) around the player in which ash is seeded each tick. */
    private static final int HORIZONTAL_RADIUS = 10;
    /** Ash particles spawned per tick at full rain intensity (scaled down by the rain level). */
    private static final int MAX_SPAWN_PER_TICK = 40;
    /** Only seed columns whose surface sits within this vertical band of the player. */
    private static final int VERTICAL_BAND = 24;

    private AshRainRenderer() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(AshRainRenderer::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
        if (client.isPaused()) {
            return;
        }

        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) {
            return;
        }

        float rainLevel = level.getRainLevel(1.0F);
        if (rainLevel <= 0.0F) {
            return;
        }

        if (!level.getBiome(player.blockPosition()).is(DesolationBiomeTags.CHARRED_FORESTS)) {
            return;
        }

        RandomSource random = level.getRandom();
        int baseX = Mth.floor(player.getX());
        int baseZ = Mth.floor(player.getZ());
        double playerY = player.getY();

        int count = Math.max(1, Mth.ceil(MAX_SPAWN_PER_TICK * rainLevel));
        for (int i = 0; i < count; ++i) {
            int bx = baseX + random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            int bz = baseZ + random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;

            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz);
            // Skip columns whose surface is far above/below the player (deep caves, high flight),
            // mirroring how vanilla rain only renders around the player's surface level.
            if (Math.abs(surface - playerY) > VERTICAL_BAND) {
                continue;
            }

            double x = bx + random.nextDouble();
            double z = bz + random.nextDouble();
            double y = surface + 1.0 + random.nextDouble() * 6.0;

            // WHITE_ASH negates the supplied Y velocity (multiplier -0.1), so a positive input
            // biases the particle downward, giving the ambient ash a falling, rain-like motion.
            level.addParticle(ParticleTypes.WHITE_ASH, x, y, z, 0.0D, 3.0D, 0.0D);
        }
    }
}
