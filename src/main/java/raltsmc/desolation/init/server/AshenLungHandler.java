package raltsmc.desolation.init.server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import raltsmc.desolation.Desolation;
import raltsmc.desolation.attachment.DesolationAttachments;
import raltsmc.desolation.config.DesolationConfig;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBiomeTags;
import raltsmc.desolation.tag.DesolationItemTags;

/**
 * The "Ashen Lung" meter. Standing in ash-laden air fills it; the meter — not the biome — is what
 * hands out Mining Fatigue and Weakness, and an air filter worn on the head stops it from filling
 * at the cost of the filter's durability.
 */
public final class AshenLungHandler {
    /** Effects are refreshed on this cadence so the per-tick path allocates nothing. */
    private static final int EFFECT_INTERVAL = 20;
    /** Comfortably longer than {@link #EFFECT_INTERVAL} so the debuff never visibly flickers. */
    private static final int EFFECT_DURATION = 80;
    /** Depth below sea level at which the ash cloud no longer reaches. */
    private static final int SHELTER_DEPTH = 10;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private AshenLungHandler() {
        return;
    }

    public static void serverTick(ServerPlayer player) {
        DesolationConfig config = Desolation.CONFIG;

        if (!config.ashenLungEnabled) {
            return;
        }

        ServerLevel world = player.level();
        int smoke = player.getAttachedOrElse(DesolationAttachments.SMOKE_INHALATION, 0);
        int updated = smoke;

        if (player.isSpectator() || player.isCreative()) {
            updated = 0;
        } else if (isInAshenAir(player, world)) {
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);

            if (isWorkingFilter(head)) {
                // The filter holds the meter where it is and wears out doing so.
                if (player.tickCount % config.ashenLungFilterDamageInterval == 0) {
                    head.hurtAndBreak(1, world, player, item -> player.onEquippedItemBroken(item, EquipmentSlot.HEAD));
                }
            } else {
                updated = smoke + intakeRate(player, world);
            }
        } else {
            updated = smoke - config.ashenLungRecoveryRate;
        }

        updated = Mth.clamp(updated, 0, config.ashenLungMaxSmoke);

        // Every write to a synced attachment is a packet, so only publish on the effect cadence (or
        // when the meter empties, which the HUD needs to see immediately).
        if (updated != smoke && (updated == 0 || player.tickCount % EFFECT_INTERVAL == 0)) {
            player.setAttached(DesolationAttachments.SMOKE_INHALATION, updated);
        }

        if (player.tickCount % EFFECT_INTERVAL == 0) {
            if (updated >= config.ashenLungMiningFatigueThreshold) {
                player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, EFFECT_DURATION, 0, true, false, true));
            }

            if (updated >= config.ashenLungWeaknessThreshold) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION, 0, true, false, true));
            }
        }
    }

    /**
     * A filter counts as working until its last point of durability, so a broken-in-hand filter never
     * silently keeps protecting the player.
     */
    private static boolean isWorkingFilter(ItemStack stack) {
        if (!stack.is(DesolationItemTags.AIR_FILTERS)) {
            return false;
        }

        return !stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    /**
     * The affected area is a biome tag rather than a namespace comparison, so datapacks decide where
     * the ash hangs in the air.
     */
    private static boolean isInAshenAir(ServerPlayer player, ServerLevel world) {
        if (player.getY() < world.getSeaLevel() - SHELTER_DEPTH) {
            return false;
        }

        return world.getBiome(player.blockPosition()).is(DesolationBiomeTags.CHARRED_FORESTS);
    }

    /** Base intake, doubled while wading through ash and doubled again during an ash storm. */
    private static int intakeRate(ServerPlayer player, ServerLevel world) {
        int rate = 1;
        BlockPos pos = player.blockPosition();
        BlockState feet = world.getBlockState(pos);
        BlockState ground = world.getBlockState(pos.below());

        if (isAsh(feet) || isAsh(ground)) {
            rate *= 2;
        }

        if (world.isRaining() && world.canSeeSky(pos)) {
            rate *= 2;
        }

        return rate;
    }

    private static boolean isAsh(BlockState state) {
        return state.is(DesolationBlocks.ASH_LAYER_BLOCK) || state.is(DesolationBlocks.ASH_BLOCK);
    }
}
