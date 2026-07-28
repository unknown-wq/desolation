package raltsmc.desolation.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * A flake of ash on the wind. Replaces the vanilla {@code WHITE_ASH} the biomes and the ash rain used
 * to borrow: this one is grey rather than white, falls at a speed we choose, and takes a supplied Y
 * velocity at face value so callers can push it down harder during an ash storm.
 */
@Environment(EnvType.CLIENT)
public class AshDriftParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;
    private final double driftX;
    private final double driftZ;
    private final float baseAlpha;

    public AshDriftParticle(ClientLevel level, double x, double y, double z, double fallSpeed, SpriteSet spriteProvider) {
        super(level, x, y, z, spriteProvider.first());
        this.spriteProvider = spriteProvider;

        this.xd = 0.0D;
        this.yd = -0.012D - fallSpeed;
        this.zd = 0.0D;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize *= 0.6F + this.random.nextFloat() * 0.5F;
        this.lifetime = 80 + this.random.nextInt(80);

        // Cold, dirty grey with a little variation so a field of them does not look flat.
        float shade = 0.42F + this.random.nextFloat() * 0.22F;
        this.rCol = shade;
        this.gCol = shade * 0.97F;
        this.bCol = shade * 0.95F;
        this.baseAlpha = 0.55F + this.random.nextFloat() * 0.3F;
        this.setAlpha(this.baseAlpha);

        this.driftX = (this.random.nextDouble() - 0.5D) * 0.02D;
        this.driftZ = (this.random.nextDouble() - 0.5D) * 0.02D;

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Slow lateral sway; the phase is derived from the world time so a whole field drifts together.
        double phase = (this.level.getGameTime() + this.age) * Math.PI / 90D;
        this.xd = this.driftX + Math.sin(phase) * 0.01D;
        this.zd = this.driftZ + Math.cos(phase * 0.7D) * 0.01D;
        this.move(this.xd, this.yd, this.zd);

        // Fade out over the last quarter of the lifetime instead of popping.
        float t = (float) this.age / (float) this.lifetime;
        this.setAlpha(this.baseAlpha * (1.0F - Mth.clamp((t - 0.75F) * 4.0F, 0.0F, 1.0F)));

        this.setSpriteFromAge(this.spriteProvider);
    }

    @Override
    protected Layer getLayer() {
        return Layer.bySprite(this.sprite);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(FabricSpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ, RandomSource random) {
            // The Y velocity is used as an extra fall speed, so ambient ash can drift while ash rain falls.
            return new AshDriftParticle(level, x, y, z, Math.max(0.0D, velocityY), this.spriteProvider);
        }
    }
}
