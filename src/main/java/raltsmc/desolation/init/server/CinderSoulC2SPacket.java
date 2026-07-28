package raltsmc.desolation.init.server;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import raltsmc.desolation.Desolation;

/**
 * The single Cinder Soul intent a client is allowed to state. Everything else the ability does —
 * cooldown, charges, movement, particles, sound — is decided by the server; see
 * {@link CinderDashHandler}.
 */
public record CinderSoulC2SPacket(Action action) implements CustomPacketPayload {
    public enum Action {
        DASH;

        private static final Action[] VALUES = values();

        /** Bounds-checked so a hand-crafted packet cannot blow up the netty decoder. */
        static Action byOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length) {
                throw new DecoderException("Unknown Cinder Soul action id: " + ordinal);
            }

            return VALUES[ordinal];
        }
    }

    public static final CustomPacketPayload.Type<CinderSoulC2SPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "cinder_soul"));
    public static final StreamCodec<ByteBuf, CinderSoulC2SPacket> CODEC = ByteBufCodecs.VAR_INT
            .map(ordinal -> new CinderSoulC2SPacket(Action.byOrdinal(ordinal)), packet -> packet.action().ordinal());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
