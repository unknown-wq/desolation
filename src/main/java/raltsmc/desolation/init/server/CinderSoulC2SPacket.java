package raltsmc.desolation.init.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import raltsmc.desolation.Desolation;

public record CinderSoulC2SPacket(TYPE action) implements CustomPacketPayload {
    public enum TYPE {
        DASH,
        READY,
        TICK
    }

    public static final CustomPacketPayload.Type<CinderSoulC2SPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "cinder_soul"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CinderSoulC2SPacket> CODEC = StreamCodec.ofMember(CinderSoulC2SPacket::write, CinderSoulC2SPacket::new);

    public CinderSoulC2SPacket(RegistryFriendlyByteBuf buf) {
        this(TYPE.valueOf(buf.readUtf()));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.action.toString());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
