package raltsmc.desolation.attachment;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import raltsmc.desolation.Desolation;

/**
 * Per-player state owned by the server. Only the two values the HUD needs are synced, and only to
 * the player they belong to; everything else stays server-side so a modified client cannot see or
 * forge it.
 */
@SuppressWarnings("SameParameterValue")
public final class DesolationAttachments {
    /** "Ashen Lung" meter: how much ash-laden air the player has breathed in. Drives the HUD. */
    public static final AttachmentType<Integer> SMOKE_INHALATION = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0)
            .persistent(Codec.INT)
            .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly())
            .buildAndRegister(of("smoke_inhalation"));

    /** Remaining Cinder Dash charges. Drives the HUD, so it is synced to the owner. */
    public static final AttachmentType<Integer> DASH_CHARGES = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0)
            .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly())
            .buildAndRegister(of("dash_charges"));

    /** Ticks accumulated towards the next Cinder Dash charge. Server-only. */
    public static final AttachmentType<Integer> DASH_RECHARGE = AttachmentRegistry.createDefaulted(of("dash_recharge"), () -> 0);

    /** Ticks left in the dash currently being applied by the server. Server-only. */
    public static final AttachmentType<Integer> DASH_ACTIVE_TICKS = AttachmentRegistry.createDefaulted(of("dash_active_ticks"), () -> 0);

    /** Direction the active dash pushes the player in, captured once when the dash starts. Server-only. */
    public static final AttachmentType<Vec3> DASH_DIRECTION = AttachmentRegistry.createDefaulted(of("dash_direction"), () -> Vec3.ZERO);

    /** Token bucket that caps how many dash requests a client may have accepted. Server-only. */
    public static final AttachmentType<Integer> DASH_PACKET_BUDGET = AttachmentRegistry.createDefaulted(of("dash_packet_budget"), () -> 0);

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationAttachments() {
        return;
    }

    private static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(Desolation.MOD_ID, path);
    }

    public static void init() { }
}
