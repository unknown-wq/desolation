package raltsmc.desolation.init.server;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import raltsmc.desolation.attachment.DesolationAttachments;

public class DesolationServerNetworking {
    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationServerNetworking() {
        return;
    }

    public static void init() {
        // Attachment types must exist on both sides before anything can be synced, and this runs from
        // the common initializer.
        DesolationAttachments.init();

        PayloadTypeRegistry.serverboundPlay().register(CinderSoulC2SPacket.ID, CinderSoulC2SPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CinderSoulC2SPacket.ID, (payload, context) -> context.server().execute(() -> {
            switch (payload.action()) {
                case DASH -> CinderDashHandler.onDashRequested(context.player());
            }
        }));
    }
}
