package raltsmc.desolation.mixin.entity.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.init.server.AshenLungHandler;
import raltsmc.desolation.init.server.CinderDashHandler;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void desolation$tickPlayerEntity(CallbackInfo info) {
        // Both features are server-authoritative; the client learns about them through synced
        // attachments and ordinary movement/particle packets.
        if ((Object) this instanceof ServerPlayer player) {
            AshenLungHandler.serverTick(player);
            CinderDashHandler.serverTick(player);
        }
    }
}
