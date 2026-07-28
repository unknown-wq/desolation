package raltsmc.desolation.init.server;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.attachment.DesolationAttachments;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationStatusEffects;

import java.util.List;

/**
 * Server-side Cinder Dash. The client only ever states an intent; charges, cooldown, the movement
 * itself, the trail particles and the ambient effects all live here, so a modified client gains
 * nothing by lying and an anti-cheat sees a server-issued velocity instead of a client one.
 */
public final class CinderDashHandler {
    /** How many accepted dash requests a client may bank up. */
    private static final int PACKET_BUDGET_MAX = 4;
    /** One token is handed back per this many ticks. */
    private static final int PACKET_BUDGET_REFILL_TICKS = 20;
    /** Cadence of the idle ember trail carried by anyone with Cinder Soul. */
    private static final int AMBIENT_INTERVAL = 5;
    /** Reach of the dash's burning collision sweep, in blocks around the player's hitbox. */
    private static final double SWEEP_REACH = 0.6D;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private CinderDashHandler() {
        return;
    }

    /**
     * Handles a dash intent from a client. Everything a modified client could get wrong is checked
     * here: the effect has to be present, the token bucket has to allow the request, and a charge
     * has to be available.
     */
    public static void onDashRequested(ServerPlayer player) {
        DesolationConfig config = Desolation.CONFIG;

        if (!config.dashEnabled || !player.isAlive() || player.isSpectator()) {
            return;
        }

        if (!player.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
            return;
        }

        int budget = player.getAttachedOrElse(DesolationAttachments.DASH_PACKET_BUDGET, PACKET_BUDGET_MAX);

        if (budget <= 0) {
            return;
        }

        player.setAttached(DesolationAttachments.DASH_PACKET_BUDGET, budget - 1);

        if (player.getAttachedOrElse(DesolationAttachments.DASH_ACTIVE_TICKS, 0) > 0) {
            return;
        }

        int charges = player.getAttachedOrElse(DesolationAttachments.DASH_CHARGES, 0);

        if (charges <= 0) {
            return;
        }

        ServerLevel world = player.level();

        player.setAttached(DesolationAttachments.DASH_CHARGES, charges - 1);
        player.setAttached(DesolationAttachments.DASH_ACTIVE_TICKS, config.dashDurationTicks);
        player.setAttached(DesolationAttachments.DASH_DIRECTION, player.getLookAngle().normalize().scale(config.dashSpeed));

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1F, 1.6F);
        world.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0D, player.getZ(), 24, 0.3D, 0.5D, 0.3D, 0.05D);
    }

    /** Runs once per server player tick from the player mixin. */
    public static void serverTick(ServerPlayer player) {
        DesolationConfig config = Desolation.CONFIG;

        if (!config.dashEnabled) {
            return;
        }

        if (!player.hasEffect(DesolationStatusEffects.CINDER_SOUL_HOLDER)) {
            clear(player, config);
            return;
        }

        if (player.tickCount % PACKET_BUDGET_REFILL_TICKS == 0) {
            int budget = player.getAttachedOrElse(DesolationAttachments.DASH_PACKET_BUDGET, PACKET_BUDGET_MAX);

            if (budget < PACKET_BUDGET_MAX) {
                player.setAttached(DesolationAttachments.DASH_PACKET_BUDGET, budget + 1);
            }
        }

        tickRecharge(player, config);
        tickActiveDash(player, config);
        tickAmbient(player);
    }

    private static void tickRecharge(ServerPlayer player, DesolationConfig config) {
        int charges = player.getAttachedOrElse(DesolationAttachments.DASH_CHARGES, 0);

        if (charges >= config.dashMaxCharges) {
            return;
        }

        int recharge = player.getAttachedOrElse(DesolationAttachments.DASH_RECHARGE, 0) + 1;

        if (recharge < config.dashRechargeTicks) {
            player.setAttached(DesolationAttachments.DASH_RECHARGE, recharge);
            return;
        }

        ServerLevel world = player.level();

        player.setAttached(DesolationAttachments.DASH_RECHARGE, 0);
        player.setAttached(DesolationAttachments.DASH_CHARGES, charges + 1);

        // A single batched call: the old "ready" burst spent 151 separate particle packets per hit.
        world.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0D, player.getZ(), 60, 0.45D, 0.6D, 0.45D, 0.02D);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1F, 1.2F);
    }

    private static void tickActiveDash(ServerPlayer player, DesolationConfig config) {
        int active = player.getAttachedOrElse(DesolationAttachments.DASH_ACTIVE_TICKS, 0);

        if (active <= 0) {
            return;
        }

        ServerLevel world = player.level();
        Vec3 direction = player.getAttachedOrElse(DesolationAttachments.DASH_DIRECTION, Vec3.ZERO);

        // The server owns the movement and tells the client about it, rather than trusting a client
        // that moved itself.
        player.setDeltaMovement(direction);
        player.hurtMarked = true;
        player.resetFallDistance();
        player.connection.send(new ClientboundSetEntityMotionPacket(player));

        world.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.8D, player.getZ(), 4, 0.2D, 0.3D, 0.2D, 0.01D);

        burnEntitiesInPath(player, world, config);
        player.setAttached(DesolationAttachments.DASH_ACTIVE_TICKS, active - 1);
    }

    /**
     * Anything the dash passes through is struck and set alight. The ignite mirrors the Cinder Soul
     * melee behaviour in {@code EnchantmentHelperMixin}; vanilla invulnerability frames keep a single
     * dash from hitting the same target repeatedly.
     */
    private static void burnEntitiesInPath(ServerPlayer player, ServerLevel world, DesolationConfig config) {
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(SWEEP_REACH),
                target -> target != player && target.isAlive() && target.invulnerableTime <= 0);

        for (LivingEntity target : targets) {
            if (config.dashDamage > 0.0D) {
                target.hurtServer(world, player.damageSources().playerAttack(player), (float) config.dashDamage);
            }

            if (config.dashIgniteSeconds > 0) {
                target.igniteForSeconds(config.dashIgniteSeconds);
            }
        }
    }

    /**
     * The idle embers used to be requested by the client roughly six times a second; the server now
     * spawns them on a fixed cadence instead.
     */
    private static void tickAmbient(ServerPlayer player) {
        if (player.tickCount % AMBIENT_INTERVAL != 0) {
            return;
        }

        ServerLevel world = player.level();

        world.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.35D, player.getZ(), 2, 0.25D, 0.25D, 0.25D, 0.02D);

        if (player.tickCount % (AMBIENT_INTERVAL * 16) == 0) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT, .8F, 1F);
        }
    }

    /** Losing the effect refills the charges so the next dose starts ready, and cancels any dash. */
    private static void clear(ServerPlayer player, DesolationConfig config) {
        if (player.getAttachedOrElse(DesolationAttachments.DASH_ACTIVE_TICKS, 0) != 0) {
            player.setAttached(DesolationAttachments.DASH_ACTIVE_TICKS, 0);
        }

        if (player.getAttachedOrElse(DesolationAttachments.DASH_RECHARGE, 0) != 0) {
            player.setAttached(DesolationAttachments.DASH_RECHARGE, 0);
        }

        if (player.getAttachedOrElse(DesolationAttachments.DASH_CHARGES, 0) != config.dashMaxCharges) {
            player.setAttached(DesolationAttachments.DASH_CHARGES, config.dashMaxCharges);
        }
    }
}
