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

@Environment(EnvType.CLIENT)
public class SparkParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;
    private final double windConstantX;
    private final double windConstantY;
    private final double windConstantZ;

    public SparkParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteProvider) {
        super(level, x, y, z, spriteProvider.first());
        this.xd = 0D;
        this.yd = random.nextDouble() * 0.2D + 0.1D;
        this.zd = 0D;
        this.rCol = 1.0F;
        this.gCol = 0.5F;
        this.bCol = 0.0F;
        this.gravity = 0.25F;
        this.lifetime = 30;
        this.spriteProvider = spriteProvider;
        this.setSpriteFromAge(spriteProvider);

        this.windConstantX = (Math.random() - 0.5D) * 2D;
        this.windConstantY = Math.random() - 0.5D;
        this.windConstantZ = (Math.random() - 0.5D) * 2D;
    }

    @Override
    public int getLightCoords(float tint) {
        float f = ((float)this.age + tint) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightCoords(tint);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);

            long worldTime = this.level.getGameTime();
            double windFactorX = Math.sin(worldTime * Math.PI / 2500D) * 2D;
            double windFactorY = (Math.sin(worldTime * Math.PI / 30D) * 0.1) + 1.1D;
            double windFactorZ = Math.cos(worldTime * Math.PI / 3000D) * 2D;
            float toMax = this.age / (float)this.lifetime;
            this.xd += (toMax * windFactorX + this.windConstantX) * 0.01D;
            this.yd += (toMax * windFactorY + this.windConstantY) * 0.005D;
            this.zd += (toMax * windFactorZ + this.windConstantZ) * 0.01D;

            this.gCol = (1F - toMax) * 0.5F;

            if (this.onGround) {
                this.xd *= 0.699999988079071D;
                this.zd *= 0.699999988079071D;
            }

            this.setSpriteFromAge(spriteProvider);
        }
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
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random) {
            return new SparkParticle(level, x, y, z, this.spriteProvider);
        }
    }
}
