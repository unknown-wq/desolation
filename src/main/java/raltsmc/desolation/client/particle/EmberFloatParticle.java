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
 * An ember carried up off hot ground: rises on the thermal, wobbles, cools from yellow through orange
 * to a dull red and dies. Used for the heat shimmer over ember blocks and as the clearing's ambient
 * particle.
 */
@Environment(EnvType.CLIENT)
public class EmberFloatParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;
    private final double wobbleX;
    private final double wobbleZ;

    public EmberFloatParticle(ClientLevel level, double x, double y, double z, double rise, SpriteSet spriteProvider) {
        super(level, x, y, z, spriteProvider.first());
        this.spriteProvider = spriteProvider;

        this.xd = 0.0D;
        this.yd = 0.015D + rise;
        this.zd = 0.0D;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize *= 0.4F + this.random.nextFloat() * 0.4F;
        this.lifetime = 40 + this.random.nextInt(50);

        this.rCol = 1.0F;
        this.gCol = 0.72F;
        this.bCol = 0.28F;
        this.setAlpha(0.9F);

        this.wobbleX = (this.random.nextDouble() - 0.5D) * 0.012D;
        this.wobbleZ = (this.random.nextDouble() - 0.5D) * 0.012D;

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public int getLightCoords(float tint) {
        // Embers light themselves: force the block-light channel up so they glow in the dark.
        int packed = super.getLightCoords(tint);
        int block = packed & 255;
        int sky = packed >> 16 & 255;

        return Math.max(block, 200) | sky << 16;
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

        float t = (float) this.age / (float) this.lifetime;

        // The thermal weakens as the ember climbs out of the hot air near the ground.
        this.yd = Math.max(0.002D, this.yd * 0.985D);
        double phase = (this.level.getGameTime() + this.age) * Math.PI / 22D;
        this.xd = this.wobbleX + Math.sin(phase) * 0.008D;
        this.zd = this.wobbleZ + Math.cos(phase) * 0.008D;
        this.move(this.xd, this.yd, this.zd);

        // Cool down: yellow-orange to dull red, then fade.
        this.gCol = 0.72F * (1.0F - t) + 0.12F * t;
        this.bCol = 0.28F * (1.0F - t);
        this.setAlpha(0.9F * (1.0F - Mth.clamp((t - 0.6F) * 2.5F, 0.0F, 1.0F)));

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
            return new EmberFloatParticle(level, x, y, z, Math.max(0.0D, velocityY), this.spriteProvider);
        }
    }
}
