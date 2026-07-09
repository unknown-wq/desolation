package raltsmc.desolation.init.server;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DesolationServerNetworking {
    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationServerNetworking() {
        return;
    }

    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(CinderSoulC2SPacket.ID, CinderSoulC2SPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CinderSoulC2SPacket.ID, (payload, context) -> context.server().execute(() -> {
            ServerPlayer player = context.player();
            ServerLevel world = player.level();
            Random random = ThreadLocalRandom.current();

            switch (payload.action()) {
                case DASH -> {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1F, 1.6F);
                }
                case READY -> {
                    List<Vec3> points = new ArrayList<>();

                    double phi = Math.PI * (3. - Math.sqrt(5.));
                    for (int i = 0; i <= 150; ++i) {
                        double y = 1 - (i / (float) (250 - 1)) * 2;
                        double radius = Math.sqrt(1 - y * y);
                        double theta = phi * i;
                        double x = Math.cos(theta) * radius;
                        double z = Math.sin(theta) * radius;

                        points.add(new Vec3(player.getX() + x * 0.5, player.getY() + 1 + y, player.getZ() + z * 0.5));
                    }

                    for (Vec3 vec : points) {
                        Vec3 vel = vec.subtract(player.position())
                                .normalize()
                                .scale(0.12 + random.nextDouble() * 0.03)
                                .add(player.getDeltaMovement().multiply(1, 0.1, 1))
                                .multiply(1.25, 1, 1.25);
                        world.sendParticles(ParticleTypes.FLAME, vec.x, vec.y, vec.z, 1, vel.x, vel.y, vel.z, .1);
                    }
                }
                case TICK -> {
                    double d = player.getX() - 0.25D + random.nextDouble() / 2;
                    double e = player.getY();
                    double f = player.getZ() - 0.25D + random.nextDouble() / 2;

                    double g = random.nextDouble() * 0.6D - 0.3D;
                    double h = random.nextDouble() * 6.0D / 16.0D;
                    double i = (random.nextDouble() - 0.5D) / 5.0D;

                    world.sendParticles(ParticleTypes.FLAME, d + g, e + h, f + g, 1, 0d, 0.1d + i, 0d, .1);
                    if (random.nextDouble() < 0.25) {
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT, .8F, 1F);
                    }
                }
            }
        }));
    }
}
