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
import net.minecraft.world.level.levelgen.Heightmap;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.registry.DesolationParticles;
import raltsmc.desolation.tag.DesolationBiomeTags;

/**
 * Client-side "ash rain" effect for the Charred Forest.
 *
 * <p>The Charred Forest biome keeps {@code hasPrecipitation(false)}, so vanilla never renders its
 * blue rain columns there. Instead, whenever a global storm is active ({@code getRainLevel > 0})
 * we spawn a field of falling {@link DesolationParticles#ASH_DRIFT} over the surrounding terrain so
 * the downpour reads as falling ash rather than water.
 *
 * <p>The surrounding columns — surface height and "is this a charred forest" — are sampled on a
 * two-block grid into {@link ColumnCache} and refreshed only when the player moves or the cache goes
 * stale, instead of calling {@code getHeight} once per particle per tick. Because the biome is tested
 * per column rather than only under the player, the effect fades in and out across a biome border
 * instead of snapping on the moment the player steps over it. The particle budget scales with the
 * player's own particle setting and with the {@code ashRainDensity} config value (0 disables it).
 *
 * <p>Registered from {@code DesolationClient#onInitializeClient} via
 * {@link ClientTickEvents#END_CLIENT_TICK}.
 */
@Environment(EnvType.CLIENT)
public final class AshRainRenderer {
    /** Column radius (blocks) around the player in which ash is seeded each tick. */
    private static final int HORIZONTAL_RADIUS = 10;
    /** Spacing of the sampled column grid; particles get a random position inside their cell. */
    private static final int COLUMN_STEP = 2;
    /** Number of sampled columns along one axis. */
    private static final int GRID = HORIZONTAL_RADIUS * 2 / COLUMN_STEP + 1;
    /** Ash particles spawned per tick at full rain intensity (scaled down by everything below). */
    private static final int MAX_SPAWN_PER_TICK = 40;
    /** Only seed columns whose surface sits within this vertical band of the player. */
    private static final int VERTICAL_BAND = 24;
    /** Resample the column grid at least this often, so terrain and chunk loads are picked up. */
    private static final int CACHE_LIFETIME_TICKS = 40;

    private static final ColumnCache COLUMNS = new ColumnCache();

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

        double density = Desolation.CONFIG.ashRainDensity * particleScale(client);
        if (density <= 0.0D) {
            return;
        }

        int baseX = Mth.floor(player.getX());
        int baseZ = Mth.floor(player.getZ());
        COLUMNS.refresh(level, baseX, baseZ, Mth.floor(player.getY()));

        // Coverage is the share of nearby columns actually inside a charred forest, so the effect
        // ramps up as the player walks in rather than snapping on at the border.
        float coverage = COLUMNS.coverage();
        if (coverage <= 0.0F) {
            return;
        }

        int count = (int) Math.round(MAX_SPAWN_PER_TICK * rainLevel * coverage * density);
        if (count <= 0) {
            return;
        }

        RandomSource random = level.getRandom();
        double playerY = player.getY();

        for (int i = 0; i < count; ++i) {
            int cell = random.nextInt(GRID * GRID);
            if (!COLUMNS.isCharred(cell)) {
                continue;
            }

            int surface = COLUMNS.surface(cell);
            // Skip columns whose surface is far above/below the player (deep caves, high flight),
            // mirroring how vanilla rain only renders around the player's surface level.
            if (Math.abs(surface - playerY) > VERTICAL_BAND) {
                continue;
            }

            double x = COLUMNS.cellX(cell) + random.nextDouble() * COLUMN_STEP;
            double z = COLUMNS.cellZ(cell) + random.nextDouble() * COLUMN_STEP;
            double y = surface + 1.0D + random.nextDouble() * 6.0D;

            // The Y velocity is read by AshDriftParticle as an extra downward speed, turning the
            // ambient drift into something that reads as falling.
            level.addParticle(DesolationParticles.ASH_DRIFT, x, y, z, 0.0D, 0.28D * rainLevel, 0.0D);
        }
    }

    /** Budget multiplier honouring the player's particle setting; MINIMAL must stay nearly free. */
    private static double particleScale(Minecraft client) {
        ParticleStatus status = client.options.particles().get();

        return switch (status) {
            case ALL -> 1.0D;
            case DECREASED -> 0.5D;
            case MINIMAL -> 0.1D;
        };
    }

    /**
     * Surface height and biome membership for the columns around the player, sampled on a
     * {@link #COLUMN_STEP} grid and reused between ticks.
     */
    @Environment(EnvType.CLIENT)
    private static final class ColumnCache {
        private final int[] surfaces = new int[GRID * GRID];
        private final boolean[] charred = new boolean[GRID * GRID];
        private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        private ClientLevel level;
        private int originX = Integer.MIN_VALUE;
        private int originZ = Integer.MIN_VALUE;
        private long builtAt = Long.MIN_VALUE;
        private float coverage;

        void refresh(ClientLevel level, int playerX, int playerZ, int playerY) {
            int newOriginX = playerX - HORIZONTAL_RADIUS;
            int newOriginZ = playerZ - HORIZONTAL_RADIUS;
            long now = level.getGameTime();

            boolean stale = this.level != level
                    || this.originX != newOriginX
                    || this.originZ != newOriginZ
                    || now - this.builtAt >= CACHE_LIFETIME_TICKS
                    || now < this.builtAt;
            if (!stale) {
                return;
            }

            this.level = level;
            this.originX = newOriginX;
            this.originZ = newOriginZ;
            this.builtAt = now;

            int charredColumns = 0;
            for (int gz = 0; gz < GRID; ++gz) {
                for (int gx = 0; gx < GRID; ++gx) {
                    int index = gz * GRID + gx;
                    int bx = newOriginX + gx * COLUMN_STEP;
                    int bz = newOriginZ + gz * COLUMN_STEP;

                    int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz);
                    this.surfaces[index] = surface;

                    // Sample the biome at the player's own height: biomes are 3D, and asking at the
                    // surface would make the effect vanish while standing on a hill.
                    this.cursor.set(bx, playerY, bz);
                    boolean inBiome = level.getBiome(this.cursor).is(DesolationBiomeTags.CHARRED_FORESTS);
                    this.charred[index] = inBiome;
                    if (inBiome) {
                        ++charredColumns;
                    }
                }
            }

            this.coverage = (float) charredColumns / (float) (GRID * GRID);
        }

        float coverage() {
            return this.coverage;
        }

        boolean isCharred(int index) {
            return this.charred[index];
        }

        int surface(int index) {
            return this.surfaces[index];
        }

        int cellX(int index) {
            return this.originX + (index % GRID) * COLUMN_STEP;
        }

        int cellZ(int index) {
            return this.originZ + (index / GRID) * COLUMN_STEP;
        }
    }
}
